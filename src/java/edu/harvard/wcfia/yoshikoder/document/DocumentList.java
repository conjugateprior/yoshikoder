package edu.harvard.wcfia.yoshikoder.document;

import java.io.Serializable;
import java.util.List;

/**
 * @author will
 */
public interface DocumentList extends List, Serializable{

    /**
     * Walks this document list and returns any document references 
     * that have been deleted or moved since its construction.
     */
    public List validate();

    /**
     * Update the list's documents with these new ones, whose metadata
     * is up to date.
     */
    public void update(YKDocument doc);
    
}