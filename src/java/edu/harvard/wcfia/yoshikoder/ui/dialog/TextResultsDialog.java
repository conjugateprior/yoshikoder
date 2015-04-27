package edu.harvard.wcfia.yoshikoder.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextResultsDialog extends JDialog {
    protected JTextArea area;
    protected JButton clearButton;
    
    public TextResultsDialog(Frame f, String title){
        super(f, title, false);
        init();
        setLocationRelativeTo(f);
    }
    
    public TextResultsDialog(Dialog dia, String title){
        super(dia, title, false);
        init();
        setLocationRelativeTo(dia);
    }
    
    protected void init(){
        area = new JTextArea(30,60);
        //area.setEditable(false);
        // TODO inherit YK font
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                area.setText("");
            } 
        });
        JPanel bp = new JPanel(new BorderLayout());
        bp.add(clearButton, BorderLayout.WEST);
        bp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.SOUTH);
        pack();
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }
    
    public void addResults(String newResults){
        area.append("\n");
        DateFormat f = 
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG);
        area.append("----------[ ");
        area.append(f.format(new Date()));
        area.append(" ]\n\n");
        area.append(newResults);
        area.setCaretPosition(area.getText().length()-1);
    }

    public static void main(String[] args) {
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG);
        System.out.println(f.format(new Date()));
    }
    
}
