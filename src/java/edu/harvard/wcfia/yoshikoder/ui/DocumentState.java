package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Location;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.LocationImpl;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Token;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;


public class DocumentState {
    
    private YKDocument doc;	
    private int caretPosition;
    private Map locationToColor;
    private Set colors;
    
    private String tooltip;
    
    public DocumentState(YKDocument d){
        doc = d;
        locationToColor = new HashMap();
        colors = new HashSet();
        
        StringBuffer sb = new StringBuffer();
        sb.append("<html><table><tr><td colspan=\"2\">");
        sb.append(doc.getTitle().trim());
        sb.append("</td></tr><tr><td>Locale:</td><td>");
        sb.append(doc.getLocale());
        sb.append("</td></tr><tr><td>Encoding:</td><td>");
        sb.append(doc.getCharsetName());
        sb.append("</td></tr>");
        Font f = doc.getPreferredFont();
        if (f != null){
            sb.append("<tr><td>Prefered font:</td><td>");
            sb.append(f.getFamily());
            sb.append("</td></tr>");
        }
        sb.append("</table></html>");
        tooltip = sb.toString();
    }
    
    public String getTooltip(){
        return tooltip;
    }
    
    public YKDocument getDocument(){
        return doc;
    }
    
    public Set getHighlightLocations(Color col){
        Set s = new HashSet();
        for (Iterator iter = locationToColor.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            if (entry.getValue().equals(col))
                s.add(entry.getKey());
        }
        return s;
    }
    
    public Set getHighlightColors(){
        return colors;
    }
    
    public void removeHighlights(){
        locationToColor.clear();
        colors.clear();
    }
    
    public void addHighlights(TokenList tl, Color col){
        if (!colors.contains(col) && tl.size()>0)
            colors.add(col);
        
        for (Iterator iter = tl.iterator(); iter.hasNext();) {
            Token element = (Token) iter.next();
            Location loc = 
                new LocationImpl(element.getStart(), element.getEnd());
            locationToColor.put(loc, col);
        }
    }
    
    public int getCaretPosition(){
        return caretPosition;
    }
    
    public void setCaretPosition(int pos){
        caretPosition = pos;
    }
    
    /**
     * Equals depends only on the document, not the highlighting state
     */
    public boolean equals(Object other){
        try {
            DocumentState ds = (DocumentState)other;
            return ds.getDocument().equals(doc);    
        } catch (Exception ex){
            return false;
        }   
    }
    
    public String toString(){
        return doc.getTitle();
    }
}
