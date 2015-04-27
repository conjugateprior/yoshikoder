package edu.harvard.wcfia.yoshikoder.reporting;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.document.DocumentList;

public class DictionaryComparisonReport extends AbstractReport implements
        YKReport {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.reporting.DictionaryComparisonReport");

    protected EntryFrequencyMap firstMap;
    protected EntryFrequencyMap secondMap;
    protected boolean showPatterns;
    
    public DictionaryComparisonReport(String reportTitle, String desc, String dictName,
            DocumentList dl, EntryFrequencyMap map1, EntryFrequencyMap map2, boolean showPats) {
        super(reportTitle, desc, dictName, dl);
        firstMap = map1;
        secondMap = map2;
        showPatterns = showPats;
        data = initData();
    }
        
    public EntryFrequencyMap getFirstEntryFrequencyMap(){
        return firstMap;
    }
    
    public EntryFrequencyMap getSecondEntryFrequencyMap(){
        return secondMap;
    }
    
    protected Object[][] initData(){
        List l = null;
        if (showPatterns){
            l = firstMap.getSortedEntries();
        } else { 
            l = firstMap.getSortedCategoryEntries();
        }
                
        log.info("report has " + l.size() + " lines");
        Object[][] report = new Object[l.size()][7]; 
        int ii=0;
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            //if (node.getParent()==null) // a root node - the dictionary name
            //    continue;
            
            String entryPath =  firstMap.getEntryPath(node);
            report[ii][0] = entryPath;//.substring(entryPath.indexOf('>')+2, entryPath.length());
            report[ii][1] = firstMap.getEntryCount(node);
            report[ii][2] = firstMap.getSummedScore(node);
            report[ii][3] = firstMap.getEntryProportion(node);     
            report[ii][4] = secondMap.getEntryCount(node);
            report[ii][5] = secondMap.getSummedScore(node);
            report[ii][6] = secondMap.getEntryProportion(node);
            ii++;
        }
        return report;
    }
    
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return String.class;
        case 1:
            return Integer.class;
        case 2: 
            return Double.class;
        case 3:
            return Double.class;
        case 4:
            return Integer.class;
        case 5: 
            return Double.class;
        case 6:
            return Double.class;
        default:
            return Object.class;
        }
    }
            
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Entry";
        case 1:
            return "Count (1)";
        case 2: 
            return "Score (1)";
        case 3:
            return "Prop. (1)";
        case 4:
            return "Count (2)";
        case 5: 
            return "Score (2)";
        case 6:
            return "Prop. (2)";
        default:
            return "No title";
        }
    }      
}
