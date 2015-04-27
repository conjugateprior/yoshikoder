package edu.harvard.wcfia.yoshikoder.document;

import java.beans.PropertyChangeEvent;


/**
 * @author will
 */
public class DocumentChangedEvent extends PropertyChangeEvent {

    public DocumentChangedEvent(Object source, YKDocument doc, YKDocument newDoc) {
        super(source, "document_changed", doc, newDoc); //$NON-NLS-1$
    }

}
