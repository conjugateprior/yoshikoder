package edu.harvard.wcfia.yoshikoder.ui.dialog;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.ui.PreferencePanel;
import edu.harvard.wcfia.yoshikoder.util.Messages;

/**
 * @author will
 */
public class PreferencesDialog extends AbstractOkCancelDialog {

    public PreferencesDialog(Yoshikoder parent) {
        super(parent, new PreferencePanel(parent));
	    setTitle(Messages.getString("PreferencesDialog.0")); 
	}

}
