package edu.harvard.wcfia.yoshikoder.ui.dialog;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.ui.NewCategoryPanel;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class NewCategoryDialog extends AbstractOkCancelDialog {

	public NewCategoryDialog(Yoshikoder yk, CategoryNode parent) {
		super(yk, new NewCategoryPanel(yk, parent), 
				Messages.getString("CategoryDialog.icon"));
		setTitle(Messages.getString("NewCategoryDialog.0")); 
	}

}
