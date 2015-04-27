package edu.harvard.wcfia.yoshikoder.ui.dialog;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.ui.NewProjectPanel;
import edu.harvard.wcfia.yoshikoder.util.Messages;

/**
 * @author will
 */
public class NewProjectDialog extends AbstractOkCancelDialog {

	public NewProjectDialog(Yoshikoder parent) {
        super(parent, new NewProjectPanel(parent));
        setTitle(Messages.getString("NewProjectDialog.0")); 
	}

}
