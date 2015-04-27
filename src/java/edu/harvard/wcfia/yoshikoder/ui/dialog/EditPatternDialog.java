package edu.harvard.wcfia.yoshikoder.ui.dialog;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.ui.EditPatternPanel;
import edu.harvard.wcfia.yoshikoder.util.Messages;

/**
 * @author will
 */
public class EditPatternDialog extends AbstractOkCancelDialog {

	public EditPatternDialog(Yoshikoder yk, CategoryNode parent, PatternNode nodeToReplace) {
		super(yk, new EditPatternPanel(yk, parent, nodeToReplace),
				Messages.getString("PatternDialog.icon"));
		setTitle(Messages.getString("EditPatternDialog.0")); 
	}

}
