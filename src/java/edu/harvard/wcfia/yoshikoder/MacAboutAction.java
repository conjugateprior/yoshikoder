package edu.harvard.wcfia.yoshikoder;

import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.roydesign.ui.StandardMacAboutFrame;
import edu.harvard.wcfia.yoshikoder.util.ApplicationDetails;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;

public class MacAboutAction extends YoshikoderAction {

    protected StandardMacAboutFrame macAbout;
    
    public MacAboutAction(Yoshikoder yk) {
        super(yk, MacAboutAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        if (macAbout == null){
            String appname = ApplicationDetails.getString("Yoshikoder.application.name");
            String appver = ApplicationDetails.getString("Yoshikoder.application.version");
            String iconPath = ApplicationDetails.getString("Yoshikoder.application.icon");
            String appbuild = ApplicationDetails.getString("Yoshikoder.application.buildnumber");
            String appcopy = ApplicationDetails.getString("Yoshikoder.application.copyright");
            
            macAbout = new StandardMacAboutFrame(appname, appver);
            Icon ic = DialogUtil.getDialogIcon(iconPath);
            
            Image img = ((ImageIcon)ic).getImage();  
            Image newimg = img.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH);  
            ic = new ImageIcon(newimg);
            
            macAbout.setApplicationIcon(ic);
            macAbout.setBuildVersion(appbuild);
            macAbout.setCopyright(appcopy);
        }
        macAbout.setLocationRelativeTo(yoshikoder);
        macAbout.setVisible(true);
    }
}
