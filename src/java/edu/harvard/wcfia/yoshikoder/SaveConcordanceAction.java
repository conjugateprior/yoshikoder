package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.ExportUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class SaveConcordanceAction extends YoshikoderAction {
    
    protected FileDialog concordanceSaver;
    
    public SaveConcordanceAction(Yoshikoder yk) {
        super(yk, SaveConcordanceAction.class.getName()); 
    }
    
    public void actionPerformed(ActionEvent e) {
        final Concordance concordance = yoshikoder.getConcordance();
        if ((concordance==null) || (concordance.size()==0))
            return;
        
        if (concordanceSaver==null)
            concordanceSaver = DialogUtil.makeFileDialog(yoshikoder, 
                "Save Concordance", FileDialog.SAVE, null);
        
        concordanceSaver.setFile(null);
        concordanceSaver.show();
        String fname = concordanceSaver.getFile();
        if (fname == null) return;
        
        final File file = 
            new File(concordanceSaver.getDirectory(), FileUtil.suffix(fname, "ykc"));
        tworker = new TaskWorker(yoshikoder){
            protected void doWork() throws Exception {
                ExportUtil.exportAsXML(concordance, file);
            } 
            protected void onError() {
                DialogUtil.yelp(yoshikoder, "Could not save concordance", e);
            }
        };
        tworker.start();
    }
    
}
