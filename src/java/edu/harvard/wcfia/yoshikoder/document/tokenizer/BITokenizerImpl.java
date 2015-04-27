package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.text.BreakIterator;
import java.util.Locale;

/**
 * @author will
 */
public class BITokenizerImpl implements Tokenizer{
    
    private Locale locale;
    private BreakIterator wordIterator;
    
    public BITokenizerImpl(Locale loc){
        locale = loc;
    }
    
    public BITokenizerImpl() {
        this(Locale.getDefault());
    }
    
    public Locale[] getLocales(){
        return new Locale[]{locale};
    }
    
    /**
     * @return Returns the locale.
     */
    public Locale getLocale() {
        return locale;
    }
    
    /**
     * @param defaultLocale The locale to set.
     */
    public void setLocale(Locale defaultLocale) {
        locale = defaultLocale;
    }
    
    public TokenList getTokens(String txt){
        TokenList tokens = new TokenListImpl();
        
        wordIterator = BreakIterator.getWordInstance(locale);
        //wordIterator = BreakIterator.getWordInstance();
        wordIterator.setText(txt);
        int start = wordIterator.first();
        int end = wordIterator.next();
        String word;
        while (end != BreakIterator.DONE) {
            int startoffset = start; // keep hold of this
            word = txt.substring(start, end);
            int endoffset = startoffset + word.length();
            
            start = end;
            // throws a runtime very rarely for strange characters.
            try {
                end = wordIterator.next();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            char c = word.charAt(0);
            if (Character.isLetterOrDigit(c) || 
                    Character.getType(c)==Character.CURRENCY_SYMBOL)
                tokens.add(new TokenImpl(word.intern(), startoffset, endoffset));
        }
        
        return tokens;
    }
    
}
