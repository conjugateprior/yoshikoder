package edu.harvard.wcfia.yoshikoder.document;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.harvard.wcfia.yoshikoder.document.tokenizer.WordTokenizer;

/**
 * TokenStructuredDocument is a decorator that adds a tokenization to a regular YKDocument
 * and allows Pattern searches over the tokens.
 * <p>
 * <b>EXPERIMENTAL</b>
 * 
 * @author will
 *
 */
public class TokenStructuredDocument implements YKDocument {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.document.TokenStructuredDocument");

    protected int[][] tokenSpans;
 
    protected YKDocument instance;
    
    public TokenStructuredDocument(YKDocument document){
        instance = document;
    }
        
    /**
     * Returns how many tokens there are in the document.
     * @return
     * @throws IOException
     */
    public int getLengthInTokens() throws IOException {
        if (tokenSpans == null){
            tokenize();
        }
        return tokenSpans.length;
    }
    
    /**
     * Hands back the spans corresponding to the tokens in this document.
     * @return
     * @throws IOException
     */
    public int[][] getTokenSpans() throws IOException {
        if (tokenSpans == null){
            tokenize();
        }
        return tokenSpans;
    }

    /**
     * Hands back the tokens of this document as Strings.
     * @return
     * @throws IOException
     */
    public String[] getTokens() throws IOException {
        int[][] tspans = getTokenSpans();
        String[] tokens = new String[tspans.length];
        String txt = getText();
        for (int ii = 0; ii < tspans.length; ii++) 
            tokens[ii] = txt.substring(tspans[ii][0], tspans[ii][1]);
        return tokens;
    }
    
    /**
     * Returns spans (beginning and end indexes) defined over tokenSpan <i>rows</i>, 
     * not the document's characters.
     * This is a helper function to reduce redundant match computations when retrieving
     * pattern spans themselves, counts of them, or concordance spans around them.
     * None of the public routines do any regular expression matching.
     * <p>
     * Be careful with this one.  Row spans start with the index of the
     * first element and end with the index of the last element.  Not the index
     * of the last element + 1.  Things might well be easier if they did...
     * 
     * @param entity a sequence of regular expression to match
     * @return spans over rows of tokenSpan (beginning and end rows for each pattern sequence)
     * @throws IOException
     */
    protected int[][] getRowSpansThatMatchPattern(Pattern[] entity) throws IOException {
        if (tokenSpans == null){
            tokenize();
        }
    
        List list = new ArrayList();
        String txt = instance.getText(); // grab the text for the duration of this method
        for (int ii=0; ii < tokenSpans.length-entity.length + 1; ii++){
            boolean matches = true;
            int jj;
            for (jj=0; jj<entity.length; jj++){
                if (!entity[jj].matcher(txt.substring(tokenSpans[ii+jj][0], 
                    tokenSpans[ii+jj][1])).matches()){ 
                    matches = false;
                    break;
                }
            }
            if (matches)
                list.add(new int[]{ii, ii+jj-1});
        }
        return (int[][])list.toArray(new int[list.size()][2]);
    }
    
    /**
     * The spans corresponding to token sequences matching the Pattern sequence.  Each
     * row is a match.  The first element is the start character index of the matching
     * part of the text, and the last element is the last character index of the matching part of 
     * the text.  E.g. substring(result[2][0], result[2][1]) is the text of the second match
     * @param entity sequence of Patterns that need to be all match, in order.
     * @return
     * @throws IOException
     */
    public int[][] getPatternMatchSpans(Pattern[] entity) throws IOException {
        int[][] rowSpans = getRowSpansThatMatchPattern(entity);
        int[][] pms = new int[rowSpans.length][2];
        for (int ii=0; ii<pms.length; ii++){
            pms[ii][0] = tokenSpans[rowSpans[ii][0]][0]; // start char of first token
            pms[ii][1] = tokenSpans[rowSpans[ii][1]][1]; // end char of last token
        }
        return pms;
    }
    
    public int[][] getPatternMatchSpans(Pattern p) throws IOException {
        return getPatternMatchSpans(new Pattern[]{p});
    }
    
    /**
     * Computes how many matches there were to the sequence of patterns handed in.
     * This does not call getPatternMatchSpans first.
     * @param entity
     * @return
     * @throws IOException
     */
    public int getPatternMatchCounts(Pattern[] entity) throws IOException {
        int[][] rowSpans = getRowSpansThatMatchPattern(entity);
        return rowSpans.length;
    }
    
    public int getPatternMatchCounts(Pattern p) throws IOException {
        return getPatternMatchCounts(new Pattern[]{p});
    }
    
    /**
     * Generates a concordance from a concordance span.  A concordance is a 
     * N x 3 String array.  When no tokens can be shown on left or right hand side
     * an empty string is returned.
     * @param entity
     * @param window
     * @return
     * @throws IOException
     */
    public String[][] getPatternConcordance(Pattern[] entity, int window) throws IOException {
        int[][] pcs = getPatternConcordanceSpans(entity, window);
        String [][] s = new String[pcs.length][3];
        String txt = getText(); // note that we'll be handing out references to the text here...
        for (int ii=0; ii<pcs.length; ii++){
            if (pcs[ii][0] != -1)
                s[ii][0] = txt.substring(pcs[ii][0], pcs[ii][1]);
            else
                s[ii][0] = "";
            
            s[ii][1] = txt.substring(pcs[ii][2], pcs[ii][3]);
            
            if (pcs[ii][4] != -1)
                s[ii][2] = txt.substring(pcs[ii][4], pcs[ii][5]);
            else
                s[ii][2] = "";
        }
        return s;
    }
    
    public String[][] getPatternConcordance(Pattern pattern, int window) throws IOException {
        return getPatternConcordance(new Pattern[]{pattern}, window);
    }
    
    public int[][] getPatternConcordanceSpans(Pattern p, int window) throws IOException {
        return getPatternConcordanceSpans(new Pattern[]{p}, window);
    }
    
    /**
     * Returns the spans of the left, center, and right hand sides of each line of a
     * concordance.  <b>Note:</b> when thers is nothing to be displayed on the left
     * or right hand side, i.e. the keyword is flush to one end of the text, then the
     * spans are set to -1.
     * 
     * @param entity the pattern sequence whose context is to be found
     * @param window how many tokens either side of entity to show
     * @return 
     * @throws IOException
     */
    public int[][] getPatternConcordanceSpans(Pattern[] entity, int window) throws IOException{
        int[][] rowSpans = getRowSpansThatMatchPattern(entity);
        int[][] concSpans = new int[rowSpans.length][6];
        
        if (window < 1){ // ugly special case...
            for (int ii=0; ii<concSpans.length; ii++){
                concSpans[ii][0] = -1; 
                concSpans[ii][1] = -1; 
                concSpans[ii][2] = tokenSpans[rowSpans[ii][0]][0]; // first char of the entity
                concSpans[ii][3] = tokenSpans[rowSpans[ii][1]][1]; // last char of the entity
                concSpans[ii][4] = -1;
                concSpans[ii][5] = -1;
            }  
            return concSpans;
        }
        
        int docLength = tokenSpans.length;
        for (int ii=0; ii<concSpans.length; ii++){

            // construct the left hand side
            int lhsStartIndex = rowSpans[ii][0] - window;
            int lhsEndIndex = rowSpans[ii][0]-1; 
            if (lhsStartIndex >= 0){
                concSpans[ii][0] = tokenSpans[lhsStartIndex][0]; // beginning of lhs
                concSpans[ii][1] = tokenSpans[lhsEndIndex][1];   // end of lhs
            } else if (lhsStartIndex > -window){
                concSpans[ii][0] = tokenSpans[0][0];           // beginning of first token
                concSpans[ii][1] = tokenSpans[lhsEndIndex][1]; // end of lhs
            } else if (lhsStartIndex == -window){
                concSpans[ii][0] = -1; // flag that there is nothing to show on the left
                concSpans[ii][1] = -1;                
            } else {
                log.info("Should never get here (lhs)");
            }
            
            //          target pattern is easy
            concSpans[ii][2] = tokenSpans[rowSpans[ii][0]][0]; // first char of the entity
            concSpans[ii][3] = tokenSpans[rowSpans[ii][1]][1]; // last char of the entity

            // construct the right hand side
            int rhsStartIndex = rowSpans[ii][1] + 1;            
            int rhsEndIndex = rowSpans[ii][1] + window;
            if (rhsEndIndex < docLength){
                concSpans[ii][4] = tokenSpans[rhsStartIndex][0]; // beginning of rhs
                concSpans[ii][5] = tokenSpans[rhsEndIndex][1];   // end of rhs
            } else if (rhsEndIndex < docLength + window -1 ){
                concSpans[ii][4] = tokenSpans[rhsStartIndex][0];        // beginning of token after target
                concSpans[ii][5] = tokenSpans[docLength-1][1];  // end of tokens
            } else if (rhsEndIndex == docLength + window - 1){ 
                concSpans[ii][4] = -1; // flag that there is nothing to show on the left
                concSpans[ii][5] = -1;                
            } else {
                log.info("Should never get here (rhs)");
            }
        }
        return concSpans;
    }
 
    /**
     * Tokenizes the document using a WordTokenizer.  This is not the final version.
     *
     */
    protected void tokenize() throws IOException {
        String txt = getText();
        WordTokenizer tok = new WordTokenizer(getLocale());
        tokenSpans = tok.getTokenSpans(txt);
    }
    
    /**
     * Triggers a fresh tokenization which updates all the token spans.
     * Useful for when a new tokenizer is available for this document's locale.
     * @throws IOException
     */
    public void retokenize() throws IOException {
        tokenize();
    }
    
    /**
     * Dumps the document's tokenization to String, creating first if necessary.
     * @return
     * @throws IOException
     */
    public String debugToString() throws IOException{
        StringBuffer sb = new StringBuffer();
        if (tokenSpans == null)
            tokenize();
        for (int ii=0; ii<tokenSpans.length; ii++){
            int start = tokenSpans[ii][0];
            int end = tokenSpans[ii][1];
            sb.append(ii + ": " + 
                    getText().substring(start, end) + 
                    " [" + start + "," + end + "]\n");
        }
        return sb.toString();
    }
    
    // delegate YKDocument methods and toString to the instance
    public String getCharsetName() { return instance.getCharsetName(); }
    public Locale getLocale() { return instance.getLocale(); }
    public File getLocation() { return instance.getLocation(); }
    public Font getPreferredFont() { return instance.getPreferredFont(); }
    public String getText() throws IOException { return instance.getText(); }
    public String getTitle() { return instance.getTitle(); }
    public void setCharsetName(String csname) { instance.setCharsetName(csname); }
    public void setLocale(Locale loc) { instance.setLocale(loc); }
    public void setLocation(File f) { instance.setLocation(f); }
    public void setPreferedFont(Font f) { instance.setPreferedFont(f); }
    public void setTitle(String title) { instance.setTitle(title); }
    
    public String toString(){ return instance.toString(); }
    
    public static void main(String[] args) throws Exception {
        File f = new File("/Users/will/spantest.txt");
        TokenStructuredDocument doc = new TokenStructuredDocument(new LazyYKDocument("span test", f));
        System.out.println(doc.debugToString());
        
        Pattern[] pats = new Pattern[]{Pattern.compile("second"), Pattern.compile("third")};
        int[][] spans = doc.getPatternMatchSpans(pats);
        System.out.println(spans.length);
        for (int ii=0; ii<spans.length; ii++){
            System.out.println(doc.getText().substring(spans[ii][0], spans[ii][1]));
        }
        int[][] concSpans = doc.getPatternConcordanceSpans(pats, 0);
        for (int ii=0; ii<concSpans.length; ii++){
            if (concSpans[ii][0] + concSpans[ii][1] != -2)
                System.out.print(doc.getText().substring(concSpans[ii][0], concSpans[ii][1]));
            System.out.print(" [ " + doc.getText().substring(concSpans[ii][2], concSpans[ii][3]) + " ] ");
            if (concSpans[ii][4] + concSpans[ii][5] != -2)
                System.out.println(doc.getText().substring(concSpans[ii][4], concSpans[ii][5]));
        }

        String[][] pc = doc.getPatternConcordance(pats, 5);
        JTable table = new JTable(pc, new String[]{"LHS", "Target", "RHS"});
        JOptionPane.showMessageDialog(null, new JScrollPane(table));
        System.exit(0);
    }
    
}
