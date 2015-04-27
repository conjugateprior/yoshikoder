package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.util.ArrayList;

import org.jfree.util.Log;

import edu.harvard.wcfia.yoshikoder.reporting.WordFrequencyMap;

public class TokenListImpl extends ArrayList implements TokenList {
	
	WordFrequencyMap map;
	
	public TokenListImpl(){
        super();
    }
    
    public TokenListImpl(int size){
        super(size);
    }
    
    @Override
    public WordFrequencyMap getWordFrequencyMap() {
    	if (map == null){
    		Log.info("computing a wordfrequencymap for the tokenlist");
    		map = new WordFrequencyMap(this);
    		
    	}
    	return map;
    }
    
}
