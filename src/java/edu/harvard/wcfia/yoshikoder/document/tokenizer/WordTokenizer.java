package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A tokenizer that returns either token spans, or whole tokens (backed by the text handed in)
 * 
 * @author will
 *
 */
public class WordTokenizer {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.document.tokenizer.WordTokenizer");
    
    protected Locale locale;
    protected BreakIterator wordIterator;
    
    public WordTokenizer(Locale loc){
        if (loc == null){
            locale = Locale.getDefault();
            log.info("Null handed in as Locale, using default: " + locale.toString());
        } else 
            locale = loc;
        
        wordIterator = BreakIterator.getWordInstance(locale);
    }
    
    public int [][] getTokenSpans(String txt){
        wordIterator.setText(txt);
        List list = new ArrayList();
        
        int start = wordIterator.first();
        int end = wordIterator.next();
        while (end != BreakIterator.DONE) {
            char c = txt.charAt(start);
            if (Character.isLetterOrDigit(c) || 
                    Character.getType(c)==Character.CURRENCY_SYMBOL)
                list.add(new int[]{start, end});            
            
            start = end;
            try {
                end = wordIterator.next(); // throws exceptions rarely
            } catch (Exception e) {
                log.log(Level.WARNING, 
                        "tokenization error somewhere after character " + end,
                        e);
            }
        }
        wordIterator.setText(""); // drop any references to documents we might be keeping
        return (int[][])list.toArray(new int[list.size()][2]);
    }
    
    public String[] getTokens(String txt){
        int[][] spans = getTokenSpans(txt);
        String[] s = new String[spans.length];
        for (int ii = 0; ii < s.length; ii++) {
            s[ii] = txt.substring(spans[ii][0], spans[ii][1]);
        }
        return s;
    }
    
}
