package edu.harvard.wcfia.yoshikoder.dictionary;

import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author will
 */
public interface PatternEngine extends Serializable{

	public final String SUBSTRING = "substring"; 
	public final String REGEXP = "regexp";

    public String getType();
	
    /**
     * Compile an appropriate regular expression from a string.
     * @param pstring
     * @return regular expression
     */
    public Pattern makeRegexp(String pstring) throws PatternSyntaxException;
    
}
