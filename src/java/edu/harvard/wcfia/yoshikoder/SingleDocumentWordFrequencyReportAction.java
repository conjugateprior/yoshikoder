package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.IOException;

import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationCache;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationException;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationService;
import edu.harvard.wcfia.yoshikoder.reporting.DocumentFrequencyReport;
import edu.harvard.wcfia.yoshikoder.reporting.WordFrequencyMap;
import edu.harvard.wcfia.yoshikoder.ui.dialog.YKReportDialog;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.DialogWorker;

public class SingleDocumentWordFrequencyReportAction extends YoshikoderAction {

    public SingleDocumentWordFrequencyReportAction(Yoshikoder yk) {
        super(yk, SingleDocumentWordFrequencyReportAction.class.getName());
    }
    
    public void actionPerformed(ActionEvent e) {
        final YKDocument doc = yoshikoder.getSelectedDocument();
        if (doc == null) return;
        
        dworker = new DialogWorker(yoshikoder){
            protected void doWork() throws Exception {
                TokenizationCache tcache = yoshikoder.getTokenizationCache();
                TokenList tl = tcache.getTokenList(doc);
                if (tl == null){
                    tl = TokenizationService.getTokenizationService().tokenize(doc);
                    tcache.putTokenList(doc, tl);
                }
                
                WordFrequencyMap map = new WordFrequencyMap(tl);
                DocumentFrequencyReport report = 
                    new DocumentFrequencyReport("Word Frequency Report", 
                            "Frequencies of each word in " + doc.getTitle(), 
                            yoshikoder.getDictionary().getName(),
                            doc, map);
                dia = new YKReportDialog(yoshikoder, report);
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
        dworker.start();
    }
}
