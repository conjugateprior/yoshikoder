package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.util.List;

import edu.harvard.wcfia.yoshikoder.reporting.WordFrequencyMap;

public interface TokenList extends List {
	
	WordFrequencyMap getWordFrequencyMap();
	
}
