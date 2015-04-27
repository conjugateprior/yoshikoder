package edu.harvard.wcfia.yoshikoder.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.harvard.wcfia.yoshikoder.util.Messages;

public class DocumentListImpl extends ArrayList implements DocumentList{

    /**
     * Creates a DocumentListImpl.  DocumentLists are intended to store
     * YKDocuments only.
     *
     */
    public DocumentListImpl() {
        super();
    }
	
    public DocumentListImpl(DocumentList list) {
        super();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            YKDocument doc = (YKDocument)iter.next();
            add(doc);
        }
    }
        
    public DocumentListImpl(YKDocument doc){
        super();
        add(doc);
    }
    
    /**
     * Overrides add to catch non YKDocument insertions.
     */
    public boolean add(Object o) {
        if (o instanceof YKDocument)
            return super.add(o);	
        else
            throw new IllegalArgumentException(Messages.getString("DocumentListImpl.argumentException") +  //$NON-NLS-1$
                    "a non YKDocument"); //$NON-NLS-1$
    }
    
    /**
     * Walks this document list and returns any files that
     * cannot be found since their references were inserted into
     * the list.
     * 
     * @return list of deleted or moved documents references.
     */
	public List validate(){
		List l = new ArrayList();
		for (Iterator it=iterator(); it.hasNext();){
			YKDocument doc = (YKDocument)it.next();
			if (!doc.getLocation().exists())
			    l.add(doc);
		}
		return l; // list of docs with locations changed in the meantime.
	}
	
	/**
	 * Replaces the document in this list sharing doc's title,
	 * locale, and charset name with doc.  This method provides a
	 * way to recover from validation failures.
	 * <p>
	 * Note: This replacement function does not check the text
	 * in case it cannot be retrieved (the usual case when the 
	 * underlying document has been moved or deleted) or the file
	 * location (since that will be wrong)
	 *  
	 * @param doc
	 */
	public void update(YKDocument doc){
	    int index = 0;
        for (Iterator usit = iterator(); usit.hasNext();){
            YKDocument ourdoc = (YKDocument)usit.next();
            if (ourdoc.getTitle().equals(doc.getTitle()) && 
                ourdoc.getLocale().equals(doc.getLocale()) &&
                ourdoc.getCharsetName().equals(doc.getCharsetName())){
                break;    
            }
            index += 1;
        }
        set(index, doc);
	}
	
}
