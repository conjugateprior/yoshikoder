package edu.harvard.wcfia.yoshikoder.reporting;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.document.DocumentList;

public class DictionaryRRDocumentComparisonReport extends AbstractReport implements YKReport {
    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.reporting.StatisticalDictionaryComparisonReport");

    
    protected ComparisonMap comparison;
    
    public DictionaryRRDocumentComparisonReport(String reportTitle, String desc, String dictName,
            DocumentList dl, EntryFrequencyMap map1, EntryFrequencyMap map2) {
        super(reportTitle, desc, dictName, dl);
        comparison = new ComparisonMap(map1, map2);
        data = initData();
    }
        
    protected ComparisonMap getComparisonMap(){
        return comparison;
    }
    
    protected Object[][] initData(){
        List l = comparison.getSortedCategoryEntries();
        log.info("report has " + l.size() + " lines");
        Object[][] report = new Object[l.size()][3]; 
        int ii=0;
        
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);
        
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            
            String entryPath =  comparison.getEntryPath(node);
            report[ii][0] = entryPath;
            
            RiskRatioStatistics rrs = comparison.getRiskRatioStatistics(node);
            try {
                double d = rrs.getRiskRatio();                
                String percentage;
                if (d > 1)
                    percentage = nf.format( (d-1)*100 );
                else
                    percentage = nf.format( -((1/d)-1)*100 ); 
                report[ii][1] = percentage;
            } catch (UncomputableRiskRatioException urre){
                report[ii][1] = "";
            }
            report[ii][2] = rrs.toString(); // NA if it can't be computed
            ii++;
        }
        return report;
    }
    
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return String.class;
        case 1:
            return String.class;
        case 2:
            return String.class;
        default:
            return Object.class;
        }
    }
            
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Entry";
        case 1:
            return "Percentage Change";
        case 2:
            return "Risk Ratio";
        default:
            return "No title";
        }
    }      
}
