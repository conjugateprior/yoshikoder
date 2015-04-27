package edu.harvard.wcfia.yoshikoder.ui;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.harvard.wcfia.yoshikoder.YKProject;
import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.util.Messages;

/**
 * @author will
 */
public class NewProjectPanel extends CommitablePanel {
    
    private JTextField nameField;
    private JTextArea descArea;
    private JCheckBox useDocs;
    
    private Yoshikoder yoshikoder;
    
    public NewProjectPanel(Yoshikoder yk) {
        super();
        yoshikoder = yk;
        
        nameField = new JTextField(15);
        nameField.setFont(yoshikoder.getDisplayFont());
        nameField.addKeyListener(keyListener); // TODO check not doc
        
        descArea = new JTextArea(3, 15);
        descArea.setFont(yoshikoder.getDisplayFont());
        descArea.addKeyListener(keyListener); // TODO check not doc
        
        useDocs = new JCheckBox();
        useDocs.setSelected(false);
        
        addWidgetInline(Messages.getString("nameLabel"), nameField); 
        addWidgetFixedDepth(Messages.getString("descriptionLabel"), new JScrollPane(descArea)); 
        addWidgetInlineFixedWidth(Messages.getString("NewProjectPanel.copyDocs"), useDocs); 
    }
    
    public void commit() throws CommitException {
        
        if ((nameField.getText() == null) || 
                (nameField.getText().length() == 0)) {
            Exception e = 
                new Exception(Messages.getString("noEntryName"));
            throw new CommitException(e);
        }
        
        String title = nameField.getText();        
        String desc = descArea.getText();
        YKProject newp = null;
        if (useDocs.isSelected())
            newp = new YKProject(title, desc, yoshikoder.getProject().getDocumentList());
        else 
            newp = new YKProject(title, desc);
        
        yoshikoder.getTokenizationCache().clear();
        yoshikoder.setProject(newp);
        yoshikoder.setUnsavedChanges(true);
    }
    
}