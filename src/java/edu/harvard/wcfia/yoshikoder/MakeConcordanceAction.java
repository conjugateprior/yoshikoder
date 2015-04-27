package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceImpl;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceLineImpl;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenImpl;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenListImpl;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationCache;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationException;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationService;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class MakeConcordanceAction extends YoshikoderAction {
    
    public MakeConcordanceAction(Yoshikoder yk) {
        super(yk, MakeConcordanceAction.class.getName());        
    }
    
    protected Concordance makeMultipleDocumentConcordance(YKDocument[] docs, Node n, int wsize) throws TokenizationException, IOException {
    	Concordance con = new ConcordanceImpl(wsize);
    	TokenizationCache tcache = yoshikoder.getTokenizationCache();
    	TokenizationService service = TokenizationService.getTokenizationService();
    	for (YKDocument doc : docs) {
    		TokenList tl = tcache.getTokenList(doc);
    		if (tl == null){
    			tl = service.tokenize(doc);
    			tcache.putTokenList(doc, tl);
    		} 
    		Concordance c = yoshikoder.getDictionary().getConcordance(tl, n, wsize);

    		if (docs.length > 1){
    			c.addLine(new ConcordanceLineImpl(new TokenListImpl(wsize), new TokenImpl(" ", 0, 0), new TokenListImpl(wsize)));
    		} 
    		con.addConcordance(c);
    	}
    	return con;
    }
    
    public void actionPerformed(ActionEvent e) {
        final Node n = yoshikoder.getSelectedNode();
        if (n == null)
            return;
        final int wsize = yoshikoder.getWindowSize();
        final YKDocument[] docs = yoshikoder.getSelectedDocuments();
        if (docs == null || docs.length == 0)
        	return;
        
        tworker = new TaskWorker(yoshikoder){
            Concordance conc = null;
            protected void doWork() throws Exception {
            	conc = makeMultipleDocumentConcordance(docs, n, wsize);
            	/*
            	TokenizationCache tcache = yoshikoder.getTokenizationCache();
                TokenList tl = tcache.getTokenList(doc);
                if (tl == null){
                    tl = TokenizationService.getTokenizationService().tokenize(doc);
                    tcache.putTokenList(doc, tl);
                } 
                int wsize = yoshikoder.getWindowSize();
                conc = yoshikoder.getDictionary().getConcordance(tl, n, wsize);
            	*/
            }
            protected void onError(){
                DialogUtil.yelp(yoshikoder, "Could not create concordance", e);
            }
            protected void onSuccess(){
                yoshikoder.setConcordance(conc); 
            }
        };
        tworker.start();
    }
}

