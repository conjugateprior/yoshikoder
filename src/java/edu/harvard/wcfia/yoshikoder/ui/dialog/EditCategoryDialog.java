package edu.harvard.wcfia.yoshikoder.ui.dialog;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.ui.EditCategoryPanel;
import edu.harvard.wcfia.yoshikoder.util.Messages;

/**
 * @author will
 */
public class EditCategoryDialog extends AbstractOkCancelDialog {

	public EditCategoryDialog(Yoshikoder yk, CategoryNode parent, CategoryNode nodeToReplace) {
		super(yk, new EditCategoryPanel(yk, parent, nodeToReplace),
				Messages.getString("CategoryDialog.icon"));
		setTitle(Messages.getString("EditCategoryDialog.0")); 
	}
}
