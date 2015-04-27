package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.IOException;

import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationCache;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationException;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationService;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;

public class AddHighlightsAction extends YoshikoderAction {

    public AddHighlightsAction(Yoshikoder yk) {
        super(yk, AddHighlightsAction.class.getName());
    }
    
    public void actionPerformed(ActionEvent e) {       
        Node n = yoshikoder.getSelectedNode();
        YKDocument doc = yoshikoder.getSelectedDocument();
        if (n == null || doc == null)
            return;
        
        TokenList toclist = null;
        try {
            TokenizationCache tcache = yoshikoder.getTokenizationCache();
            toclist = tcache.getTokenList(doc);
            if (toclist == null){
                TokenList tl =
                    TokenizationService.getTokenizationService().tokenize(doc);
                tcache.putTokenList(doc, tl);
                toclist = tl;
            }
            toclist = yoshikoder.getDictionary().getMatchingTokens(toclist, n);            
            yoshikoder.addHighlights(toclist);
            
        }  catch (TokenizationException te){
            DialogUtil.yelp(yoshikoder, "Could not tokenize the document", te); // TODO loc
        } catch (IOException ioe){
            DialogUtil.yelp(yoshikoder, 
                    "Could not get text from the document.  Has it moved?", ioe); // TODO loc
        }
    }
}
