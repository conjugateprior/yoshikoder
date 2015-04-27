package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;

import edu.harvard.wcfia.yoshikoder.ui.dialog.PreferencesDialog;

public class PreferencesAction extends YoshikoderAction {
    
    public PreferencesAction(Yoshikoder yk) {
        super(yk, PreferencesAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        PreferencesDialog dia = new PreferencesDialog(yoshikoder);
        dia.setVisible(true);
    }
}
