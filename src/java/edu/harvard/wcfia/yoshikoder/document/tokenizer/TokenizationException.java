package edu.harvard.wcfia.yoshikoder.document.tokenizer;

/**
 * @author will
 */
public class TokenizationException extends Exception {
    
    public TokenizationException() {
        super();
    }
    
    public TokenizationException(String message) {
        super(message);
    }
    
    public TokenizationException(String message, Throwable towel){
        super(message, towel);
    }
    
    public TokenizationException(Throwable towel){
        super(towel);
    }
}
