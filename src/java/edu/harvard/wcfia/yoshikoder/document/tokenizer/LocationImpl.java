package edu.harvard.wcfia.yoshikoder.document.tokenizer;



// the simplest implementation of location
public class LocationImpl implements Location {
	
	private int start;
	private int end;

	public LocationImpl(int starti, int endi){
		start = starti;
		end = endi;
	}
	
	public int getStartPosition(){
		return start;
	}

	public void setStartPosition(int i){
		start = i;
	}
	
	public int getEndPosition(){
		return end;
	}
	
	public void setEndPosition(int i){
		end = i;
	}
	
    public String toString() {
        return "(" + start + ", " + end + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    public boolean equals(Object o){ // works for all Location implementations
        Location loc = null;
        try {
            loc = (Location)o;
            return (loc.getStartPosition() == start) && 
                   (loc.getEndPosition() == end);
        } catch (ClassCastException cce){
            return false;
        }
    }
	
}
