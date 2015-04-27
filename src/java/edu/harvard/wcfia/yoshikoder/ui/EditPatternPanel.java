package edu.harvard.wcfia.yoshikoder.ui;

import java.util.regex.Pattern;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNodeImpl;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class EditPatternPanel extends NewPatternPanel {
    
    protected PatternNode nodeToEdit;
    
    public EditPatternPanel(Yoshikoder yk, 
            CategoryNode parentnode,
            PatternNode node) {
        super(yk, parentnode);
        nodeToEdit = node;
        
        // adjust UI
        name.setText(nodeToEdit.getName());
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
            Pattern p = 
                yoshikoder.getDictionary().getPatternEngine()
                .makeRegexp(name.getText());
            
            Double d = getScore();
            PatternNode node = 
                new PatternNodeImpl(name.getText(), d, p);
            if (!node.equals(nodeToEdit)){
                yoshikoder.replaceNode(nodeToEdit, node);
                yoshikoder.setSelectedNode(node);
                yoshikoder.setUnsavedChanges(true);
            }
        } catch (Exception ex){
            throw new CommitException(ex);
        }
    }
    
    
}
