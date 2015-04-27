package edu.harvard.wcfia.yoshikoder.dictionary;

import javax.swing.tree.MutableTreeNode;

public interface Node extends MutableTreeNode{

	public String getName();
	public void setName(String name);
	
	public Double getScore();
	public void setScore(Double d);
	
	public String getPopup(); // a short description

	public Object getTemporary();
	public void setTemporary(Object o);
}
