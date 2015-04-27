package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.tree.TreePath;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceLine;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.YKDocumentFactory;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Token;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenListImpl;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationException;
import edu.harvard.wcfia.yoshikoder.reporting.DictionaryFrequencyReport;
import edu.harvard.wcfia.yoshikoder.reporting.EntryFrequencyMap;
import edu.harvard.wcfia.yoshikoder.ui.dialog.YKDictionaryReportDialog;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class ConcordanceFrequencyReportAction extends YoshikoderAction {
    
	protected boolean onlyShowCats = true;
	
    public ConcordanceFrequencyReportAction(Yoshikoder yk) {
        super(yk, ConcordanceFrequencyReportAction.class.getName());
    }
    
    public void actionPerformed(ActionEvent e) {
        final Concordance conc = yoshikoder.getConcordance();
        if ((conc == null) || (conc.getLines().size()==0))
            return;

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
                TokenList tlist = new TokenListImpl();
                for (Iterator iter = conc.iterator(); iter.hasNext();) {
                    ConcordanceLine line = (ConcordanceLine) iter.next();
                    for (Iterator iterator = line.getLeftHandSide().iterator(); iterator
                    .hasNext();) {
                        Token token = (Token) iterator.next();
                        tlist.add(token);
                    }
                    for (Iterator iterator = line.getRightHandSide().iterator(); iterator
                    .hasNext();) {
                        Token token = (Token) iterator.next();
                        tlist.add(token);
                    }
                }
                EntryFrequencyMap efm = new EntryFrequencyMap(yoshikoder.getDictionary(), tlist);
                YKDocument fake = 
                    YKDocumentFactory.createDummyDocument("Concordance", "none", "UTF-8");
                DictionaryFrequencyReport reportcatsonly = 
                    new DictionaryFrequencyReport("Concordance Report", 
                            catnode.getName() + " applied to current concordance",
                            yoshikoder.getDictionary().getName(),
                            fake, efm, false);
                DictionaryFrequencyReport reportcatsandpats = 
                    new DictionaryFrequencyReport("Concordance Report", 
                    		catnode.getName() + "applied to current concordance",
                            yoshikoder.getDictionary().getName(),
                            fake, efm, true);
                dia = new YKDictionaryReportDialog(yoshikoder, reportcatsonly, reportcatsandpats, onlyShowCats);
            }
            protected void onSuccess() {
            	dia.setVisible(true);
            	onlyShowCats = !dia.getCurrentReport().getShowPatterns();
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
