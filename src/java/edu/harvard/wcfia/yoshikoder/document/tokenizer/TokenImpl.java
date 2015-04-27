package edu.harvard.wcfia.yoshikoder.document.tokenizer;


public class TokenImpl implements Token {

    protected String text;
    protected int start;
    protected int end;
    
    public TokenImpl(String txt, int st, int en){
        text = txt;
        start = st;
        end = en;
    }
    
    public String getText(){
        return text;
    }
    
    public int getStart(){
        return start;
    }
    
    public int getEnd(){
        return end;
    }
        
    public boolean equals(Object o){
        TokenImpl tok = null;
        try {
            tok = (TokenImpl)o;
            return text.equals(o) && 
                   start==tok.getStart() &&
                   end==tok.getEnd();
        } catch (ClassCastException cce){
            return false;
        }
    }

    public int hashCode(){
        return text.hashCode() + start;
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(text);
        sb.append(" (");
        sb.append(start);
        sb.append(",");
        sb.append(end);
        sb.append(")");
        return sb.toString();
    }
}
