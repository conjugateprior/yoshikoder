package edu.harvard.wcfia.yoshikoder.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.ui.CommitException;
import edu.harvard.wcfia.yoshikoder.ui.CommitableJPanel;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;

/**
 * @author will
 */
public abstract class AbstractOkCancelDialog extends JDialog {
    
    protected JFrame parentFrame;
    protected CommitableJPanel panel;
    protected Icon icon;
    
    protected PropertyChangeListener listener = new PropertyChangeListener(){
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("commit")){
                try {
                    handleOk();
                } catch (CommitException ce){
                    handleError(ce);
                }
            }
        }
    };
    
    protected JButton okButton;
    protected JButton cancelButton;
    
    public AbstractOkCancelDialog(Yoshikoder parent,
            CommitableJPanel p){
        this(parent, p, null);
    }
    
    public AbstractOkCancelDialog(Yoshikoder parent, 
            CommitableJPanel cpanel, 
            String iconName){
        super(parent, true);
        parentFrame = parent;
        panel = cpanel;
        panel.addPropertyChangeListener(listener); // listens for commits
        
        JPanel all = new JPanel(new BorderLayout());
        JPanel buttons = createButtonPanel();
        all.add(buttons, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        //panel.setBorder(BorderFactory.createLineBorder(Color.blue));
        all.add(panel, BorderLayout.CENTER);
        
        if (iconName != null){
            icon = DialogUtil.getDialogIcon(iconName);
            JLabel iconSide = new JLabel(icon);
            iconSide.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
            all.add(iconSide, BorderLayout.WEST);
        }
        
        getContentPane().add(all);
        pack();
        setLocationRelativeTo(parent);  
    }
    
    protected JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        
        Box bbox = Box.createHorizontalBox();
        cancelButton = new JButton(Messages.getString("cancel")); //$NON-NLS-1$
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                dispose();
            }
        });
        okButton = new JButton(Messages.getString("ok")); //$NON-NLS-1$
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    handleOk();
                } catch (CommitException e) {
                    handleError(e);
                }
            }
        });
        // balance button widths
        int width = Math.max(okButton.getPreferredSize().width, 
                cancelButton.getPreferredSize().width);
        okButton.setPreferredSize(new Dimension(width, 
                okButton.getPreferredSize().height));
        cancelButton.setPreferredSize(new Dimension(width, 
                cancelButton.getPreferredSize().height));
        
        
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener( new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                dispose();
            } });
        
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
    
    protected void handleOk() throws CommitException {
        panel.commit();
        dispose();
    }
    
    protected void handleError(CommitException e) {
    	System.err.println("commit exception message: " + e.getMessage());
        DialogUtil.yelp(this, e.getMessage(), e);
    }
    
}
