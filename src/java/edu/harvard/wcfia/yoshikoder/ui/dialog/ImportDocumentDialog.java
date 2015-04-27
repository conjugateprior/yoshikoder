package edu.harvard.wcfia.yoshikoder.ui.dialog;

import java.io.File;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.ui.ImportDocumentPanel;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class ImportDocumentDialog extends AbstractOkCancelDialog {

	public ImportDocumentDialog(Yoshikoder yk, File f) {
		super(yk, new ImportDocumentPanel(yk, f));
		setTitle(Messages.getString("ImportDocumentDialog.0"));
	}

}
