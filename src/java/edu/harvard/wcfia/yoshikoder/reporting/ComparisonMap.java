package edu.harvard.wcfia.yoshikoder.reporting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.harvard.wcfia.yoshikoder.dictionary.Node;

/**
 * The ComparisonMap matches category entries with risk ratio statistics.
 * @author will
 *
 */
public class ComparisonMap {

    protected Comparator comparator = new Comparator(){ // sort alphabetically on the path
        public int compare(Object o1, Object o2) {
            Node n1 = (Node)o1;
            Node n2 = (Node)o2;
            String path1 = getEntryPath(n1);
            String path2 = getEntryPath(n2);
            return path1.compareTo(path2);
        } 
    };
    
    protected Map comparisonMap;
    
    /**
     * Makes a comparison map from precomputed entry statistics.
     * @param m1 
     * @param m2
     */
    public ComparisonMap(EntryFrequencyMap m1, EntryFrequencyMap m2){
        comparisonMap = new HashMap();
    
        int total1 = m1.getTokenTotal();
        int total2 = m2.getTokenTotal();
        for (Iterator iter = m1.getSortedCategoryEntries().iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            Integer count1 = m1.getEntryCount(node);
            Integer count2 = m2.getEntryCount(node);
            RiskRatioStatistics rrs = 
                new RiskRatioStatistics(count1.intValue(), total1, count2.intValue(), total2);
            comparisonMap.put(node, rrs);
        }
    }
    
    public RiskRatioStatistics getRiskRatioStatistics(Node n){
        return (RiskRatioStatistics)comparisonMap.get(n);
    }
    
    public List getSortedCategoryEntries(){
        List nl = new ArrayList(comparisonMap.keySet());
        Collections.sort(nl, comparator);
        return nl;
    }
    
    /**
     * Returns a string representation of the full path of a dictionary entry.
     * @param n
     * @return full path
     */
    public String getEntryPath(Node n){
        Node parent = n;
        StringBuffer sb = new StringBuffer();
        sb.append(n.getName());
        while ((parent = (Node)parent.getParent()) != null){
            sb.insert(0, parent.getName() + ">");
        }
        return sb.toString();
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = getSortedCategoryEntries().iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            String path = getEntryPath(node);
            RiskRatioStatistics rrs = getRiskRatioStatistics(node);
            sb.append(path);
            sb.append(" ");
            sb.append(rrs.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
    
}
