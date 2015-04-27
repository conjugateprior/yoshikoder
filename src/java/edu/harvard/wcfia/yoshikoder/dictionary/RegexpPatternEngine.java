package edu.harvard.wcfia.yoshikoder.dictionary;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexpPatternEngine implements PatternEngine {
    
    protected int reFlags;
    
    public RegexpPatternEngine(){
        reFlags = Pattern.CASE_INSENSITIVE;
    }
    
    public String getType(){
        return PatternEngine.REGEXP;
    }
    
    public int getReFlags(){
        return reFlags;
    }
    
    public void setReFlags(int ref){
        reFlags = ref;
    }
    
    /**
     * Converts a candidate string into an ordinary regular expression.
     */
    public Pattern makeRegexp(String pstring) throws PatternSyntaxException{
        return Pattern.compile(pstring, reFlags);
    }
}
