package edu.harvard.wcfia.yoshikoder.ui.dialog;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.ui.NewPatternPanel;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class NewPatternDialog extends AbstractOkCancelDialog {

	public NewPatternDialog(Yoshikoder yk, CategoryNode parent) {
		super(yk, new NewPatternPanel(yk, parent),
				Messages.getString("PatternDialog.icon"));
		setTitle(Messages.getString("NewPatternDialog.0")); 
	}

}
