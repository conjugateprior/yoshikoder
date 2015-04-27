package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.ExportUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class ExportConcordanceAsExcelAction extends YoshikoderAction {
    
    protected FileDialog concordanceExporter;
    
    public ExportConcordanceAsExcelAction(Yoshikoder yk) {
        super(yk, ExportConcordanceAsExcelAction.class.getName());
    }
    
    public void actionPerformed(ActionEvent e) {
        final Concordance concordance = yoshikoder.getConcordance();
        if ((concordance==null) || (concordance.size()==0))
            return;
        
        if (concordanceExporter == null)
            concordanceExporter = DialogUtil.makeFileDialog(yoshikoder, 
                "Export Concordance as MS Excel", FileDialog.SAVE, null);
        
        concordanceExporter.setFile(null);
        concordanceExporter.show();
        String filename = concordanceExporter.getFile();
        if (filename == null) return;
        
        final File file = new File(concordanceExporter.getDirectory(),
                FileUtil.suffix(filename, "xls"));
        tworker = new TaskWorker(yoshikoder){
            protected void doWork() throws Exception {
                ExportUtil.exportAsXLS(concordance, file);
            } 
            protected void onError() {
                DialogUtil.yelp(yoshikoder, "Could not export concordance in Excel format", e); 
            }
        };
        tworker.start(); 
    }
    
}
