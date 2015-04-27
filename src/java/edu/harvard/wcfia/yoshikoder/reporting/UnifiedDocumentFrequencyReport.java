package edu.harvard.wcfia.yoshikoder.reporting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.DocumentListImpl;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.YKDocumentFactory;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.BITokenizerImpl;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Tokenizer;

public class UnifiedDocumentFrequencyReport extends AbstractReport {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.reporting.UnifiedDocumentFrequencyReport");
        
    protected WordFrequencyMap[] maps;
    
    public UnifiedDocumentFrequencyReport(String reportTitle, String desc, 
            DocumentList dl, WordFrequencyMap[] m) {
        super(reportTitle, desc, "No dictionary", dl);
        log.info("got " + dl.size() + " documents and " + m.length + "word frequency maps");
        
        documentList = dl;
        maps = m;
        data = initData();
    }
    
    protected Object[][] initData(){
        Set vocab = new HashSet();
        for (int ii = 0; ii < documentList.size(); ii++) {
            WordFrequencyMap map = maps[ii];
            List v = map.getVocabularyList();
            for (Iterator iter = v.iterator(); iter.hasNext();) {
                String word = (String) iter.next();
                vocab.add(word);
            }
        }
               
        List vocabulary = new ArrayList(vocab);
        Collections.sort(vocabulary);
        
        Object[][] report = 
            new Object[vocabulary.size()][documentList.size()*2+1];

        int row = 0;
        for (Iterator iter = vocabulary.iterator(); iter.hasNext();) {
            String word = (String) iter.next();
            report[row][0] = word;
            for (int ii = 0; ii < documentList.size(); ii++) {
                Integer count = maps[ii].getWordCount(word);
                if (count != null){
                    report[row][ii + 1] = new Double(count.doubleValue());
                    report[row][ii + documentList.size() + 1] = (maps[ii].getWordProportion(word).doubleValue());
                } else {
                    report[row][ii + 1] = new Double(0);
                    report[row][ii + documentList.size() + 1] = report[row][ii + 1];
                }   
            }
            row++;
        }
        return report;
    }
    
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0)
            return String.class;
        else 
            return Double.class;
    }

    public String getColumnName(int columnIndex) {
        if (columnIndex == 0)
            return "Word";
        else if (columnIndex > documentList.size())
            return "Prop. (" + (columnIndex - documentList.size()) + ")";
        else
            return "Count (" + (columnIndex)  + ")";
    }
    
    public static void main(String[] args) throws Exception{
        YKDocument d1 = YKDocumentFactory.createDummyDocument("D1", "Mary had a little lamb.  Mary had some more", "UTF-8");
        YKDocument d2 = YKDocumentFactory.createDummyDocument("D1", "Jackie had a little beef.  Jackie whined some more", "UTF-8");
        DocumentList dl = new DocumentListImpl();
        dl.add(d1);
        dl.add(d2);
        Tokenizer tok = new BITokenizerImpl();
        WordFrequencyMap wd1 = new WordFrequencyMap(tok.getTokens(d1.getText()));
        WordFrequencyMap wd2 = new WordFrequencyMap(tok.getTokens(d2.getText()));
        UnifiedDocumentFrequencyReport rep = new UnifiedDocumentFrequencyReport("title", "desc", 
               dl, new WordFrequencyMap[]{wd1, wd2});
        JTable table = new JTable(rep);
        JOptionPane.showMessageDialog(null, new JScrollPane(table));
    }
    
}
