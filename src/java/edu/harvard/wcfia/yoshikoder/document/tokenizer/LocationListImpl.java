package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.util.ArrayList;
import java.util.List;


// the simplest implementation of LocationList
public class LocationListImpl 
	extends ArrayList 
	implements LocationList{
	
	public LocationListImpl(){
		super();
	}
	
	public List getLocations(){
		return this;
	}
	
	public void addLocation(Location loc){
		add(loc);
	}
	
	public void addLocations(LocationList locations){
		addAll(locations);
	}
	
    public boolean equals(Object o){
        LocationList list = null;
        try {
            list = (LocationList)o;
            return super.equals(list);
        } catch (ClassCastException cce){
            return false;
        }
    }
    
    public String toString() {
        return super.toString();
    }
	
}
