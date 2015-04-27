package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.ExportUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class ExportDictionaryAsHtml extends YoshikoderAction {

    protected FileDialog dictionaryExporter;
    
    public ExportDictionaryAsHtml(Yoshikoder yk) {
        super(yk, ExportDictionaryAsHtml.class.getName());
    }
    
    public void actionPerformed(ActionEvent e) {
        final YKDictionary dict = yoshikoder.getDictionary();
        
        if (dictionaryExporter == null)
            dictionaryExporter = DialogUtil.makeFileDialog(yoshikoder, 
                "Export Dictionary as HTML", FileDialog.SAVE, null);
        
        dictionaryExporter.setFile(null);
        dictionaryExporter.show();
        String filename = dictionaryExporter.getFile();
        if (filename == null) return;
        
        final File file = new File(dictionaryExporter.getDirectory(),
                FileUtil.suffix(filename, "html", "htm"));
        tworker = new TaskWorker(yoshikoder){
            protected void doWork() throws Exception {
                ExportUtil.exportAsHTML(dict, file);
            } 
            protected void onError() {
                DialogUtil.yelp(yoshikoder, "Could not export dictionary as HTML", e); 
            }
        };
        tworker.start(); 
    }

}
