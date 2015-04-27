package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import edu.harvard.wcfia.yoshikoder.ui.dialog.ImportDocumentDialog;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.DialogWorker;

public class ImportDocumentAction extends YoshikoderAction {

    protected FileDialog documentImporter;
    
    public ImportDocumentAction(Yoshikoder yk) {
        super(yk, ImportDocumentAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        if (documentImporter==null)
            documentImporter = 
                DialogUtil.makeFileDialog(yoshikoder,
                        "Import Document", FileDialog.LOAD, 
                        DialogUtil.txtFilenameFilter); // TODO loc
        
        documentImporter.setFile(null);
        documentImporter.show();
        String file = documentImporter.getFile();
        if (file == null) return;
        final File f = new File(documentImporter.getDirectory(), file);   
        dworker = new DialogWorker(yoshikoder){
            protected void doWork() throws Exception {
                dia = new ImportDocumentDialog(yoshikoder, f);
            }
            protected void onError(){}
        };
        dworker.start();
    }

}
