package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.ExportUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class ExportConcordanceAsHtmlAction extends YoshikoderAction {
    
    protected FileDialog concordanceExporter;
    
    public ExportConcordanceAsHtmlAction(Yoshikoder yk) {
        super(yk, ExportConcordanceAsHtmlAction.class.getName());
    }
    
    public void actionPerformed(ActionEvent e) {
        final Concordance concordance = yoshikoder.getConcordance();
        if ((concordance==null) || (concordance.size()==0))
            return;
        
        if (concordanceExporter == null)
            concordanceExporter = DialogUtil.makeFileDialog(yoshikoder, 
                "Export Concordance as HTML", FileDialog.SAVE, null);
        
        concordanceExporter.setFile(null);
        concordanceExporter.show();
        String filename = concordanceExporter.getFile();
        if (filename == null) return;
        
        final File file = new File(concordanceExporter.getDirectory(),
                FileUtil.suffix(filename, "html", "htm"));
        tworker = new TaskWorker(yoshikoder){
            protected void doWork() throws Exception {
                ExportUtil.exportAsHTML(concordance, file);
            } 
            protected void onError() {
                DialogUtil.yelp(yoshikoder, "Could not export concordance as HTML", e);
            }
        };
        tworker.start();
        
    }
   
}
