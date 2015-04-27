package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.ui.PreviewPanel;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;
import edu.harvard.wcfia.yoshikoder.util.VBProFileParser;

public class ImportVBProAction extends YoshikoderAction {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.ImportVBProAction");
    
    protected FileDialog vbproImporter;
    
    protected byte[] fileBytes;
    protected YKDictionary importedDictionary;
    
    protected boolean bailNow;
   
    public ImportVBProAction(Yoshikoder yk) {
        super(yk, ImportVBProAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        if (vbproImporter==null)
            vbproImporter = DialogUtil.makeFileDialog(yoshikoder, 
                "Import VBPro Dictionary", FileDialog.LOAD, null);
        
        // locate the file
        vbproImporter.setFile(null);
        vbproImporter.show();
        String file = vbproImporter.getFile();
        if (file==null) return;
        final File f = new File(vbproImporter.getDirectory(), file);
        
        try {
            fileBytes = FileUtil.getBytes(f, 1000);
        } catch (IOException ioe){
            DialogUtil.yelp(yoshikoder, "Could not open file " + f.getName(), ioe);
            return;
        }
        
        final PreviewPanel preview = new PreviewPanel(fileBytes, yoshikoder.getDefaultEncoding());
        int i = JOptionPane.showConfirmDialog(yoshikoder, preview, 
                "Preview Dictionary",  JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);    
        if (i != JOptionPane.OK_OPTION)
            return;
                
        // parse the contents and insert the resulting dictionary
        tworker = new TaskWorker(yoshikoder){
            VBProFileParser parser = new VBProFileParser();
            protected void doWork() throws Exception {
                importedDictionary = parser.parse(f, preview.getSelectedEncoding().name());
                importedDictionary.setName(f.getName());
                if (parser.getErrors().size()>0)
                    throw new Exception("non-fatal import errors");
            }
            protected void onSuccess() {
                log.info("parsed dictionary without major errors");
                log.info("setting dictionary to imported version");
                yoshikoder.setDictionary(importedDictionary);
                yoshikoder.setUnsavedChanges(true);
            }
            protected void onError() {
                if (parser.getErrors() == null){
                    // serious error occurred before parser had a chance to work
                    DialogUtil.yelp(yoshikoder, "Could not parse contents of VBPro file", e);
                } else {
                    // nonfatal errors
                    List l = parser.getErrors();
                    if (l.size() > 0){
                        JTextArea area = new JTextArea(20, 40); 
                        area.setFont(yoshikoder.getDisplayFont());
                        area.setEditable(false);
                        area.setLineWrap(true);
                        area.setWrapStyleWord(true);
                        area.append("There were some problems importing a VBPro dictionary from " +
                                f.getName() + "\n\n");
                        area.append("Unparseable or duplicate patterns are listed below.\n\n");
                        area.append("Would you like to discard these patterns and import the rest?\n\n");
                        for (Iterator iter = l.iterator(); iter.hasNext();) {
                            VBProFileParser.BadPattern bp = 
                                (VBProFileParser.BadPattern)iter.next();
                            area.append(bp + "\n");
                        }
                        area.setCaretPosition(0);
                        // throw it in anyway if they want us to
                        int resp = 
                            JOptionPane.showConfirmDialog(yoshikoder, new JScrollPane(area),
                                "Problem Patterns in Import", 
                                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                        if (resp == JOptionPane.YES_OPTION){
                            log.info("setting dictionary to imported version from error handler");
                            yoshikoder.setDictionary(importedDictionary);
                            yoshikoder.setUnsavedChanges(true);
                        } 
                    }
                }
            }
        };
        tworker.start();
        
        
    }
        
    public static void main(String[] args) {
        String name = ">>fg f<d<<";
        Pattern p = Pattern.compile("^\\>+(.+?)\\<+$");
        Matcher m = p.matcher(name);
        System.out.println( m.matches() );
        System.out.println( m.group(1));
    }
    
}
