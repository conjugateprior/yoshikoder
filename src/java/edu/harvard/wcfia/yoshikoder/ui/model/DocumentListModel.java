package edu.harvard.wcfia.yoshikoder.ui.model;

import java.util.Iterator;

import javax.swing.DefaultListModel;

import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.ui.DocumentState;

/**
 * @author will
 */
public class DocumentListModel extends DefaultListModel{
    
    public DocumentListModel(DocumentList dl) {
        super();
        for (Iterator iter = dl.iterator(); iter.hasNext();) {
            YKDocument doc = (YKDocument)iter.next();
            addElement(new DocumentState(doc));
        }        
    }
        
}
