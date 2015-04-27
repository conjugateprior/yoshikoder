package edu.harvard.wcfia.yoshikoder.document;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import edu.harvard.wcfia.yoshikoder.util.FileUtil;

abstract public class AbstractYKDocument implements YKDocument {
	
	protected String title;
	protected String charsetName;
	protected File location;
	protected Locale locale;
	protected Font preferredFont;
	
	public AbstractYKDocument(String docTitle, File f, String csname){
		title = docTitle;
		location = f;
		charsetName = csname;
		locale = Locale.getDefault();
	}
	
	public AbstractYKDocument(String docTitle, File f){
		this(docTitle, f, FileUtil.systemEncoding); 
	}
	
	public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale loc) {
        locale = loc;
    }
    
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String newTitle){
		title = newTitle;
	}
	
	// implemented lazily in subclasses
	abstract public String getText() throws IOException;
	
	public String getCharsetName(){
		return charsetName;
	}
	
	public void setCharsetName(String csname){
		charsetName = csname;
	}

	public File getLocation(){
		return location;
	}

	public void setLocation(File f){
		location = f;
	}

	/**
	 * Depends only on title and file location.  Two files that have the same title and
     * null locations are equal.
	 */
	public boolean equals(Object o){
	    try {
	        AbstractYKDocument d = (AbstractYKDocument)o;
	        if (title.equals(d.getTitle()))
                if ((location != null) && (d.getLocation() != null))
                    return location.equals(d.getLocation()); // same non-null location
                else if ((location == null) && (d.getLocation()==null))
                    return true; // same name, both null location
        } catch (ClassCastException e){}
	    return false;
	}
	
    public int hashCode(){
        if (location != null)
            return location.hashCode() + title.hashCode();
        else
            return title.hashCode();
    }
    
	public void setPreferedFont(Font f){
		preferredFont = f;
	}
	
	public Font getPreferredFont(){
		return preferredFont;
	}
	
	public String toString(){
	    return "\"" + getTitle() + "\"";
	}
}
