package edu.harvard.wcfia.yoshikoder.document;

import java.beans.PropertyChangeEvent;


/**
 * @author will
 */
public class DocumentAddedEvent extends PropertyChangeEvent {

    public DocumentAddedEvent(Object s, YKDocument newDoc) {
        super(s, "document_added", null, newDoc);
    }

}
