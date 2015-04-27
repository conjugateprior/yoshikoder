package edu.harvard.wcfia.yoshikoder.dictionary;

import java.util.regex.Pattern;

public class PatternNodeImpl extends NodeImpl implements PatternNode{
	
	protected Pattern pattern;
	
	public PatternNodeImpl(){
		super();
	}
	
	public PatternNodeImpl(String n){
		super(n);
	}

	public PatternNodeImpl(String n, Double sc){
		super(n, sc);
	}
	
	public PatternNodeImpl(String n, Double sc, Pattern pat){
		super(n, sc);
		pattern = pat;
	}
	
	public Pattern getPattern(){
		return pattern;
	}

	
	public void setPattern(Pattern pat){
		pattern = pat;
	}
	
	public String getPopup(){
	    return super.getPopup();
	}
	
}
