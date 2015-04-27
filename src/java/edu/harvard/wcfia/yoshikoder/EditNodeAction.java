package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.ui.dialog.EditCategoryDialog;
import edu.harvard.wcfia.yoshikoder.ui.dialog.EditPatternDialog;

public class EditNodeAction extends YoshikoderAction {

    public EditNodeAction(Yoshikoder yk) {
        super(yk, EditNodeAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        Node node = yoshikoder.getSelectedNode();
        if (node != null){
            if (node instanceof CategoryNode) {
                CategoryNode n = (CategoryNode)node;
                JDialog dia = 
                    new EditCategoryDialog(yoshikoder, (CategoryNode)n.getParent(), n);
                dia.show();
                
            } else if (node instanceof PatternNode) {
                PatternNode n = (PatternNode)node;
                JDialog dia = 
                    new EditPatternDialog(yoshikoder, (CategoryNode)n.getParent(), n);
                dia.show();
            } 
        }
    }

}
