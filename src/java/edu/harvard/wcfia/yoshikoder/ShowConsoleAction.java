package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.File;

import edu.harvard.wcfia.yoshikoder.ui.dialog.MessageDialog;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.DialogWorker;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;

public class ShowConsoleAction extends YoshikoderAction {

    File logFile;
    
    public ShowConsoleAction(Yoshikoder yk) {
        super(yk, ShowConsoleAction.class.getName());
        logFile = YKFS.getYKFS().getLogFile();
    }

    public void actionPerformed(ActionEvent e) {
        dworker = new DialogWorker(yoshikoder){
            protected void doWork() throws Exception {
                String messages = FileUtil.slurp(logFile);
                dia = new MessageDialog(yoshikoder, "Console", messages);
            }
            protected void onError() {
                DialogUtil.yelp(yoshikoder, "Failed to launch logging console", e);
            }
        }; 
        dworker.start();
    }

}
