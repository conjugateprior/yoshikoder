package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.ImportUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class OpenConcordanceAction extends YoshikoderAction {

    protected FileDialog concordanceChooser;
    
    public OpenConcordanceAction(Yoshikoder yk) {
        super(yk, OpenConcordanceAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        if (concordanceChooser==null)
            concordanceChooser = DialogUtil.makeFileDialog(yoshikoder, 
                "Open Concordance", FileDialog.LOAD, DialogUtil.ykcFilenameFilter); 
            
        concordanceChooser.setFile(null);
        concordanceChooser.show();
        String file = concordanceChooser.getFile();
        if (file == null) return;
        final File concFile = new File(concordanceChooser.getDirectory(), file);
            
        tworker = new TaskWorker(yoshikoder){
            Concordance conc;
            protected void doWork() throws Exception {
                conc = ImportUtil.importConcordance(concFile);
            }
            protected void onSuccess() {
                yoshikoder.setConcordance(conc);
            }
            protected void onError() {
                DialogUtil.yelp(yoshikoder, "Could not import concordance", e);
            }
        };
        tworker.start();
    }
}
