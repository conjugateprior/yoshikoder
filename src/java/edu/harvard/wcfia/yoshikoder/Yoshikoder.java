package edu.harvard.wcfia.yoshikoder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.DuplicateException;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenCache;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationCache;
import edu.harvard.wcfia.yoshikoder.ui.DictionaryPanel;
import edu.harvard.wcfia.yoshikoder.ui.DocumentPanel;
import edu.harvard.wcfia.yoshikoder.ui.FatalErrorPanel;
import edu.harvard.wcfia.yoshikoder.ui.ListConcordancePanel;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.ImportUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class Yoshikoder extends JFrame {
    
    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.Yoshikoder"); 
    
    // model
    protected YKProject project;
    
    // components
    protected ListConcordancePanel concPanel;
    protected DictionaryPanel dictPanel;
    protected DocumentPanel docPanel;
    
    // properties
    protected Color highlightColor;
    protected boolean unsavedChanges;
    protected TokenizationCache tokenizationCache;
    
    // actions
    // file menu
    private Action newProjectAction = new NewProjectAction(this);
    private Action openProjectAction = new OpenProjectAction(this);
    private Action saveProjectAction = new SaveProjectAction(this);
    private Action saveAsProjectAction = new SaveProjectAsAction(this);
    //private Action importVBProAction = new ImportVBProAction(this);
    private Action importYKAction = new OpenDictionaryAction(this);
    private Action exportProjectAction = new ExportProjectAsHtmlAction(this);
    
    // dictionary menu
    private Action addCategoryAction = new AddCategoryAction(this);
    private Action addPatternAction = new AddPatternAction(this);
    private Action removeNodeAction = new RemoveNodeAction(this);
    private Action editNodeAction = new EditNodeAction(this);
    
    private Action addWordsToCategoryAction = new AddWordsToCategoryAction(this);
    
    private Action saveAsDictionaryAction = new SaveAsDictionaryAction(this);
    private Action exportDictionaryAsHtmlAction = new ExportDictionaryAsHtml(this);
    
    // highlight menu
    private Action addHighlightsAction = new AddHighlightsAction(this);
    private Action removeHighlightsAction = new RemoveHighlightsAction(this);
    private Action setHighlightColorAction = new SetHighlightColorAction(this);
    
    // concordance menu
    private Action makeConcordanceAction = new MakeConcordanceAction(this); 
    private Action openConcordanceAction = new OpenConcordanceAction(this);
    private Action saveConcordanceAction = new SaveConcordanceAction(this);
    private Action exportConcordanceAsHtmlAction = new ExportConcordanceAsHtmlAction(this);
    private Action exportConcordanceAsExcelAction = new ExportConcordanceAsExcelAction(this);
    private Action multipleConcordanceFrequencyReportAction = new MultipleConcordanceFrequencyReportAction(this);
    
    // document menu
    private Action addDocumentAction = new AddDocumentAction(this);
    private Action editDocumentAction = new EditDocumentAction(this);
    private Action removeDocumentAction = new RemoveDocumentAction(this);
    private Action importDocumentAction = new ImportDocumentAction(this);
    private Action exportDocumentAsUTF8Action = new ExportDocumentAsUTF8Action(this);
    private Action exportDocumentAsUTF16Action = new ExportDocumentAsUTF16Action(this);
    
    // report menu
    private Action dictionaryReportAction = new SingleDocumentDictionaryReportAction(this);
    private Action documentReportAction = new SingleDocumentWordFrequencyReportAction(this);
    //private Action comparisonReportAction = new DictionaryComparisonReportAction(this);
    private Action statisticalComparisonReportAction = 
        new DictionaryRRDocumentComparisonAction(this);
    private Action concordanceReportAction = new ConcordanceFrequencyReportAction(this);
    //private Action allDocumentsReportAction = 
    //    new MultipleDocumentFrequencyReportAction(this);
    private Action duplicateReportAction = new DuplicateReportAction(this);
    private Action unifiedDocumentReport = new UnifiedWordFrequencyReportAction(this);
    private Action unifiedDictionaryReport = new UnifiedDictionaryFrequencyReportAction(this);
    
    // help menu
    private Action consoleAction = new ShowConsoleAction(this);
    private Action licenseAction = new ShowLicenseAction(this);
    
    // may overridden by platform-specific subclasses
    protected Action aboutAction;
    protected Action helpAction;
    protected Action finishAction;
    protected Action preferencesAction = new PreferencesAction(this);
    
    private Charset defaultEncoding = Charset.defaultCharset();
    private Locale defaultLocale = Locale.getDefault();
    private int windowSize = 6;
    
    public Yoshikoder() {
        super("Yoshikoder"); 
        
        Preferences preferences = Preferences.userNodeForPackage(Yoshikoder.class);
        String charsetName = preferences.get("default.charset", Charset.defaultCharset().name());
        setDefaultEncoding(Charset.forName(charsetName));
        String localeName = preferences.get("default.locale", Locale.getDefault().toString());
        setDefaultLocale(FileUtil.parseLocale(localeName));
        Integer win = preferences.getInt("default.windowsize", 5);
        log.info("windowsize from prefs in constructor:" + win);
        setWindowSize(win);
        
        project = getLastProject();
        setTitle("Yoshikoder Project: " + getProjectFileName());
        
        highlightColor = Color.yellow;
        tokenizationCache = new TokenCache();
        makeGUI();
    }
    
    public void editDocument(){
    	boolean changed = docPanel.editDocument();
    	if (changed) 
    		setUnsavedChanges(true);
    }
    
    public Charset getDefaultEncoding() {
		return defaultEncoding;
	}
    
    public void setDefaultEncoding(Charset encoding) {
		defaultEncoding = encoding;
	}
    
    public Locale getDefaultLocale() {
		return defaultLocale;
	}
    
    public void setDefaultLocale(Locale loc) {
		defaultLocale = loc;
	}
    
    
    public boolean hasUnsavedChanges(){
        return unsavedChanges;
    }

    public void setUnsavedChanges(boolean b){
    	if (FileUtil.isMac())
    		getRootPane().putClientProperty("Window.documentModified", b);
    	unsavedChanges = b;
    }
    
    public DocumentList getDocumentList(){
        return project.getDocumentList();
    }
    
    public YKDictionary getDictionary() {
        return project.getDictionary();
    }
    
    public void setDictionary(YKDictionary dict){
        project.setDictionary(dict);
        // update ui
        dictPanel.setDictionary(dict);
    }
    
    public Font getDisplayFont() {
        return project.getDisplayFont();
    }
    
    public void setDisplayFont(Font f){
        project.setDisplayFont(f);
        // update ui
        concPanel.setDisplayFont(f);
        dictPanel.setDisplayFont(f);
        docPanel.setDisplayFont(f);
    }
    
    protected String getProjectFileName(){
    	File f = project.getLocation();
    	if (f != null)
    		return f.getAbsolutePath();
    	else
    		return "not saved";
    }
    
    public YKProject getProject() {
        return project;
    }
        
    public void setProject(YKProject proj) {
        getTokenizationCache().clear();
        setUnsavedChanges(false);
        
    	project = proj;     
        Font pfont = project.getDisplayFont();
        // update ui
        docPanel.setDocumentList(project.getDocumentList());
        docPanel.setDisplayFont(pfont);                
        dictPanel.setDictionary(project.getDictionary());
        dictPanel.setDisplayFont(pfont);
        concPanel.setConcordance(null);
        concPanel.setDisplayFont(pfont);
    
        setTitle("Yoshikoder Project: " + getProjectFileName());
    }

    public Color getHighlightColor(){
        return highlightColor;
    }
    
    public void setHighlightColor(Color col){
        highlightColor = col;
    }
    
    public Concordance getConcordance(){
        return concPanel.getConcordance();
    }
    
    public void setConcordance(Concordance conc){
        concPanel.setConcordance(conc);
    }
   
    public YKDocument getSelectedDocument(){
        return docPanel.getSelectedDocument();
    }
    
    public YKDocument[] getSelectedDocuments(){
        return docPanel.getSelectedDocuments();
    }
    
    public void setSelectedDocument(YKDocument doc){
        docPanel.setSelectedDocument(doc);
    }
    
    public Node getSelectedNode(){
        return dictPanel.getSelectedNode();
    }
    
    public void setSelectedNode(Node n){
        dictPanel.setSelectedNode(n);
    }
        
    public int getWindowSize() {
        return windowSize;
    	//return project.getDictionary().getWindowSize();
    }
    
    public void setWindowSize(int ws) {
        windowSize = ws;
    	//project.getDictionary().setWindowSize(ws);
    }
   
    public TokenizationCache getTokenizationCache(){
        return tokenizationCache;
    }
    
    public void setTokenizationCache(TokenizationCache tc){
        tokenizationCache = tc;
    }
    
    // basic actions
    
    public void addHighlights(TokenList tl){
        docPanel.addHighlights(tl, highlightColor);
    }
    
    public void removeHighlights(){
        docPanel.removeHighlights();
    }

    protected FileDialog projectSavingDialog;
    public void saveProject(boolean saveAs) throws IOException {
    	if (projectSavingDialog == null){
            projectSavingDialog = DialogUtil.makeFileDialog(Yoshikoder.this, "Save Project", FileDialog.SAVE, null);
            projectSavingDialog.setFile(null);
    	}
    	
    	File pf = getProject().getLocation();
    	
        if (pf != null && saveAs){
            projectSavingDialog.setVisible(true);
            String file = projectSavingDialog.getFile();
            if (file != null){
                File asFile = new File(projectSavingDialog.getDirectory(), FileUtil.suffix(file, "ykp"));
                project.saveAsXml(asFile);
                // don't alter location
            } else {
                return;
            }
            
        } else if (pf != null && !saveAs){
        	project.saveAsXml(pf);
        
        } else if (pf == null && saveAs){
        	projectSavingDialog.setVisible(true);
            String file = projectSavingDialog.getFile();
            if (file != null){
                File asFile = new File(projectSavingDialog.getDirectory(), FileUtil.suffix(file, "ykp"));
                project.saveAsXml(asFile);
                // *don't* alter (lack of) location
            } else {
                return;
            }
        } else if (pf == null && !saveAs){
        	projectSavingDialog.setVisible(true);
            String file = projectSavingDialog.getFile();
            if (file != null){
                File asFile = new File(projectSavingDialog.getDirectory(), FileUtil.suffix(file, "ykp"));
                project.setLocation(asFile); // alter location
                project.saveAsXml(asFile);  
                setUnsavedChanges(false);
                setTitle("Yoshikoder Project: " + getProjectFileName());
            } else {
                return;
            }
        }
    }
    
    public void addDocument(YKDocument doc){
        project.addDocument(doc);
        // update ui
        docPanel.addDocument(doc);
    }
    
    public void removeDocument(YKDocument doc){
        log.info("documentlist before removal" + project.getDocumentList().toString());
        project.removeDocument(doc);
        // update ui
        docPanel.removeDocument(doc);
        tokenizationCache.removeTokenList(doc);
        log.info("documentlist after removal " + project.getDocumentList().toString());
    }
    
    public void addCategory(CategoryNode cat, CategoryNode parent) throws DuplicateException {
        project.getDictionary().addCategory(cat, parent);
    }
    
    public void addPattern(PatternNode pat, CategoryNode parent) throws DuplicateException {
        project.getDictionary().addPattern(pat, parent);
    }
    
    public void replaceNode(Node node, Node replacement) throws DuplicateException{
        project.getDictionary().replace(node, replacement);
    }
    
    public void removeNode(Node n){
        project.getDictionary().remove(n);
    }
   
    protected YKProject getLastProject(){
        Preferences preferences = 
            Preferences.userNodeForPackage(Yoshikoder.class);
        
        String lastProject = preferences.get("last.project", null);
        log.info("last.project is " + lastProject);
        if (lastProject != null){
            File f = new File(lastProject);
            if (f.exists()){
                try {
                    YKProject proj = ImportUtil.importYKProject(f);
                    return proj;
                } catch (Exception ioe){
                    log.log(Level.WARNING, "Could not load " + lastProject, ioe);
                }
            } else {
                log.warning(lastProject + "does not exist");
            }
        }
        
        log.info("returning new fresh project");
        return new YKProject();
    }
    
    protected void platformSpecificSetup(){
    	setupWindows();
    }
    
    protected void setupWindows(){
        finishAction = new ExitAction(this);
        aboutAction = new WindowsAboutAction(this);
    }
    
    protected JMenu makeFileMenu(){
        JMenu fileMenu = DialogUtil.makeMenu("Yoshikoder.fileMenu"); 
        fileMenu.add(newProjectAction);
        fileMenu.add(openProjectAction);
        fileMenu.addSeparator();
        fileMenu.add(saveProjectAction);
        fileMenu.add(saveAsProjectAction);
        JMenu em = DialogUtil.makeMenu("Yoshikoder.projectExportMenu");
        em.add(exportProjectAction);
        fileMenu.add(em);
        fileMenu.addSeparator();
        fileMenu.add(preferencesAction);
        
        // these two get removed again in the OSX subclass
        fileMenu.addSeparator();
        fileMenu.add(finishAction);
        	
        return fileMenu;
    }
    
    protected JMenu makeDictionaryMenu(){
        JMenu dictMenu = DialogUtil.makeMenu("Yoshikoder.dictionaryMenu");
        dictMenu.add(addCategoryAction);
        dictMenu.add(addPatternAction);
        dictMenu.add(addWordsToCategoryAction);
        dictMenu.add(editNodeAction);
        dictMenu.add(removeNodeAction);
        dictMenu.addSeparator();
        dictMenu.add(importYKAction); 
        dictMenu.add(saveAsDictionaryAction);
        dictMenu.addSeparator();
        //JMenu m = DialogUtil.makeMenu("Yoshikoder.dictionaryImportMenu");   
        //m.add(importVBProAction);
        //dictMenu.add(m);
        JMenu m = DialogUtil.makeMenu("Yoshikoder.dictionaryExportMenu");
        m.add(exportDictionaryAsHtmlAction);
        dictMenu.add(m);
        return dictMenu;
    }
    
    protected  JMenu makeDocumentMenu(){
        JMenu docMenu = DialogUtil.makeMenu("Yoshikoder.documentMenu"); 
        docMenu.add(addDocumentAction);
        docMenu.add(editDocumentAction);
        docMenu.add(importDocumentAction);
        docMenu.add(removeDocumentAction);
        docMenu.addSeparator();
        JMenu em = DialogUtil.makeMenu("Yoshikoder.documentExportMenu");
        em.add(exportDocumentAsUTF8Action);
        em.add(exportDocumentAsUTF16Action);
        docMenu.add(em);
        return docMenu;
    }
    
    protected JMenu makeConcordanceMenu(){
        JMenu concMenu = DialogUtil.makeMenu("Yoshikoder.concordanceMenu"); 
        concMenu.add(makeConcordanceAction);
        concMenu.add(openConcordanceAction);
        concMenu.addSeparator();
        concMenu.add(saveConcordanceAction);
        JMenu ce = DialogUtil.makeMenu("Yoshikoder.concordanceExportMenu");
        ce.add(exportConcordanceAsHtmlAction);
        ce.add(exportConcordanceAsExcelAction);
        concMenu.add(ce);
        return concMenu;
    }
    
    protected JMenu makeHighlightMenu(){
        JMenu highMenu = DialogUtil.makeMenu("Yoshikoder.highlightMenu");
        highMenu.add(addHighlightsAction);
        highMenu.add(removeHighlightsAction);
        highMenu.addSeparator();
        highMenu.add(setHighlightColorAction);
        return highMenu;
    }
    
    protected JMenu makeReportMenu(){
        JMenu reportMenu = DialogUtil.makeMenu("Yoshikoder.reportMenu"); 
        
        //reportMenu.addSeparator();
        JMenu dictMenu = new JMenu("Apply Dictionary");
        reportMenu.add(dictMenu);
        dictMenu.add(dictionaryReportAction);
        dictMenu.add(unifiedDictionaryReport);
        dictMenu.add(statisticalComparisonReportAction);
        //dictMenu.add(comparisonReportAction);
        dictMenu.addSeparator();
        dictMenu.add(concordanceReportAction);
        dictMenu.add(multipleConcordanceFrequencyReportAction);
        //reportMenu.add(wordscoringAction);
        
        JMenu freqMenu = new JMenu("Count Words");
        reportMenu.add(freqMenu);
        freqMenu.add(documentReportAction);
        //reportMenu.add(allDocumentsReportAction);
        freqMenu.add(unifiedDocumentReport);
        
        reportMenu.addSeparator();
        reportMenu.add(duplicateReportAction);
        
        return reportMenu;
    }
    
    protected JMenu makeHelpMenu(){
        JMenu helpMenu = DialogUtil.makeMenu("Yoshikoder.helpMenu"); 
        //if (FileUtil.isMac())
        //    helpAction = new MacHelpAction(this);
        //else 
        helpAction = new HelpAction(this);     
        helpMenu.add(helpAction);
        helpMenu.add(consoleAction);
        helpMenu.addSeparator();
        helpMenu.add(licenseAction);
        helpMenu.addSeparator();
        helpMenu.add(aboutAction); 
        
        return helpMenu;
    }
    
    protected JToolBar makeToolbar(){
        JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.add(DialogUtil.makeToolbarButton(addCategoryAction, "Toolbar.addCategory.icon"));
        toolbar.add(DialogUtil.makeToolbarButton(addPatternAction, "Toolbar.addPattern.icon"));
        toolbar.add(DialogUtil.makeToolbarButton(removeNodeAction, "Toolbar.removeNode.icon"));
        toolbar.addSeparator();
        toolbar.add(DialogUtil.makeToolbarButton(addHighlightsAction, "Toolbar.addHighlights.icon"));
        toolbar.add(DialogUtil.makeToolbarButton(makeConcordanceAction, "Toolbar.makeConcordance.icon"));
        toolbar.addSeparator();
        toolbar.add(DialogUtil.makeToolbarButton(addDocumentAction, "Toolbar.addDocument.icon"));
        toolbar.add(DialogUtil.makeToolbarButton(importDocumentAction, "Toolbar.importDocument.icon"));
        return toolbar;
    }
    
    protected MouseListener makePopupListener(){
        final JPopupMenu popup = new JPopupMenu();
        popup.add(addCategoryAction);
        popup.add(addPatternAction);
        popup.add(editNodeAction);
        popup.add(addWordsToCategoryAction);
        popup.addSeparator();
        popup.add(removeNodeAction);
        popup.addSeparator();
        popup.add(addHighlightsAction);
        popup.add(makeConcordanceAction);
        
        MouseAdapter adapt = new MouseAdapter() {
            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger())
                    popup.show(e.getComponent(), e.getX(), e.getY());
            }
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }
        };
        return adapt;
    }
    
    protected void makeGUI() {
        
        // components
        Font pfont = project.getDisplayFont();
        dictPanel = new DictionaryPanel(project.getDictionary());
        dictPanel.setDisplayFont(pfont);
        docPanel = new DocumentPanel(this, project.getDocumentList());
        docPanel.setDisplayFont(pfont);
        concPanel = new ListConcordancePanel();
        concPanel.setDisplayFont(pfont);
        
        platformSpecificSetup();
        // actions
        //if (FileUtil.isMac())
        //    setupMac();
        //else if (FileUtil.isWindows())
        //    setupWindows();
        //else
        //   setupOther();

        // menus (more platform specific stuff here maybe)
        JMenuBar bar = new JMenuBar();
        bar.add(makeFileMenu());
        bar.add(makeDictionaryMenu());
        bar.add(makeDocumentMenu());        
        bar.add(makeHighlightMenu());
        bar.add(makeConcordanceMenu());
        bar.add(makeReportMenu());
        bar.add(makeHelpMenu());
        setJMenuBar(bar);
        
        // the right click popup menu
        MouseListener popupListener = makePopupListener();
        dictPanel.getTree().addMouseListener(popupListener); // why!
        
        // toolbar
        JToolBar toolbar = makeToolbar();        
        
        // page layout
        JSplitPane split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split1.setBorder(null);
        dictPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        docPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        split1.setLeftComponent(dictPanel);
        split1.setRightComponent(docPanel);
        split1.setDividerSize(2);
        
        JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split2.setTopComponent(split1);
        concPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        split2.setBottomComponent(concPanel);
        split2.setDividerSize(2);
        split2.setDividerLocation(0.5);
        
        // arrangement
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolbar, BorderLayout.NORTH);
        getContentPane().add(split2, BorderLayout.CENTER);
        pack();
        
        // screen placement
        Dimension pref = getPreferredSize();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - pref.width / 2, d.height / 2 - pref.height / 2);

        // on quit
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                finishAction.actionPerformed(null);
            }
        });
    }
    
    public static void main(String[] args) {      
        //YKFS ykfs = YKFS.getYKFS(); // prepares file system local storage as side effect

        try {
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        	log.log(Level.WARNING, "could not set appropriate look and feel", e);
        }

        Yoshikoder yk = null;
        try {
        	if (FileUtil.isMac())
        		yk = new YoshikoderOSX();
        	else
        		yk = new Yoshikoder();
        	
        	yk.setVisible(true);
        
        } catch (Throwable e) {	
        	StringBuffer sb = new StringBuffer();
        	sb.append(Messages.getString("Yoshikoder.uncaughtErrorMessage"));
        	StringWriter sw = new StringWriter();
        	e.printStackTrace(new PrintWriter(sw)); // show the user
        	String message = sb.toString();            
        	log.log(Level.SEVERE, message, e);

        	message = message.replaceAll("\n", "<br>\n"); 
        	FatalErrorPanel panel = new FatalErrorPanel(message);
        	JOptionPane.showMessageDialog(yk, panel, 
        			Messages.getString("Yoshikoder.uncaughtError.0"), 
        			JOptionPane.PLAIN_MESSAGE);

        	System.exit(1);
        } finally {
        	//
        }
    }
}
