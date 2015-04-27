package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.util.Locale;

/**
 * @author will
 */
public interface Tokenizer {
    
    public TokenList getTokens(String txt) throws TokenizationException;
    
    public Locale[] getLocales(); // which locales is this suitable for?
    
}
