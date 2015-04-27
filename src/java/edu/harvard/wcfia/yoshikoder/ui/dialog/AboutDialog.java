package edu.harvard.wcfia.yoshikoder.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.util.ApplicationDetails;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;

/**
 * @author will
 */
public class AboutDialog extends JDialog { 
    
    public AboutDialog(Yoshikoder yk) {
        super(yk, Messages.getString("AboutDialog.aboutTitle"), true); 
        
        String version = ApplicationDetails.getString("Yoshikoder.application.version");
        String copyright = ApplicationDetails.getString("Yoshikoder.application.copyright");
        String iconPath = ApplicationDetails.getString("Yoshikoder.application.icon");
        String buildnumber = ApplicationDetails.getString("Yoshikoder.application.buildnumber");
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JTextArea area = new JTextArea();
        area.append("Yoshikoder v." + version + "\n");
        area.append("Build " + buildnumber + "\n");
        area.append(copyright);
        area.setEditable(false);
        area.setBorder(BorderFactory.createEmptyBorder(10,40,10,40));
        area.setBackground(Color.WHITE);
        
        Icon ic = DialogUtil.getDialogIcon(iconPath);
        
        Image img = ((ImageIcon)ic).getImage();  
        Image newimg = img.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH);  
        ic = new ImageIcon(newimg);
        
        panel.add(area, BorderLayout.SOUTH);
        if (yk != null)
            panel.add(new JLabel(ic), BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(yk);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
}
