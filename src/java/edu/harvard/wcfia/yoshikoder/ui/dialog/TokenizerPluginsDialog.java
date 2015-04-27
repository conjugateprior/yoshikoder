package edu.harvard.wcfia.yoshikoder.ui.dialog;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.ui.TokenizerPluginsPanel;
import edu.harvard.wcfia.yoshikoder.util.Messages;

	public class TokenizerPluginsDialog extends AbstractOkCancelDialog {

	    public TokenizerPluginsDialog(Yoshikoder parent) {
	        super(parent, new TokenizerPluginsPanel(parent));
		    setTitle(Messages.getString("TokenizerPluginsDialog.0")); //$NON-NLS-1$
		}

}
