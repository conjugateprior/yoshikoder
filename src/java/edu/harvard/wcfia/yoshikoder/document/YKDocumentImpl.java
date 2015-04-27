package edu.harvard.wcfia.yoshikoder.document;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.util.FileUtil;

public class YKDocumentImpl extends AbstractYKDocument implements YKDocument {
	
    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.document.YKDocumentImpl");
    
	protected String text;
	
	public YKDocumentImpl(String docTitle, File f, String csname) 
		throws IOException {
		super(docTitle, f, csname);
		// load text immediately
		text = FileUtil.slurp(f, csname);
	}

	public YKDocumentImpl(String docTitle, String docText, String csname){
		super(docTitle, null);
		text = docText;
		charsetName = csname;
	}
	
	public YKDocumentImpl(String docTitle, String docText){
		super(docTitle, null);
		text = docText;
	}
	
	public String getText() throws IOException{
		log.info("Handing back the original text");
        return text;
	}

}
