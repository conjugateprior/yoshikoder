package edu.harvard.wcfia.yoshikoder.ui.dialog;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MessageDialog extends JDialog {
    
    public MessageDialog(JFrame yk, String title, String text){
        super(yk, title, true);
        JTextArea area =  new JTextArea(20,40);
        area.setEditable(false);
        area.setText(text);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(area));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(yk);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
}


