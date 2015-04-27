package edu.harvard.wcfia.yoshikoder.reporting;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.document.DocumentListImpl;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;

public class DictionaryFrequencyReport extends AbstractReport implements YKReport {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.reporting.DictionaryFrequencyReport");
    
    protected EntryFrequencyMap map;
    protected boolean showPatterns = false;
    
    public DictionaryFrequencyReport(String reportTitle, String desc, String dictName,
            YKDocument doc, EntryFrequencyMap efmmap) {
        this(reportTitle, desc, dictName, doc, efmmap, false);
    }
    
    public DictionaryFrequencyReport(String reportTitle, String desc, String dictName,
            YKDocument doc, EntryFrequencyMap efmmap, boolean showPats) {
        super(reportTitle, desc, dictName, new DocumentListImpl(doc));
        map = efmmap;
        showPatterns = showPats;
        data = initData();
    }
    
    public boolean getShowPatterns(){
    	return showPatterns;
    }
    
    protected Object[][] initData(){
        List l = null;
        if (showPatterns)
            l = map.getSortedEntries();
        else {
            l = map.getSortedCategoryEntries();
            log.info("length of sorted categories: " + l.size());
        }
        
        Object[][] report = new Object[l.size()][3];
        int ii=0;
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            //if (node.getParent()==null) // a root node - the dictionary name
            //   continue;
            
            String entryPath =  map.getEntryPath(node);
            report[ii][0] = entryPath;//.substring(entryPath.indexOf('>')+2, entryPath.length());
            report[ii][1] = map.getEntryCount(node);
            report[ii][2] = map.getEntryProportion(node);
            //report[ii][3] = map.getEntryProportion(node);                
            ii++;
        }
        return report;
    }

    public EntryFrequencyMap getEntryFrequencyMap(){
        return map;
    }
    
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
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
            
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Entry";
        case 1:
            return "Count";
        case 2: 
            return "Proportion";
        default:
            return "No title";
        }
    }        
}
