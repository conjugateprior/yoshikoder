package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;

import edu.harvard.wcfia.yoshikoder.document.YKDocument;

public class RemoveDocumentAction extends YoshikoderAction {

    public RemoveDocumentAction(Yoshikoder yk) {
        super(yk, RemoveDocumentAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        YKDocument[] doc = yoshikoder.getSelectedDocuments();
        if (doc == null || doc.length == 0)
            return;
        
        for (int ii = 0; ii < doc.length; ii++) {
            yoshikoder.removeDocument(doc[ii]);			
		}
        yoshikoder.setUnsavedChanges(true);
    }

}
