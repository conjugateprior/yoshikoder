package edu.harvard.wcfia.yoshikoder.ui;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNodeImpl;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class NewCategoryPanel extends NewPatternPanel {
    
    protected JTextArea description;
    
    // just adds a description field to super and overrides the action
    public NewCategoryPanel(Yoshikoder yk, CategoryNode parentnode) {
        super(yk, parentnode);
    }
    
    public void commit() throws CommitException {
        // wrap all exceptions
        try {
            if (name.getText() == null || 
                    name.getText().length()==0){
                throw new Exception(Messages.getString("noEntryName")); //$NON-NLS-1$
            }
            Double d = getScore();
            String desc = description.getText();
            
            CategoryNode node = 
                new CategoryNodeImpl(name.getText(), d, desc);	
            yoshikoder.addCategory(node, parent);
            yoshikoder.setSelectedNode(node);
            yoshikoder.setUnsavedChanges(true);

        } catch (Exception ex){
            throw new CommitException(ex);
        }
    }
    
    protected void makeGUI(){
        name = new JTextField(15);
        name.setFont(yoshikoder.getDisplayFont());
        name.addKeyListener(keyListener);
        addField(Messages.getString("nameLabel"), name); 
        
        description = new JTextArea(3,15);
        description.addKeyListener(keyListener);
        addWidgetFixedDepth(Messages.getString("descriptionLabel"),  
                new JScrollPane(description));
        
        //score = new JTextField(5);
        //score.addKeyListener(keyListener);
        //addWidgetInlineFixedWidth(Messages.getString("scoreLabel"), score);  
    }
    
}
