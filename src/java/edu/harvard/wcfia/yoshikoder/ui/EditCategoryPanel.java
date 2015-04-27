package edu.harvard.wcfia.yoshikoder.ui;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNodeImpl;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class EditCategoryPanel extends NewCategoryPanel {
    
    protected CategoryNode nodeToEdit;
    
    public EditCategoryPanel(Yoshikoder yk, 
            CategoryNode parentnode,
            CategoryNode node) {
        super(yk, parentnode);
        nodeToEdit = node;
        
        // adjust UI
        name.setText(nodeToEdit.getName());
        description.setText(nodeToEdit.getDescription());
        if (nodeToEdit.getScore() != null)
            score.setText(nodeToEdit.getScore().toString());
    }
    
    public void commit() throws CommitException {
        // wrap all exceptions
        try {
            if (name.getText() == null || 
                    name.getText().length()==0){
                throw new Exception(Messages.getString("noEntryName"));
            }
            String desc = description.getText();
            Double d = getScore();
            CategoryNode node = 
                new CategoryNodeImpl(name.getText(), d, desc);
            
            if (!nodeToEdit.equals(node)){            
                yoshikoder.replaceNode(nodeToEdit, node);
                yoshikoder.setSelectedNode(node);
                yoshikoder.setUnsavedChanges(true);
            }           
        } catch (Exception ex){
            throw new CommitException(ex);
        }
    }
    
}
