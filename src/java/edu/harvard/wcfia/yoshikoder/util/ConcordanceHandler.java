package edu.harvard.wcfia.yoshikoder.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceImpl;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceLine;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceLineImpl;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Token;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenImpl;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenListImpl;

/**
 * @author will
 */
public class ConcordanceHandler extends DefaultHandler {
    
    private Concordance conc;
    private int windowSize;
    private List clist;
    
    private List lhs;
    private List rhs;
    private boolean inRhs;
    private String target;
    
    public ConcordanceHandler(){
        clist = new ArrayList();
    }
    
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

    	if (qName.equals("concordance")){ //$NON-NLS-1$
    		String style = attributes.getValue("style"); //$NON-NLS-1$
    		if (!style.equals("050805")) //$NON-NLS-1$
    			throw new SAXException(Messages.getString("wrongFormat")); //$NON-NLS-1$
    		String ws = attributes.getValue("windowsize"); //$NON-NLS-1$
    		if (ws == null)
    			windowSize = -1; // guess it...
    		else {
    			try {
    				windowSize = Integer.parseInt(ws);
    			} catch (NumberFormatException nfe){
    				windowSize = -1;
    				throw new SAXException(nfe);
    			}
    		}
    			
    	} else if (qName.equals("line")) { //$NON-NLS-1$
            inRhs = false;
            lhs = new ArrayList();
            rhs = new ArrayList();
        } else if (qName.equals("w")) { //$NON-NLS-1$
            String tar = attributes.getValue("target"); //$NON-NLS-1$
            String txt = attributes.getValue("txt"); //$NON-NLS-1$
            if (tar != null){
                target = txt;
                inRhs = true;
            } else {
                if (inRhs)
                    rhs.add(txt);
                else
                    lhs.add(txt);
            }
        }
    }

    public void endElement(String uri, String localName, String qName){                                                  
        if (qName.equals("line")) { //$NON-NLS-1$
            TokenList tl = new TokenListImpl();
            for (Iterator iter = lhs.iterator(); iter.hasNext();) {
                String lhs = (String) iter.next();
                Token t = new TokenImpl(lhs, 0, 0);
                tl.add(t);
            }
            TokenList rl = new TokenListImpl();
            for (Iterator iter = rhs.iterator(); iter.hasNext();) {
                String rhs = (String) iter.next();
                Token t = new TokenImpl(rhs, 0, 0);
                rl.add(t);
            }
            Token targ = new TokenImpl(target, 0, 0);
            ConcordanceLine line = new ConcordanceLineImpl(tl, targ, rl);
            clist.add(line);
        }
    }
    
    public Concordance getConcordance(){
    	if (windowSize == -1){
    		// not specified - we have to guess it.
    		int max = 0;
    		for (Iterator iter = clist.iterator(); iter.hasNext();) {
				ConcordanceLine line = (ConcordanceLine) iter.next();
				int m = Math.max(line.getLeftHandSide().size(),
								 line.getRightHandSide().size());
				if (m > max)
					max = m;
			}
    		windowSize = max;
    	}
    	
        conc = new ConcordanceImpl(windowSize);
    	for (Iterator iter = clist.iterator(); iter.hasNext();) {
			ConcordanceLine line = (ConcordanceLine) iter.next();
			conc.addLine(line);
		}
    	return conc;
    }
    
    public static void main(String[] args) {
        try {
            System.out.println( 
                    ImportUtil.importConcordance(new File("/home/will/fooconc.ykc")) ); //$NON-NLS-1$
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
