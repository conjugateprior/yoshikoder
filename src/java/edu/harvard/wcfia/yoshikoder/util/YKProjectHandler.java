package edu.harvard.wcfia.yoshikoder.util;

import java.awt.Font;
import java.io.File;
import java.util.Locale;
import java.util.Stack;
import java.util.regex.PatternSyntaxException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.harvard.wcfia.yoshikoder.YKProject;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNodeImpl;
import edu.harvard.wcfia.yoshikoder.dictionary.DuplicateException;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternEngine;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternEngineFactory;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNodeImpl;
import edu.harvard.wcfia.yoshikoder.dictionary.SimpleDictionary;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.YKDocumentFactory;
 
/**
 * @author will
 */
public class YKProjectHandler extends DefaultHandler {

    private YKProject proj;
    private YKDictionary dict;
    
    private Stack stack;
    private boolean isRoot = true;
    
    public YKProjectHandler(){
        stack = new Stack();
    }
    
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException{

        if (qName.equals("ykproject")) { //$NON-NLS-1$
            
            String name = attributes.getValue("name"); 
            proj = new YKProject();
            proj.setName(name);
            String desc = attributes.getValue("desc"); 
            if (desc != null)
                proj.setDescription(desc);
            String loc = attributes.getValue("location"); 
            if (loc != null)
                proj.setLocation(new File(loc));
            String fnt = attributes.getValue("font"); 
            if (fnt != null){
            	proj.setDisplayFont(Font.decode(fnt));
            }
            	
        } else if (qName.equals("dictionary")){ 
            
            dict = new SimpleDictionary(proj.getName());
            System.err.println("setting up a fresh dictionary");

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

            
            proj.setDictionary(dict);
            /*
            String wsize = attributes.getValue("windowsize"); 
            try {
            	proj.setWindowSize( Integer.parseInt(wsize) );
            } catch (NumberFormatException nfe){
            	//
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
            
        	//System.err.println(">> found pnode with ");
            String name = attributes.getValue("name"); 
            //System.err.println("name: " + name);
            String score = attributes.getValue("score"); 
            //System.err.println("score:  + score");
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
            
        } else if (qName.equals("document")) { 
            
            String title = attributes.getValue("title");            
            String locale = attributes.getValue("locale"); 
            Locale lloc = null;
            if (locale != null){
                lloc = FileUtil.parseLocale(locale);
            } else {
            	lloc = Locale.getDefault();
            }
            
            String location = attributes.getValue("location"); 
            File floc = null;
            if (location != null)
                floc = new File(location);
                
            String charsetName = attributes.getValue("charsetname"); 
            if (charsetName == null)
            	charsetName = FileUtil.systemEncoding;
            
            YKDocument doc = null;
            doc = YKDocumentFactory.createYKDocument(floc, title, charsetName, lloc);
            doc.setLocale(lloc);
            
            String prefFont = attributes.getValue("font"); 
            if (prefFont != null)
            	doc.setPreferedFont(Font.decode(prefFont));
            
            proj.addDocument(doc);
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
        
    public YKProject getProject(){
        return proj;
    }
    
}
