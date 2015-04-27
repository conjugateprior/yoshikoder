package edu.harvard.wcfia.yoshikoder.dictionary;

import java.util.regex.Pattern;

public interface PatternNode extends Node{
	
	public Pattern getPattern();
	
    public void setPattern(Pattern re); 
	
}
