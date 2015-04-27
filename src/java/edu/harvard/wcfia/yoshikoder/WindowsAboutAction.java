package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import edu.harvard.wcfia.yoshikoder.ui.dialog.AboutDialog;

public class WindowsAboutAction extends YoshikoderAction {

    protected JDialog dia;
    
    public WindowsAboutAction(Yoshikoder yk) {
        super(yk, WindowsAboutAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        if (dia == null)
            dia = new AboutDialog(yoshikoder);
        dia.setLocationRelativeTo(yoshikoder);
        dia.setVisible(true);
    }

}
