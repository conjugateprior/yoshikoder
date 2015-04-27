package edu.harvard.wcfia.yoshikoder.reporting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.dictionary.SimpleDictionary;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Token;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;

public class EntryFrequencyMap {
    
    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.reporting.EntryFrequencyMap");
    
    protected Map nodeToCount; 
    protected int tokenTotal;
    
    protected CategoryNode currentTopNode;
    
    protected Comparator comparator = new Comparator(){ // sort alphabetically on the path
        public int compare(Object o1, Object o2) {
            Node n1 = (Node)o1;
            Node n2 = (Node)o2;
            String path1 = getEntryPath(n1);
            String path2 = getEntryPath(n2);
            return path1.compareTo(path2);
        } 
    };

    public EntryFrequencyMap(CategoryNode node, TokenList tl){
        nodeToCount = new HashMap();
        tokenTotal = tl.size();
        
        currentTopNode = (CategoryNode)node.getParent();
        collectPatterns(node, tl);
    }

    
    public EntryFrequencyMap(YKDictionary dict, TokenList tl){
        nodeToCount = new HashMap();
        tokenTotal = tl.size();
        
        CategoryNode root = dict.getDictionaryRoot();
        collectPatterns(root, tl);
    }
    
    public int getTokenTotal(){
        return tokenTotal;
    }
    
    protected void collectPatterns(Node node, TokenList tl){
    	//log.info("in collectPatterns: node = " + node.getName());
    	WordFrequencyMap wmap = tl.getWordFrequencyMap();
    	List<String> words = wmap.getVocabularyList();
        
    	if (node instanceof PatternNode){
            Pattern pattern = ((PatternNode)node).getPattern();
            Matcher matcher = pattern.matcher("");
            int count = 0;
            for (Iterator iter = words.iterator(); iter.hasNext();) {
                String w = (String) iter.next();
                matcher.reset(w);
                if (matcher.matches())
                    count += wmap.getWordCount(w);
            }
            
            nodeToCount.put(node, new Integer(count));
            //log.info("putting " + node.getName() + " -> " + count);
            // walk upwards, adding count to every parent
            Node parent = node;
            while ((parent = (Node)parent.getParent()) != currentTopNode){
            	Integer c = (Integer)nodeToCount.get(parent);
                //log.info("parent " + parent.getName() + " has count " + c);

                Integer newCount = new Integer(c.intValue() + count);
                nodeToCount.put(parent, newCount);
                //log.info("now putting " + node.getName() + " -> " + count);
            }
        } else {
            nodeToCount.put(node, new Integer(0));
            //log.info("in category node fork: putting " + node.getName() + " -> " + "0");
        }
        // recurse
        Enumeration en = node.children();
        while (en.hasMoreElements()){
            Node n = (Node)en.nextElement();
            collectPatterns(n, tl);
        }
    }
    
    public List getSortedEntries(){
        List nl = new ArrayList(nodeToCount.keySet());
        Collections.sort(nl, comparator);
        return nl;
    }
    
    public List getSortedCategoryEntries(){
        List nl = getSortedEntries();
        List cats = new ArrayList();
        for (Iterator iter = nl.iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            if (node instanceof CategoryNode)
                cats.add(node);
        }
        return cats;
    }
    
    public Integer getEntryCount(Node dictionaryNode){
        return (Integer)nodeToCount.get(dictionaryNode);
    }

    public Double getEntryProportion(Node dictionaryNode){
        Integer count = getEntryCount(dictionaryNode);
        if (count != null)
            if (tokenTotal > 0)
                return new Double(count.doubleValue() / tokenTotal);
            else
                return new Double(0);
        else
            return null;
    }
    
    public String getEntryPath(Node n){
        Node parent = n;
        StringBuffer sb = new StringBuffer();
        sb.append(n.getName());
        while ((parent = (Node)parent.getParent()) != null){
            sb.insert(0, parent.getName() + ">");
        }
        return sb.toString();
    }
    
    /**
     * Returns null if no score is set and score*count otherwise.
     * 
     * @param dictionaryNode
     * @return summed score
     */
    public Double getSummedScore(Node dictionaryNode){
        Double score = dictionaryNode.getScore();
        if (score != null){
            Integer ii = (Integer)nodeToCount.get(dictionaryNode);
            if (ii == null)
                return new Double(0);
            else
                return new Double(ii.intValue() * score.doubleValue());
        }
        return null;
    }
    
    public String toString(){
        return nodeToCount.toString();
    }
}
