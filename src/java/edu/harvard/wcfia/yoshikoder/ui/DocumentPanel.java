package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.DocumentListImpl;
import edu.harvard.wcfia.yoshikoder.document.LazyYKDocument;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.YKDocumentFactory;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Location;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenListImpl;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationCache;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationService;
import edu.harvard.wcfia.yoshikoder.ui.model.DocumentListModel;
import edu.harvard.wcfia.yoshikoder.util.CharsetWrapper;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.LocaleWrapper;
import edu.harvard.wcfia.yoshikoder.util.Messages;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class DocumentPanel extends JPanel {
    
    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.ui.DocumentPanel");
    
    protected String lostText = Messages.getString("DocumentPanel.lostDocument");
    
    protected TaskWorker tworker;
    
    protected Highlighter h;
    protected DefaultHighlighter.DefaultHighlightPainter myHighlightPainter;
    
    protected DocumentList documentList;
    protected JTextArea area;
    
    protected DefaultListModel model;
    protected JList docList;
    
    protected Font displayFont;
    protected Color currentColor;
    
    protected JLabel currentDocument = new JLabel();
    protected Yoshikoder yoshikoder;
    
    protected ListSelectionListener listListener = new ListSelectionListener(){    
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) 
                return;
            if (docList.getSelectedIndex() != -1){
                updateView();
                log.info("updating view in listListener");
            } else {
                area.setText("");
                currentDocument.setText("");
            }
        }
    };

    public DocumentPanel(Yoshikoder yk, DocumentList dl){
        super(new GridBagLayout());

        yoshikoder = yk;
        
        area = new JTextArea(10, 40);
        area.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        
        displayFont = area.getFont(); // just take the default
        currentColor = Color.yellow;
        
        docList = new JList(){
            public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                if (index != -1){
                    DocumentState state = (DocumentState)model.getElementAt(index);
                    return state.getTooltip();
                } else {
                    return null;
                }
            }
        };
        docList.addListSelectionListener(listListener);
        setDocumentList(dl);
        docList.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        h = area.getHighlighter();
        myHighlightPainter = 
            new DefaultHighlighter.DefaultHighlightPainter( currentColor );
        
        // arrangement
        //JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //JLabel topLabel = new JLabel(Messages.getString("DocumentPanel.topLabel"));
        //topLabel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        
        currentDocument.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 0.8;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 0, 5);
        add(currentDocument, c);
        
        JLabel lab2 = new JLabel("Documents");
        lab2.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.gridx = 1;
        c.weightx = 0.2;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 0, 0);
        add(lab2, c);

        JScrollPane pa = new JScrollPane(area);
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 1;
        c.gridx = 0;
        c.weightx = 0.8;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 5);
        add(pa, c);
        
        JScrollPane sp = new JScrollPane(docList);
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 1;
        c.gridx = 1;
        c.weightx = 0.2;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 0);
        add(sp, c);
        
    }
    
    protected void setHighlightColor(Color col){
        if (!col.equals(myHighlightPainter.getColor()))
                myHighlightPainter =
                    new DefaultHighlighter.DefaultHighlightPainter(col);
    }
    
    // removed the threading which was screwing up multi-delete
    protected void updateView(){    	
        final DocumentState docstate = (DocumentState)docList.getSelectedValue();
        if (docstate == null) return;
        
        String txt;
        Set colors = null;
        Map colorToLocation = null;
        try {
            txt = docstate.getDocument().getText();
        } catch (Exception ex){
            txt = lostText;
        }
        colors = docstate.getHighlightColors();
        //log.info("Finding colors: " + colors.toString());
        colorToLocation = new HashMap();
        for (Iterator iter = colors.iterator(); iter.hasNext();) {
        	Color col = (Color) iter.next();
        	//log.info("examining " + col.toString());
        	Set s = docstate.getHighlightLocations(col);
        	//log.info("Found a set of locations: " + s.toString());
        	colorToLocation.put(col, s);
        }
    
        area.setText(txt);
        currentDocument.setText(docstate.getDocument().getTitle());
        
        area.setCaretPosition(docstate.getCaretPosition());
        Font pf = docstate.getDocument().getPreferredFont();
        if (pf != null)
        	area.setFont(pf);
        else if (!displayFont.equals(area.getFont())){
        	log.info("Setting area to font: " + displayFont.toString());
        	area.setFont(displayFont);
        }

        // add highlights
        for (Iterator iter = colors.iterator(); iter.hasNext();) {
        	Color col = (Color) iter.next();
        	setHighlightColor(col);
        	Set s = (Set)colorToLocation.get(col);
        	for (Iterator iterator = s.iterator(); iterator.hasNext();) {
        		Location loc = (Location) iterator.next();
        		try {
        			h.addHighlight(loc.getStartPosition(), 
        					loc.getEndPosition(), myHighlightPainter);
        		} catch (BadLocationException ble){
        			log.warning("highlighting error");
        		}
        	}
        }
    
        
        /*
        tworker = new TaskWorker(this){
            String txt;
            Set colors;
            Map colorToLocation;
            protected void doWork() throws Exception {
                txt = docstate.getDocument().getText();
                colors = docstate.getHighlightColors();
                //log.info("Finding colors: " + colors.toString());
                colorToLocation = new HashMap();
                for (Iterator iter = colors.iterator(); iter.hasNext();) {
                    Color col = (Color) iter.next();
                    //log.info("examining " + col.toString());
                    Set s = docstate.getHighlightLocations(col);
                    //log.info("Found a set of locations: " + s.toString());
                    colorToLocation.put(col, s);
                }
            }
            protected void onError() {
                txt = lostText;
            }
            protected void onSuccess() {
            	area.setText(txt);
                area.setCaretPosition(docstate.getCaretPosition());
                Font pf = docstate.getDocument().getPreferredFont();
                if (pf != null)
                    area.setFont(pf);
                else if (!displayFont.equals(area.getFont())){
                    log.info("Setting area to font: " + displayFont.toString());
                    area.setFont(displayFont);
                }
                        
                // add highlights
                for (Iterator iter = colors.iterator(); iter.hasNext();) {
                    Color col = (Color) iter.next();
                    setHighlightColor(col);
                    Set s = (Set)colorToLocation.get(col);
                    for (Iterator iterator = s.iterator(); iterator.hasNext();) {
                        Location loc = (Location) iterator.next();
                        try {
                            h.addHighlight(loc.getStartPosition(), 
                                    loc.getEndPosition(), myHighlightPainter);
                        } catch (BadLocationException ble){
                            log.warning("highlighting error");
                        }
                    }
                }
            }
        };
        
        tworker.start();
        */
    }
    
    public void setDocumentList(DocumentList dl){
        documentList = dl;
        
        /*
        // tidy up lost project documents
        Set lostdocs = new HashSet();
        for (Iterator iter = documentList.iterator(); iter.hasNext();) {
            YKDocument doc = (YKDocument) iter.next();
            if (!doc.getLocation().exists())			
                lostdocs.add(doc);
        }
        for (Iterator iter = lostdocs.iterator(); iter.hasNext();) {
            YKDocument lostdoc = (YKDocument) iter.next();
            log.info("Deleting non-existent " + lostdoc.getTitle() + " from the documentlist");
            documentList.remove(lostdoc);
        }
        */
        
        model = new DocumentListModel(documentList);
        docList.setModel(model);

        if (model.size()>0)
            docList.setSelectedIndex(0);
    }
    
    public void addDocument(YKDocument doc){
        DocumentState ds = new DocumentState(doc);
        model.addElement(ds);
        docList.setSelectedValue(ds, true);
    }
    
    public void removeDocument(YKDocument doc){
        DocumentState ds = new DocumentState(doc);
        int where = model.indexOf(ds);
        if (where != -1){
            model.removeElement(ds);
            log.info("removed the document " + ds.getDocument().getTitle() + " at " + where + " from model");
            log.info("selecting " + (where-1));
            if ((where-1) >= 0){
                log.info("selecting " + (where-1));
                docList.setSelectedIndex(where-1);
            } else if (where < model.size()){
                log.info("selecting " + where);
                docList.setSelectedIndex(where);
            } else {
                log.warning("That was the last document");
                area.setText("");
                currentDocument.setText("");
            }
        }
    }
    
    public void addHighlights(TokenList tl, Color col){        
        DocumentState ds = (DocumentState)docList.getSelectedValue();
        if (ds == null) {
            log.info("No document state selected");
            return;
        }
        ds.addHighlights(tl, col);
        updateView();
    }
    
    public void removeHighlights(){
        DocumentState ds = (DocumentState)docList.getSelectedValue();
        if (ds == null) {
            log.info("No document state selected");
            return;
        }
        ds.removeHighlights();
        updateView();
    }
    
    public void setSelectedDocument(YKDocument doc){
        DocumentState ds = new DocumentState(doc);
        docList.setSelectedValue(ds, true);
        // view is updated by selection listener
    }
    
    public YKDocument getSelectedDocument(){
        DocumentState ds = (DocumentState)docList.getSelectedValue();
        if (ds != null)
            return ds.getDocument();
        else
            return null;
    }
    
    class DocumentEditPanel extends JPanel {
    	
    	DocumentState state;
    	JComboBox localeBox = new JComboBox(FileUtil.getLocaleList().toArray(new LocaleWrapper[]{}));
    	JComboBox encodingBox = new JComboBox(FileUtil.getCharsetList().toArray(new CharsetWrapper[]{}));
    	JTextField titleField = new JTextField();
    	
    	public Locale getSelectedLocale(){
    		return ((LocaleWrapper)localeBox.getSelectedItem()).locale;
    	}
    	
    	public Charset getSelectedEncoding(){
    		return ((CharsetWrapper)encodingBox.getSelectedItem()).charset;
    	}
    	
    	public String getNewTitle(){
    		return titleField.getText();
    	}
    	
    	public void setDocumentState(DocumentState ds){
    		state = ds;
    		YKDocument doc = state.getDocument();
    		localeBox.setSelectedItem(new LocaleWrapper(doc.getLocale()));
    		encodingBox.setSelectedItem(new CharsetWrapper(Charset.forName(doc.getCharsetName())));
    		titleField.setText(doc.getTitle());
    	}
    	
    	public DocumentState getDocumentState(){
    		return state;
    	}
    	
    	public DocumentEditPanel(DocumentState s) {
    		super(new GridBagLayout());
    		state = s;
    		encodingBox.setSelectedItem(new CharsetWrapper(Charset.forName(s.getDocument().getCharsetName())));
    		//System.err.println(s.getDocument().getTitle() + " has locale " + s.getDocument().getLocale());
    		localeBox.setSelectedItem(new LocaleWrapper(s.getDocument().getLocale()));
    		titleField.setText(s.getDocument().getTitle());
    		
    		GridBagConstraints c = new GridBagConstraints();
    		c.fill = GridBagConstraints.NONE;
    		c.anchor = GridBagConstraints.WEST;
    		c.insets = new Insets(0, 5, 5, 5);
    		c.gridx = 0;
    		c.gridy = 0;
    		add(new JLabel("Title"), c);
    		
    		c.fill = GridBagConstraints.NONE;
    		c.gridx = 0;
    		c.gridy = 1;
    		add(new JLabel("Encoding"), c);
    		
    		c.fill = GridBagConstraints.NONE;
    		c.gridx = 0;
    		c.gridy = 2;
    		add(new JLabel("Locale"), c);
    		
    		c.fill = GridBagConstraints.HORIZONTAL;
    		c.gridx = 1;
    		c.gridy = 0;
    		add(titleField, c);
    		
    		c.fill = GridBagConstraints.HORIZONTAL;
    		c.gridx = 1;
    		c.gridy = 1;
    		add(encodingBox, c);

    		c.fill = GridBagConstraints.HORIZONTAL;
    		c.gridx = 1;
    		c.gridy = 2;
    		add(localeBox, c);
    		
    	}
    }
    
    protected DocumentEditPanel dep = null;
    
    // return whether there were any actual changes
    public boolean editDocument(){
    	DocumentState ds = (DocumentState)docList.getSelectedValue();
    	if (dep == null)
    		dep = new DocumentEditPanel(ds);
    	else
    		dep.setDocumentState(ds);
    	
    	int resp = JOptionPane.showConfirmDialog(yoshikoder, dep, 
    			"Edit Document", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    	if (resp != JOptionPane.OK_OPTION)
    		return false;
    	    	
    	String newtitle = dep.getNewTitle();
    	boolean exists = false;
    	for (int ii = 0; ii < docList.getModel().getSize(); ii++) {
			DocumentState d = (DocumentState) docList.getModel().getElementAt(ii);
    		if (d.getDocument() != dep.getDocumentState().getDocument()){ // *exact* reference equality please
    			if (d.getDocument().getTitle().equals(newtitle)){
    				exists = true;
    				break;
    			}
    		}
    	}
    	if (exists){
    		DialogUtil.yelp(yoshikoder, "A document with this title already exists", "Title exists");
    		return false;
    	
    	} else {
        	boolean changed = false;
    		
    		DocumentState s = dep.getDocumentState();
    		YKDocument doc = s.getDocument();

    		if (!doc.getCharsetName().equals(dep.getSelectedEncoding().name())){
    			doc.setCharsetName(dep.getSelectedEncoding().name());
    			changed = true;
    		}
    		if (changed){ // need to remove cached tokens
    			TokenizationCache cache = yoshikoder.getTokenizationCache();
    			cache.removeTokenList(doc);
    			if (doc instanceof LazyYKDocument)
    				((LazyYKDocument)doc).clearCachedText(); // force a re-read from file system
    			// does not do anything weird to the text area immediately, but probably should...
    		}

    		if (!doc.getLocale().equals(dep.getSelectedLocale())){
    			doc.setLocale(dep.getSelectedLocale());
    			changed = true;
    		}	
    		if (!doc.getTitle().equals(newtitle)){
    			doc.setTitle(newtitle);
    			changed = true;
    		}
    		repaint();
    		return changed;
    		// TODO reorder the list
    		//model = new DocumentListModel(documentList);
    		// trigger updates?
    		//docList.setModel(model);
    		// TODO select the original document
    		//docList.setSelectedValue(s, true);
    	}

    }

    public YKDocument[] getSelectedDocuments(){
    	//Object[] o = docList.getSelectedValues();
    	//for (int i = 0; i < o.length; i++) {
    	//    System.err.println("Is this a documentstate instance? " + (o[i] instanceof DocumentState));
    	//}
    	int[] indices = docList.getSelectedIndices();

    	YKDocument[] d = new YKDocument[indices.length];
    	if (indices.length > 0){
    		for (int ii = 0; ii < indices.length; ii++) {
    			DocumentState ds = (DocumentState)docList.getModel().getElementAt(indices[ii]);
    			d[ii] = ds.getDocument();
    			//System.err.println(d[ii].getTitle() + " has been extracted from some document state object");
    		}
    	} 
    	return d;
    }


    public void setDisplayFont(Font f){
    	displayFont = f;
    	updateView();
    }

    public static void main(String[] args) throws Exception {
    	/*
    	DocumentList dl = new DocumentListImpl();
    	for (int ii=0; ii<4; ii++){
    		YKDocument doc = 
    				YKDocumentFactory.createDummyDocument("title" + ii, 
    						"text for document " + ii, "UTF-8");
    		dl.add(doc);
    	}
        DocumentPanel pane = new DocumentPanel(dl);
        YKDocument doc = pane.getSelectedDocument();
        TokenList tl = TokenizationService.getTokenizationService().tokenize(doc);
        TokenList first = new TokenListImpl();
        first.add(tl.get(0));
        pane.addHighlights(first, Color.yellow);
        first.clear();
        first.add(tl.get(1));
        pane.addHighlights(first, Color.lightGray);
        
        
        JOptionPane jo = new JOptionPane(pane);
        JDialog dia = jo.createDialog((JFrame)null, "Documents");
        dia.show();
        System.exit(0);
    	*/
    }
}
