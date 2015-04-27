package edu.harvard.wcfia.yoshikoder.dictionary;

import javax.swing.tree.DefaultMutableTreeNode;

public class NodeImpl extends DefaultMutableTreeNode implements Node {

	protected String name;
	protected Double score;
	protected Object temporary;

	public NodeImpl(){
		name = "Node"; //$NON-NLS-1$
	}

	public NodeImpl(String n){
		name = n;
	}

	public NodeImpl(String n, Double sc){
		name = n;
		score = sc;
	}
	
	public Object getTemporary(){
	    return temporary;
	}
	
	public void setTemporary(Object o){
	    temporary = o;
	}
	
	public String getName(){
		return name;
	}

	public void setName(String n){
		name = n; 
	}
	
	public Double getScore(){
		return score;
	}

	public void setScore(Double d){
		score = d;
	}

	public void setScore(double d){
		score = new Double(d);
	}
	
    public String getPopup() {
        if (score==null)
            return name;
        else
            return name + " [" + score + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public String toString(){
        return name;
    }
}
