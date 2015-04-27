package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

public class QuitAction extends YoshikoderAction {

    static private Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.QuitAction");
    
    protected ApplicationCloser closer;
    
    public QuitAction(Yoshikoder yk) {
        super(yk, QuitAction.class.getName());
        closer = new ApplicationCloser(yoshikoder);
    }

    public void actionPerformed(ActionEvent e) {
        log.info("quit event caught");
        closer.closeApplication();
    }
}
