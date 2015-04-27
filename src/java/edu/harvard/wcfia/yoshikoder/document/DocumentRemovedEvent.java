package edu.harvard.wcfia.yoshikoder.document;

import java.beans.PropertyChangeEvent;


/**
 * @author will
 */
public class DocumentRemovedEvent extends PropertyChangeEvent {

    public DocumentRemovedEvent(Object s, YKDocument doc) {
        super(s, "document_removed", doc, null); 
    }
}
