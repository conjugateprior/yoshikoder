package edu.harvard.wcfia.yoshikoder.dictionary;

public class DuplicateException extends Exception {

	public DuplicateException() {
        super();
    }
 
    public DuplicateException(String message) {
        super(message);
    }
 
    public DuplicateException(Throwable cause) {
        super(cause);
    }
 
    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

}
