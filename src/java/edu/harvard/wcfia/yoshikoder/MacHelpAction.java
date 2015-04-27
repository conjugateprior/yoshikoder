package edu.harvard.wcfia.yoshikoder;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import com.apple.eawt.Application;

public class MacHelpAction extends YoshikoderAction{

    public MacHelpAction(Yoshikoder yk) {
        super(yk, MacHelpAction.class.getName());
        // override...
        putValue(Action.ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(
                KeyEvent.VK_SLASH, KeyEvent.SHIFT_MASK | 
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    public void actionPerformed(ActionEvent e) {
    	Application app = Application.getApplication();
    	app.openHelpViewer();
    	
        //HelpBook.launchHelpViewer();
    }    
}
