package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

public class ExitAction extends YoshikoderAction {

    static private Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.ExitAction");
    
    protected ApplicationCloser closer;
    
    public ExitAction(Yoshikoder yk) {
        super(yk, ExitAction.class.getName());
        closer = new ApplicationCloser(yoshikoder);
    }

    public void actionPerformed(ActionEvent e) {
        log.info("Exit action caught");
        closer.closeApplication();
    }

}
