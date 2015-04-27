package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class FatalErrorPanel extends JPanel {

    protected JEditorPane area;
    
    public FatalErrorPanel(String message){
        super(new BorderLayout());
        area = new JEditorPane("text/html", message);
        area.setEditable(false);
        
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(200,200));
        add(scroll, BorderLayout.CENTER);
        
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }
    
}
