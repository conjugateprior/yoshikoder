package edu.harvard.wcfia.yoshikoder.document;

import java.io.File;

import edu.harvard.wcfia.yoshikoder.util.Messages;

public class DocumentModifiedException extends DocumentTextException {

	protected long lastModified;
	protected File file;

	public DocumentModifiedException() {
        super();
    }

	public DocumentModifiedException(File f, long lastMod) {
        super(f + " " + 
        	Messages.getString("DocumentModifiedException.modifiedMessage"));
		lastModified = lastMod;
		file = f;
    }
 
    public DocumentModifiedException(String message) {
        super(message);
    }

}
