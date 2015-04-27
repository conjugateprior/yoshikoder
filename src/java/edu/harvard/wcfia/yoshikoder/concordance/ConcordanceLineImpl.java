package edu.harvard.wcfia.yoshikoder.concordance;

import java.util.Iterator;

import edu.harvard.wcfia.yoshikoder.document.tokenizer.Token;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;


public class ConcordanceLineImpl implements ConcordanceLine {

	private TokenList lhs; // Strings
	private Token target; 
	private TokenList rhs; // Strings
	
	public ConcordanceLineImpl(TokenList left, Token targ, TokenList right){
		// toss backing arrays here?
		lhs = left;
		target = targ;
		rhs = right;
	}

	public TokenList getLeftHandSide(){
		return lhs;
	} 

	public TokenList getRightHandSide(){
		return rhs;
	}

	public Token getTarget(){
		return target;
	}
	
    public String getLeftHandSideView(){
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = lhs.iterator(); iter.hasNext();) {
            Token el = (Token) iter.next();
            sb.append(el.getText() + " ");
        } 
        if (sb.length()>0)
            sb.deleteCharAt(sb.length()-1);
        return sb.toString();        
    }

    public String getRightHandSideView(){
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = rhs.iterator(); iter.hasNext();) {
            Token el = (Token) iter.next();
            sb.append(el.getText() + " ");
        } 
        if (sb.length()>0)
            sb.deleteCharAt(sb.length()-1);
        
        return sb.toString();        
    }
    
    public String getTargetView(){
        String s = getTarget().getText();
        if (s==null)
            return "";
        else
            return s;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("["); 
        for (Iterator iter = lhs.iterator(); iter.hasNext();) {
            Token word = (Token) iter.next();
            sb.append(word.getText() + " "); 
        }
        sb.append("[" + target.getText() + "] "); 
        for (Iterator iter = rhs.iterator(); iter.hasNext();) {
            Token word = (Token) iter.next();
            sb.append(word.getText() + " "); 
        }
        if (sb.length()>0)
            sb.delete(sb.length()-1, sb.length());
        sb.append("]"); 
        return sb.toString();
    }
}
