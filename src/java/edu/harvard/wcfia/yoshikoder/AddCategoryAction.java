package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.ui.dialog.NewCategoryDialog;

public class AddCategoryAction extends YoshikoderAction {

    public AddCategoryAction(Yoshikoder yk) {
        super(yk, AddCategoryAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        Node n = yoshikoder.getSelectedNode();
        if ((n != null) && (n instanceof CategoryNode)) {
            JDialog dia = new NewCategoryDialog(yoshikoder, (CategoryNode)n);
            dia.setVisible(true);
        }
    }

}
