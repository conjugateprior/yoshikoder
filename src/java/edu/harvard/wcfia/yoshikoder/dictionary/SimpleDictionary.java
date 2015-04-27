package edu.harvard.wcfia.yoshikoder.dictionary;


/**
 * Simple dictionary implementation that makes substring matches in patterns.
 * 
 * @author will
 */
public class SimpleDictionary extends AbstractYKDictionary implements YKDictionary {
    
    public SimpleDictionary(String name){
        super(name, PatternEngine.SUBSTRING);
    }
    
    public SimpleDictionary() {
        super("Dictionary", PatternEngine.SUBSTRING);
    }

    public String toString(){
        return super.toString();
    }
}
