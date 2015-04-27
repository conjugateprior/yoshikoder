package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


public interface LocationList extends Collection, Serializable{
	
	public List getLocations();
	public void addLocation(Location loc);
	public void addLocations(LocationList locations);
	
}
