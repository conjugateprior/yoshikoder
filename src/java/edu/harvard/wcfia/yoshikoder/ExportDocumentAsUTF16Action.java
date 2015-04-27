package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class ExportDocumentAsUTF16Action extends YoshikoderAction{
    
    protected FileDialog documentExporter;
    
    public ExportDocumentAsUTF16Action(Yoshikoder yk) {
        super(yk, ExportDocumentAsUTF16Action.class.getName());
    }
    
    public void actionPerformed(ActionEvent e) {        
        final YKDocument doc = yoshikoder.getSelectedDocument();
        if (doc == null) return;
        
        if (documentExporter==null)
            documentExporter = 
                DialogUtil.makeFileDialog(yoshikoder, 
                        "Export Document as UTF-16", 
                        FileDialog.SAVE, null); // TODO loc        
        
        documentExporter.setFile(null);
        documentExporter.show();
        String fname = documentExporter.getFile();
        if (fname == null) return;
        
        File filed = new File(documentExporter.getDirectory(), fname);
        final File file = FileUtil.suffix( filed, "txt");
        tworker = new TaskWorker(yoshikoder){
            protected void doWork() throws Exception {
                FileUtil.save(file, doc.getText(), "UTF-16"); 
            }
            protected void onError() {
                DialogUtil.yelp(yoshikoder, "Could not export the document", e); // TODO loc
            }
        };
        tworker.start();
    }
    
}
