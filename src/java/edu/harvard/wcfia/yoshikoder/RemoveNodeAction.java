package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;

public class RemoveNodeAction extends YoshikoderAction {

    public RemoveNodeAction(Yoshikoder yk) {
        super(yk, RemoveNodeAction.class.getName());
    }

    // select the next child down, or the parent, in that order.
    // there always will be a parent
    protected Node chooseNextSelection(Node n){
        Node parent = (Node)n.getParent();
        int index = parent.getIndex(n);
        if ((index+1) < parent.getChildCount())
            return (Node)parent.getChildAt(index+1);
        else 
            return parent;
    }
    
    public void actionPerformed(ActionEvent e) {
        Node n = yoshikoder.getSelectedNode();
        if (n != null){
            if ((n instanceof PatternNode) || 
                    ((n instanceof CategoryNode) && (n.getParent() != null))){
                Node selected = chooseNextSelection(n);
                yoshikoder.getDictionary().remove(n);
                yoshikoder.setSelectedNode(selected);
                yoshikoder.setUnsavedChanges(true);
            }
        }
    }

}
