package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationCache;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationException;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationService;
import edu.harvard.wcfia.yoshikoder.reporting.DictionaryFrequencyReport;
import edu.harvard.wcfia.yoshikoder.reporting.EntryFrequencyMap;
import edu.harvard.wcfia.yoshikoder.ui.dialog.YKDictionaryReportDialog;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class SingleDocumentDictionaryReportAction extends YoshikoderAction {
	
	private static Logger log = Logger.getLogger(DictionaryFrequencyReport.class.getName());
	
	protected boolean onlyShowCats = true;

    public SingleDocumentDictionaryReportAction(Yoshikoder yk) {
        super(yk, SingleDocumentDictionaryReportAction.class.getName());
    }
    
    public void actionPerformed(ActionEvent e) {
        final YKDocument doc = yoshikoder.getSelectedDocument();
        if (doc == null) return;
        
        Node n = yoshikoder.getSelectedNode();
        CategoryNode cnode = null;
        if (n instanceof CategoryNode)
        	cnode = (CategoryNode)n;
        else // patternnode
        	cnode = (CategoryNode)n.getParent();
        final CategoryNode catnode = cnode;
        
        TaskWorker tworker = new TaskWorker(yoshikoder){
            YKDictionaryReportDialog dia;
        	protected void doWork() throws Exception {
                TokenizationCache tcache = yoshikoder.getTokenizationCache();
                TokenList tl = tcache.getTokenList(doc);
                if (tl == null){
                    tl = TokenizationService.getTokenizationService().tokenize(doc);
                    tcache.putTokenList(doc, tl);
                }
                
                EntryFrequencyMap efm = new EntryFrequencyMap(catnode, tl);
                
                DictionaryFrequencyReport catsAndPats = 
                    new DictionaryFrequencyReport("Dictionary Entry Frequencies", 
                            "Frequencies of each dictionary entry in " + doc.getTitle(), 
                            yoshikoder.getDictionary().getName(),
                            doc, efm, true);
                DictionaryFrequencyReport catsOnly = 
                    new DictionaryFrequencyReport("Dictionary Entry Frequencies", 
                            "Frequencies of each dictionary entry in " + doc.getTitle(), 
                            yoshikoder.getDictionary().getName(),
                            doc, efm, false);
                
                dia = new YKDictionaryReportDialog(yoshikoder, catsOnly, catsAndPats, onlyShowCats);
            }
            protected void onSuccess() {
            	dia.setVisible(true);
            	// why is this called without the dialog returning
            	onlyShowCats = !dia.getCurrentReport().getShowPatterns();
            	log.info("now closed dialog said that the cats only flag was set when it last looked? " + onlyShowCats);
            }
            protected void onError() {
                if (e instanceof TokenizationException){
                    DialogUtil.yelp(yoshikoder, "Tokenization Error", e);
                } else if (e instanceof IOException){
                    DialogUtil.yelp(yoshikoder, "Input/Ouput Error", e);
                } else {
                    DialogUtil.yelp(yoshikoder, "Error", e);
                }
            }
        };
        tworker.start();
    }
    
}
