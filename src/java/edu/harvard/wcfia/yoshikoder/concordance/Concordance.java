package edu.harvard.wcfia.yoshikoder.concordance;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


public interface Concordance extends Collection, Serializable{
	
	public boolean addConcordance(Concordance c);
	public boolean addLine(ConcordanceLine line);
	public List getLines();
	public int getWindowSize();
	
}
