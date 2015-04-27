package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.DocumentListImpl;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationCache;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationException;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationService;
import edu.harvard.wcfia.yoshikoder.reporting.EntryFrequencyMap;
import edu.harvard.wcfia.yoshikoder.reporting.DictionaryRRDocumentComparisonReport;
import edu.harvard.wcfia.yoshikoder.ui.ComparisonPanel;
import edu.harvard.wcfia.yoshikoder.ui.dialog.YKReportDialog;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.DialogWorker;

public class DictionaryRRDocumentComparisonAction extends YoshikoderAction {
    public DictionaryRRDocumentComparisonAction(Yoshikoder yk) {
        super(yk, DictionaryRRDocumentComparisonAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        if (yoshikoder.getProject().getDocumentList().size() > 1){
            ComparisonPanel panel = new ComparisonPanel(yoshikoder);
            
            int ret = JOptionPane.showConfirmDialog(yoshikoder, panel, "Comparison", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ret != JOptionPane.OK_OPTION) return;
            
            final YKDocument doc1 = panel.getFirstDocument();
            final YKDocument doc2 = panel.getSecondDocument();
            
            if ((doc1 == null) || (doc2 == null)){
                return;            
            }
            
            Node n = yoshikoder.getSelectedNode();
            CategoryNode cnode = null;
            if (n instanceof CategoryNode)
            	cnode = (CategoryNode)n;
            else // patternnode
            	cnode = (CategoryNode)n.getParent();
            final CategoryNode catnode = cnode;
            
            dworker = new DialogWorker(yoshikoder){
                protected void doWork() throws Exception {
                    DocumentList dl = new DocumentListImpl();
                    dl.add(doc1);
                    dl.add(doc2);

                    TokenizationCache tcache = yoshikoder.getTokenizationCache();
                    TokenList tl1 = tcache.getTokenList(doc1);
                    TokenList tl2 = tcache.getTokenList(doc2);
                    if (tl1 == null){
                        tl1 = TokenizationService.getTokenizationService().tokenize(doc1);
                        tcache.putTokenList(doc1, tl1);
                    }
                    if (tl2 == null){
                        tl2 = TokenizationService.getTokenizationService().tokenize(doc2);
                        tcache.putTokenList(doc2, tl2);
                    }
                    
                    //YKDictionary dict = yoshikoder.getDictionary();
                    EntryFrequencyMap efm1 = new EntryFrequencyMap(catnode, tl1);
                    EntryFrequencyMap efm2 = new EntryFrequencyMap(catnode, tl2);
                    
                    String key = doc1.getTitle() + 
                        " vs. " + doc2.getTitle();
                    DictionaryRRDocumentComparisonReport report = 
                        new DictionaryRRDocumentComparisonReport(key,
                                key,
                                catnode.getName(), dl, efm1, efm2);
                    
                    System.err.println(report==null);
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
    
}
