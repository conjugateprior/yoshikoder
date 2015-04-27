package edu.harvard.wcfia.yoshikoder;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import edu.harvard.wcfia.yoshikoder.dictionary.DictionaryReplacedEvent;
import edu.harvard.wcfia.yoshikoder.dictionary.SimpleDictionary;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.document.DocumentAddedEvent;
import edu.harvard.wcfia.yoshikoder.document.DocumentChangedEvent;
import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.DocumentListImpl;
import edu.harvard.wcfia.yoshikoder.document.DocumentRemovedEvent;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.YKDocumentFactory;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.ExportUtil;

public class YKProject {

	protected DocumentList documentList;
	protected YKDictionary dictionary;
	
	//protected String name;
	protected String description;
	protected File location;
	
	protected Font displayFont = DialogUtil.getDefaultFont();
	
	protected PropertyChangeSupport pceListeners = 
	    new PropertyChangeSupport(this);
	
	public YKProject(String pname, String pdesc, DocumentList dlist){
	    dictionary = new SimpleDictionary(pname);	    
	    documentList = dlist;
	    setDescription(pdesc);
	}
	
	public YKProject(String pname, String pdesc){
	    dictionary = new SimpleDictionary(pname);
	    setDescription(pdesc);
	    documentList = new DocumentListImpl();
	}
	
    public YKProject() {
        documentList = new DocumentListImpl();
        dictionary = new SimpleDictionary();
        setName("Untitled"); //$NON-NLS-1$
        setDescription("New dictionary"); //$NON-NLS-1$
    }
		
    public String getName(){
        return dictionary.getName();
    }
    
    public void setName(String pname){
        dictionary.setName(pname);
    }
    
    public String getDescription(){
        return dictionary.getDictionaryRoot().getDescription();
    }
    
    public void setDescription(String desc){
    	dictionary.getDictionaryRoot().setDescription(desc);
    }
    
    public File getLocation(){
        return location;
    }
    
    public void setLocation(File f){
        location = f;
    }
    
    public YKDictionary getDictionary() {
        return dictionary;
    }
    
    public void setDictionary(YKDictionary dict) {
        YKDictionary oldValue = dictionary;
        dictionary = dict;
        DictionaryReplacedEvent dre = 
            new DictionaryReplacedEvent(this, oldValue, dictionary);
        pceListeners.firePropertyChange(dre);
    }
    
    public DocumentList getDocumentList() {
        return documentList;
    }

    public void replaceDocument(YKDocument oldDoc, YKDocument newDoc){
        int index = documentList.indexOf(oldDoc);
        documentList.remove(index);
        documentList.add(index, newDoc);
        DocumentChangedEvent dce = 
        	new DocumentChangedEvent(this, oldDoc, newDoc);
        pceListeners.firePropertyChange(dce);
    }
    
    public void addDocument(YKDocument doc){
    	documentList.add(doc);
        DocumentAddedEvent dae = new DocumentAddedEvent(this, doc);
        pceListeners.firePropertyChange(dae);
    }
    
    public void removeDocument(YKDocument doc){
        if (doc != null){
            documentList.remove(doc);
            DocumentRemovedEvent dre = new DocumentRemovedEvent(this, doc);
            pceListeners.firePropertyChange(dre);
        }
    }

	/*
    public void setWindowSize(int i){
	    getDictionary().setWindowSize(i);
	}
	
	public int getWindowSize(){
	    return getDictionary().getWindowSize();
	}
	*/
	
	//	 The listener list wrapper methods.
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        pceListeners.addPropertyChangeListener(listener);
    }
    
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        pceListeners.removePropertyChangeListener(listener);
    }

    public static void main(String[] args) {
        try {
            YKProject pr = new YKProject();
            pr.addPropertyChangeListener(new PropertyChangeListener(){
                public void propertyChange(PropertyChangeEvent evt) {
                    System.out.println("caught a " + evt.getPropertyName() +  //$NON-NLS-1$
                            " event"); //$NON-NLS-1$
                }
            });	
            YKDocument d = 
                YKDocumentFactory.createYKDocument(new File("~/louise_blurb.txt")); //$NON-NLS-1$
            pr.addDocument(d);
            YKDocument f = 
                YKDocumentFactory.createYKDocument(new File("~/prev.txt")); //$NON-NLS-1$
            pr.replaceDocument(d, f);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

	public Font getDisplayFont() {
		return displayFont;
	}

	public void setDisplayFont(Font dFont) {
		displayFont = dFont;
	}
    
    public void saveAsXml(File f) throws IOException{
    	System.err.println("saving myself as xml to file " + f.getAbsolutePath());
        ExportUtil.exportAsXML(this, f);
    }
    
    public void saveAsHtml(File f) throws IOException{
        ExportUtil.exportAsHTML(this, f);
    }
    
}
