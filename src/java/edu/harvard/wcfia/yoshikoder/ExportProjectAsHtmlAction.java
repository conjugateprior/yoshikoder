package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.ExportUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class ExportProjectAsHtmlAction extends YoshikoderAction {
    
    protected FileDialog projectExporter;
    
    public ExportProjectAsHtmlAction(Yoshikoder yk) {
        super(yk, ExportProjectAsHtmlAction.class.getName());
    }
    
    public void actionPerformed(ActionEvent e) {
        if (projectExporter == null)
            projectExporter = DialogUtil.makeFileDialog(yoshikoder, 
                    "Export Project As HTML", FileDialog.SAVE, null);
        
        projectExporter.setFile(null);
        projectExporter.show();
        
        String filename = projectExporter.getFile();
        if (filename==null) return;
        File f = new File(projectExporter.getDirectory(), filename);        
        
        final File file = FileUtil.suffix(f, "html", "htm");
        tworker = new TaskWorker(yoshikoder){
            protected void doWork() throws Exception {
                ExportUtil.exportAsHTML(yoshikoder.getProject(), file);
            } 
            protected void onError() {
                DialogUtil.yelp(yoshikoder, "Could not export the project as HTML", e); 
            }
        };
        tworker.start();   
    }
}
