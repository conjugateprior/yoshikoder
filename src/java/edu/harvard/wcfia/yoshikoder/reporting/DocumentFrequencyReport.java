package edu.harvard.wcfia.yoshikoder.reporting;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.document.DocumentListImpl;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class DocumentFrequencyReport extends AbstractReport implements YKReport{

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.reporting.DocumentFrequencyReport");
    
    protected WordFrequencyMap map;
    
    public DocumentFrequencyReport(String reportTitle, String desc, String dictName, 
            YKDocument doc, WordFrequencyMap wfmap) {
        super(reportTitle, desc, dictName, new DocumentListImpl(doc));
        map = wfmap;
        data = initData();
    }
    
    protected Object[][] initData(){
        List vocabulary = map.getSortedVocabularyList();        
        Collections.sort(vocabulary);
        
        Object[][] report = 
            new Object[vocabulary.size()][3];

        int ii = 0;
        for (Iterator iter = vocabulary.iterator(); iter.hasNext();) {
            String word = (String) iter.next();
            report[ii][0] = word;
            report[ii][1] = map.getWordCount(word);
            report[ii][2] = map.getWordProportion(word);
            ii++;
        }
        return report;
    }
    
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex){
            case 0:
                return String.class;
            case 1:
                return Integer.class;
            case 2:
                return Double.class;
            default:
                return Object.class;
        }
    }

    public String getColumnName(int columnIndex) {
        switch (columnIndex){
        case 0:
            return Messages.getString("word");
        case 1:
            return Messages.getString("count");
        case 2:
            return Messages.getString("proportion");
        default:
            return null;
        }
    }
}
