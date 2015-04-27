package edu.harvard.wcfia.yoshikoder.concordance;

import java.io.Serializable;

import edu.harvard.wcfia.yoshikoder.document.tokenizer.Token;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;

public interface ConcordanceLine extends Serializable{
	
	public TokenList getLeftHandSide(); // the tokens of the text
	public Token getTarget();
	public TokenList getRightHandSide();

    public String  getLeftHandSideView(); // the chunk of text 
    public String getTargetView();
    public String getRightHandSideView();
    
}
