package edu.harvard.wcfia.yoshikoder.util;

import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNodeImpl;
import edu.harvard.wcfia.yoshikoder.dictionary.DuplicateException;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternEngine;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNodeImpl;
import edu.harvard.wcfia.yoshikoder.dictionary.SimpleDictionary;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;

/**
 * @author will
 */
public class YKOldDictionaryHandler extends DefaultHandler {

    private YKDictionary dict;
    private PatternEngine engine;
    
    private Stack stack;
    private StringBuffer sb;
    private boolean inPattern = false;
        
    /**
     * 
     */
    public YKOldDictionaryHandler() {
        super();
        stack = new Stack();
        dict = new SimpleDictionary();
        engine = dict.getPatternEngine();
        sb = new StringBuffer();
    }

    public YKDictionary getDictionary(){
        return dict;
    }
    
    public void characters(char[] ch, int start, int length){
        sb.append(ch, start, length);
    }
    
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException{

        if (qName.equals("dictionary")) { //$NON-NLS-1$
            String style = attributes.getValue("style"); //$NON-NLS-1$
            if (!style.equals("041204")) //$NON-NLS-1$
                throw new SAXException(Messages.getString("wrongFormat")); //$NON-NLS-1$
        
        } else if (qName.equals("category")){ //$NON-NLS-1$
            
            CategoryNode newcat = new CategoryNodeImpl("Nameless"); //$NON-NLS-1$
            if (stack.size()==0){ // this is root
                dict.setDictionaryRoot(newcat);
            } else {
                CategoryNode cparent = (CategoryNode)stack.peek();
                try {
                    dict.addCategory(newcat, cparent);
                } catch (DuplicateException de){
                    throw new SAXException(de);
                }
            }
            stack.push(newcat);
            inPattern = false;
             
        } else if (qName.equals("pattern")){ //$NON-NLS-1$
            PatternNode newpat = new PatternNodeImpl("Nameless"); //$NON-NLS-1$
            CategoryNode cparent = (CategoryNode)stack.peek();
            try {
                dict.addPattern(newpat, cparent);
            } catch (DuplicateException de){
                throw new SAXException(de);
            }    
            stack.push(newpat);
            inPattern = true;
        
        } else if (qName.equals("comment")){ //$NON-NLS-1$
            sb = new StringBuffer();
        
        } else if (qName.equals("name")){ //$NON-NLS-1$
            sb = new StringBuffer();
        
        } else if (qName.equals("score")){ //$NON-NLS-1$
            sb = new StringBuffer();
        }
        
    }
    
    public void endElement(String uri, String localName, String qName) 
    	throws SAXException{
    
        if (qName.equals("name")){ //$NON-NLS-1$
            String name = sb.toString();
            Node node = (Node)stack.peek();
            node.setName(name);
            
            if (inPattern){
                try {
                    PatternNode pn = (PatternNode)node;
                    Pattern p = engine.makeRegexp(name);
                    pn.setPattern(p);
                } catch (PatternSyntaxException pse){
                    throw new SAXException(pse);
                }
            }
        
        } else if (qName.equals("score")){ //$NON-NLS-1$
            String score = sb.toString();
            try {
                double d = Double.parseDouble(score);
                Node node = (Node)stack.peek();
                node.setScore(new Double(d));
            } catch (NumberFormatException nfe){
                throw new SAXException(nfe);
            }
            
        } else if (qName.equals("comment")){ //$NON-NLS-1$
            CategoryNode node = (CategoryNode)stack.peek();
            node.setDescription(sb.toString());
            
        } else if (qName.equals("category")){ //$NON-NLS-1$
	        stack.pop();
	    
        } else if (qName.equals("pattern")){ //$NON-NLS-1$
	        stack.pop();
	    
        }
    }
    
    
    
}
