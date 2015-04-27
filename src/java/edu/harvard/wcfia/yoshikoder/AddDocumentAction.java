package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.YKDocumentFactory;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class AddDocumentAction extends YoshikoderAction {

    protected JFileChooser documentChooser;
    
    public AddDocumentAction(Yoshikoder yk) {
        super(yk, AddDocumentAction.class.getName());

    }

    public void actionPerformed(ActionEvent e) {   
        if (documentChooser==null){
            documentChooser = new JFileChooser();
            documentChooser.setMultiSelectionEnabled(true);
            documentChooser.setFileFilter(new FileFilter(){
               public boolean accept(File f) {
                   return f.getName().endsWith(".txt") || f.isDirectory();
               }
               public String getDescription() {
                   return "Text files";
               }
            });
        }
        
        int resp = documentChooser.showOpenDialog(yoshikoder);
        if (resp != JFileChooser.APPROVE_OPTION) return;
  
        final File[] fs = documentChooser.getSelectedFiles();
        if (fs.length == 0) return;
        
        final Locale defLoc = yoshikoder.getDefaultLocale();
        
        tworker = new TaskWorker(yoshikoder){
            YKDocument[] docs;
            protected void doWork() throws Exception {
                docs = new YKDocument[fs.length];
                for (int ii = 0; ii < fs.length; ii++) {       
                    YKDocument doc = YKDocumentFactory.createYKDocument(fs[ii], fs[ii].getName(), 
                    		yoshikoder.getDefaultEncoding().toString(), defLoc);
                    docs[ii] = doc;
                }
            };
            protected void onSuccess(){ 
                for (int ii = 0; ii < docs.length; ii++) {
                    yoshikoder.addDocument(docs[ii]);
                    yoshikoder.setUnsavedChanges(true);
                }
                
            }
            protected void onError() {
                DialogUtil.yelp(yoshikoder, "Could not add document", e);
            }
        };
        tworker.start();        
    }

}
