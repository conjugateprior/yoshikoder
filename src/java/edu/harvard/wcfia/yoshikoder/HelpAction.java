package edu.harvard.wcfia.yoshikoder;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.util.DialogUtil;

public class HelpAction extends YoshikoderAction {

    private static Logger log = Logger.getLogger(HelpAction.class.getName());
    
    protected File helpFile;
    
    public HelpAction(Yoshikoder yk) {
        super(yk, HelpAction.class.getName());
        helpFile = YKFS.getYKFS().getOnlineHelpIndex();
    }

    public void actionPerformed(ActionEvent e) {
        try {
        	Desktop.getDesktop().browse(helpFile.toURI());
            
        } catch (Exception ioe){
            log.log(Level.WARNING, 
                    "Could not launch browser for online help at " + 
                    helpFile.getAbsolutePath(), ioe);
            DialogUtil.yelp(yoshikoder, "Sorry, a browser could not be launched", ioe);
        }
    }

}
