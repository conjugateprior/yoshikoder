package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.io.File;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.util.FileUtil;

/**
 * Breaks a text into sentence spans.  Note that this will leave trailing newlines (paragraph
 * breaks etc.) in its current incarnation.  These should be fixed.
 * 
 * @author will
 *
 */
public class SentenceTokenizer {
    
    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.document.tokenizer.SentenceTokenizer");
    
    protected Locale locale;
    protected BreakIterator sentenceIterator;
    
    public SentenceTokenizer(Locale loc){
        if (loc == null){
            locale = Locale.getDefault();
            log.info("Null handed in as Locale, using default: " + locale.toString());
        } else 
            locale = loc;
        
        sentenceIterator = BreakIterator.getSentenceInstance(locale);
    }
    
    public int [][] getTokenSpans(String txt){
        sentenceIterator.setText(txt);
        List list = new ArrayList();
        
        int start = sentenceIterator.first();
        int end = sentenceIterator.next();
        while (end != BreakIterator.DONE) {
            if (Character.isLetterOrDigit( txt.charAt(start) ))
                list.add(new int[]{start, end});            

            start = end;
            try {
                end = sentenceIterator.next(); // throws exceptions rarely
            } catch (Exception e) {
                log.log(Level.WARNING, 
                        "tokenization exception somewhere after character " + end,
                        e);
            }
        }
        sentenceIterator.setText(""); // drop any document references we might be keeping
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
    
    public static void main(String[] args) throws Exception {
        File f = new File("/Users/will/review.txt");
        String txt = FileUtil.slurp(f);
        txt = txt.replace('\r', '\n');
        WordTokenizer tok = new WordTokenizer(null);
        String[] spans = tok.getTokens(txt);
        
        for (int ii = 0; ii < spans.length; ii++) {
          System.out.println(ii + ": " + spans[ii] + "]");
        }
    }
    
}
