package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import edu.harvard.wcfia.yoshikoder.document.YKDocument;

public interface TokenizationCache {

    public TokenList getTokenList(YKDocument doc);
    
    public void putTokenList(YKDocument doc, TokenList tl);
    
    public void removeTokenList(YKDocument doc);
    
    public void clear();
    
}