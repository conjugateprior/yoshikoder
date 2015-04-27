package edu.harvard.wcfia.yoshikoder.dictionary;

public class CategoryNodeImpl extends NodeImpl implements CategoryNode{
	
	protected String description;

	public CategoryNodeImpl(){
		super();
	}

	public CategoryNodeImpl(String nname){
		super(nname);
	}

	public CategoryNodeImpl(String nname, Double sc){
		super(nname, sc);
	}

	public CategoryNodeImpl(String nname, Double sc, String desc){
		super(nname, sc);
		description = desc;
	}
	
	public String getDescription(){
		return description;
	}

	public void setDescription(String desc){
		description = desc;
	}
	
	public String getPopup(){
	    if (description == null || description.length()==0)
	        return super.getPopup();
	    else 
	        return super.getPopup() +
	    	   " (" + description + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
