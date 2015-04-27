package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;

import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.ExportUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class SaveAsDictionaryAction extends YoshikoderAction {
    
    protected FileDialog dictionarySaver;
    
    public SaveAsDictionaryAction(Yoshikoder yk) {
        super(yk, SaveAsDictionaryAction.class.getName());
    }
    
    public void actionPerformed(ActionEvent e) {
        if (dictionarySaver==null)
            dictionarySaver = DialogUtil.makeFileDialog(yoshikoder,
                    "Save Dictionary As", FileDialog.SAVE, null); 
        
        dictionarySaver.setFile(null);
        dictionarySaver.show();
        String fname = dictionarySaver.getFile();
        if (fname == null) return;
        File f = new File(dictionarySaver.getDirectory(), FileUtil.suffix(fname, "ykd"));
        
        final File file = FileUtil.suffix(f, "ykd");
        tworker = new TaskWorker(yoshikoder){
            protected void doWork() throws Exception {
                YKDictionary dict = yoshikoder.getDictionary();
                ExportUtil.exportAsXML(dict, file);
            } 
            protected void onError() {
                DialogUtil.yelp(yoshikoder, "Could not save dictionary", e); 
            }
        };
        tworker.start();        
    }
    
}
