package edu.harvard.wcfia.yoshikoder.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.harvard.wcfia.yoshikoder.util.ExportUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class ExportDialog extends JDialog {

    protected String[] formats;
    protected JRadioButton[] buttons;
    protected JButton okButton, cancelButton;
    protected String chosenFormat; // null if cancelled
    
    public ExportDialog(Frame parent, String[] fileformats){
        super(parent, Messages.getString("export"), true);
        init(parent, fileformats);
    }
    
    public ExportDialog(Dialog parent, String[] fileformats){
        super(parent, Messages.getString("export"), true);
        init(parent, fileformats);
    }

    protected void init(Component parent, String[] fileformats){
        formats = fileformats;
        JPanel panel = new JPanel(new GridLayout(fileformats.length, 1));
        ButtonGroup group = new ButtonGroup();
        buttons = new JRadioButton[fileformats.length];
        for (int ii = 0; ii < fileformats.length; ii++) {
            JRadioButton button = new JRadioButton(fileformats[ii]);
            buttons[ii] = button;
            group.add(button);
            panel.add(button);
        }
        buttons[0].setSelected(true);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        JPanel main = new JPanel(new BorderLayout());
        main.add(panel, BorderLayout.CENTER);
        JLabel label = new JLabel("Choose an file format for the report");
        label.setBorder(BorderFactory.createEmptyBorder(10,10,0, 10));
        main.add(label, BorderLayout.NORTH);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(main, BorderLayout.CENTER);
        getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(parent);

    }
    
    protected JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout());

        Box bbox = Box.createHorizontalBox();
        cancelButton = new JButton(Messages.getString("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                chosenFormat = null;
                hide();
            }
        });
        okButton = new JButton(Messages.getString("ok")); 
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int buttonOn = 0;
                for (int ii = 0; ii < buttons.length; ii++) {
                    if (buttons[ii].isSelected()){
                        buttonOn = ii;
                        break;
                    }
                }
                chosenFormat = formats[buttonOn];
                hide();
            }
        });
        
        // balance button widths
        int width = Math.max(okButton.getPreferredSize().width, 
                cancelButton.getPreferredSize().width);
        okButton.setPreferredSize(new Dimension(width, 
                okButton.getPreferredSize().height));
        cancelButton.setPreferredSize(new Dimension(width, 
                cancelButton.getPreferredSize().height));
    
        if (FileUtil.isMac()){
            bbox.add(Box.createHorizontalGlue());
            bbox.add(cancelButton);
            bbox.add(Box.createHorizontalStrut(5));
            bbox.add(okButton);
            bbox.add(Box.createHorizontalGlue());
            bbox.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        } else {
            bbox.add(Box.createHorizontalGlue());
            bbox.add(okButton);
            bbox.add(Box.createHorizontalStrut(5));
            bbox.add(cancelButton);
            bbox.add(Box.createHorizontalGlue());
            bbox.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));           
        }        
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                Color.GRAY));
        buttonPanel.add(bbox, BorderLayout.EAST);

        return buttonPanel;
        
    }
       
    public String getChosenFormat(){
        return chosenFormat;
    }
    
    public static void main(String[] args) {
        
        JDialog d = new JDialog();
        ExportDialog dia = new ExportDialog(d, 
                new String[]{ExportUtil.HTML_FORMAT, ExportUtil.XML_FORMAT, ExportUtil.TXT_FORMAT});
        dia.show();
        System.out.println(dia.getChosenFormat());
    }
    
}
