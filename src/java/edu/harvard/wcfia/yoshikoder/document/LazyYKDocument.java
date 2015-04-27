package edu.harvard.wcfia.yoshikoder.document;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.util.FileUtil;

public class LazyYKDocument extends AbstractYKDocument implements YKDocument {
	
    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.document.LazyYKDocument");
    
	protected long lastModified;

	protected SoftReference srText = new SoftReference(null);

	public LazyYKDocument(String docTitle, File f, String csname){
		super(docTitle, f, csname);
	}

	public LazyYKDocument(String docTitle, File f){
		super(docTitle, f);
	}
	
	public String getText() throws IOException {
		String txt = (String)srText.get();
		if (txt==null){
		    log.info("Loading document text from the filesystem");
			txt = loadText();
			srText = new SoftReference(txt);
		}
		return txt;
	}
	
	// update our metadata
	public void setLocation(File f){
		location = f;
	}
	
	public void clearCachedText(){
		srText.clear();
	}
	
	protected String loadText() throws IOException{
		return FileUtil.slurp(location, charsetName);
	}
	
    public String toString() {
        return title;
    }
}
