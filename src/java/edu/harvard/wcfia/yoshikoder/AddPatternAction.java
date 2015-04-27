package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.ui.dialog.NewPatternDialog;

public class AddPatternAction extends YoshikoderAction {

    public AddPatternAction(Yoshikoder yk) {
        super(yk, AddPatternAction.class.getName());

    }

    public void actionPerformed(ActionEvent e) {
        Node node = yoshikoder.getSelectedNode();
        if ((node != null) && (node instanceof CategoryNode)){
            JDialog dia = new NewPatternDialog(yoshikoder, (CategoryNode)node);
            dia.setVisible(true);
        }
    }

}
