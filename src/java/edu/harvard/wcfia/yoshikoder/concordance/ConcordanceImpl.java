package edu.harvard.wcfia.yoshikoder.concordance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ConcordanceImpl extends ArrayList implements Concordance{
	
	protected int windowSize;
	
	public ConcordanceImpl(List lines, int winSize){
		super(lines);
		windowSize = winSize;
	}
	
	public ConcordanceImpl(int winSize){
		super();
		windowSize = winSize;
	}
	
	public List getLines(){
		return this;
	}
	
	public boolean addLine(ConcordanceLine line){
		return add(line);
	}
	
	public boolean addConcordance(Concordance c){
		return addAll(c);
	}
	
	public int getWindowSize(){
		return windowSize;
	}
	
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = iterator(); iter.hasNext();) {
            ConcordanceLine line = (ConcordanceLine) iter.next();
            sb.append(line.toString() + "\n"); //$NON-NLS-1$
        }
        if (sb.length()>1)
            sb.delete(sb.length()-1, sb.length());
        return sb.toString();	
    }
	
}
