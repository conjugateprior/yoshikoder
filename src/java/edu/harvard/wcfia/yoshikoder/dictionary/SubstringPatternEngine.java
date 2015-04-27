package edu.harvard.wcfia.yoshikoder.dictionary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Generates a regular expression corresponding to simple pattern language.
 * Patterns generate exact matches, unless an asterisk is present, when
 * any number of non-whitespace characters are permitted (regexp: \S*).
 * The asterisk cannot be escaped.
 * 
 * @author will
 */
public class SubstringPatternEngine implements PatternEngine {
    
    protected int reFlags;
    
    public SubstringPatternEngine(){
        reFlags = Pattern.CASE_INSENSITIVE;
    }
    
    public String getType(){
        return PatternEngine.SUBSTRING;
    }
    
    public int getReFlags(){
        return reFlags;
    }
    
    public void setReFlags(int ref){
        reFlags = ref;
    }
    
    /**
     * Converts a candidate string into regular expression that looks for
     * case-insensitive word-internal exact matches, or substring matches
     * if * is applied in the pattern.
     */
    public Pattern makeRegexp(String pstring) throws PatternSyntaxException{
        String escaped = escape(pstring);
        return Pattern.compile(escaped, reFlags);
    }
    
    private String escape(String pstring){
        StringBuffer sb = new StringBuffer();
        char[] pchar = pstring.toCharArray();
        for (int ii=0; ii<pchar.length; ii++){
            if (pchar[ii] == '*'){
                if (ii == 0)
                    sb.append("\\S*\\Q");
                else if (ii == pchar.length-1)
                    sb.append("\\E\\S*"); 
                else 
                    sb.append("\\E\\S*\\Q"); // end quot \S* start quot
            } else {
                if (ii == 0)
                    sb.append("\\b\\Q");   // add prefix if at start 
                sb.append(pchar[ii]);      // add letter
                if (ii == pchar.length-1)  // add suffix if at end
                    sb.append("\\E\\b"); 
            }
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
        SubstringPatternEngine engine = new SubstringPatternEngine();
        Pattern p = engine.makeRegexp("*f*k*");
        System.out.println(p.pattern());
        Matcher m = p.matcher("fgk"); 
        System.out.println(m.matches());
        
    }
    
}
