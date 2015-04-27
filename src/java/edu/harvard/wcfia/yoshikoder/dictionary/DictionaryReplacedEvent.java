package edu.harvard.wcfia.yoshikoder.dictionary;

import java.beans.PropertyChangeEvent;


/**
 * @author will
 */
public class DictionaryReplacedEvent extends PropertyChangeEvent {

    public DictionaryReplacedEvent(Object source,
            YKDictionary oldDict, YKDictionary dict) {
        super(source, "dictionary_replaced", oldDict, dict); //$NON-NLS-1$
    }

}
