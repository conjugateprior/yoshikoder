package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;

public class EditDocumentAction extends YoshikoderAction {

	public EditDocumentAction(Yoshikoder yk) {
		super(yk, EditDocumentAction.class.getName());
	}

	public void actionPerformed(ActionEvent e) {
		yoshikoder.editDocument();
	}
}
