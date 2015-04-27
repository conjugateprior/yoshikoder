package edu.harvard.wcfia.yoshikoder.util;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNodeImpl;
import edu.harvard.wcfia.yoshikoder.dictionary.DuplicateException;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternEngine;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternEngineFactory;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNodeImpl;
import edu.harvard.wcfia.yoshikoder.dictionary.SimpleDictionary;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;

public class YKDictionaryHandler extends DefaultHandler {
    
	private static Logger log = Logger.getLogger(YKDictionaryHandler.class.getName());
	
    private YKDictionary dict;
    
    private Stack stack;
    private boolean isRoot = true;
    
    public YKDictionaryHandler(){
        stack = new Stack();
    }
    
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException{
        
        if (qName.equals("dictionary")){ 
            
            dict = new SimpleDictionary("nameless");
            
            PatternEngine peng = null;            
            // covers a late implementation change; used to be a classforName            
            String patengine = attributes.getValue("patternengineclassname"); 
            if (patengine != null)
                peng = 
                    PatternEngineFactory.createEngine(PatternEngine.SUBSTRING);
            String enginetype = attributes.getValue("patternengine"); 
            if (enginetype != null)
                peng = PatternEngineFactory.createEngine(enginetype);
            
            dict.setPatternEngine(peng);
            
            /*
            String ws = attributes.getValue("windowsize");
            if (ws != null){
            	try {
            		int wsi = Integer.parseInt(ws, 2);
            		dict.setWindowSize(wsi);
            	} catch (NumberFormatException ex){
            		log.log(Level.WARNING, "Could not parse windowsize " + ws, ex);
            		dict.setWindowSize(2);
            	}
            } else {
            	dict.setWindowSize(2);
            }
            */
            
        } else if (qName.equals("cnode")){ 
            
            String name = attributes.getValue("name");             
            String score = attributes.getValue("score"); 
            Double d = null;
            if (score != null){
                try {
                    d = new Double(Double.parseDouble(score));
                } catch (NumberFormatException nfe){
                    //
                }
            }
            String desc = attributes.getValue("desc"); 
            
            CategoryNode cn = new CategoryNodeImpl(name, d, desc);
            if (isRoot){    
                stack.push(cn);
                dict.setDictionaryRoot(cn);
                isRoot = false; 
            } else {
                try {
                    dict.addCategory(cn, (CategoryNode)stack.peek());
                    stack.push(cn);
                } catch (DuplicateException de){
                    throw new SAXException(de);
                }
            }
            
        } else if (qName.equals("pnode")){ 
            
            String name = attributes.getValue("name"); 
            String score = attributes.getValue("score"); 
            Double d = null;
            if (score != null){
                try {
                    d = new Double(Double.parseDouble(score));
                } catch (NumberFormatException nfe){
                    //
                }
            }
            PatternNode pn = null;
            try {
                pn = new PatternNodeImpl(name, d, 
                        dict.getPatternEngine().makeRegexp(name));
            } catch (PatternSyntaxException pse){
                throw new SAXException(pse);
            }
            try {
                dict.addPattern(pn, (CategoryNode)stack.peek());
                stack.push(pn);
            } catch (DuplicateException de){
                throw new SAXException(de);
            }
            
        }
    }
    
    public void endElement(String uri, String localName, String qName) 
    throws SAXException{
        
        if (qName.equals("cnode")) { 
            stack.pop();
        } else if (qName.equals("pnode")){ 
            stack.pop();
        }
    }
    
    public YKDictionary getDictionary(){
        return dict;
    }
        
}
