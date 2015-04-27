package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.document.YKDocument;

public class TokenCache implements TokenizationCache {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenCache");
    
    protected Map map;
    
    public TokenCache() {
        map = new HashMap();
    }

    public TokenList getTokenList(YKDocument doc) {
        SoftReference sr = (SoftReference)map.get(doc);
        if (sr == null)
            return null; // never cached
        
        TokenList tl = (TokenList)sr.get();
        if (tl != null)
            log.info("Found a cached token list for " + doc.getTitle());
        else
            log.info("Previous token list has been garbage collected");
        
        return tl; 
    }

    public void putTokenList(YKDocument doc, TokenList tl) {
        SoftReference sr = new SoftReference(tl);
        map.put(doc, sr);
    }

    public void removeTokenList(YKDocument doc) {
        map.remove(doc);
    }

    public void clear() {
        map.clear();
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
            YKDocument doc = (YKDocument) iter.next();
            sb.append(doc.getTitle() + ": ");
            SoftReference sr = (SoftReference)map.get(doc);
            TokenList tl = (TokenList)sr.get();
            if (tl == null)
                sb.append("[null],");
            else
                sb.append("[" + tl.size() + " tokens], ");
        }
        sb.delete(sb.length()-2, sb.length());
        return sb.toString();
    }
}
