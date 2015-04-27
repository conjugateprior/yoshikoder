package edu.harvard.wcfia.yoshikoder.reporting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.YKDocumentFactory;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Token;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationService;

public class WordFrequencyMap {

    protected HashMap map;
    protected int total;
        
    /*
    public WordFrequencyMap(){
        map = new HashMap();
        total = 0;
    }
    */
    
    public WordFrequencyMap(TokenList tl){
        map = new HashMap();
        total = 0;
        for (Iterator iter = tl.iterator(); iter.hasNext();) {
            Token token = (Token) iter.next();
            String text = token.getText().toLowerCase();
            incrementWordCount(text, 1); // creates if necessary
        }
    }
    
    /**
     * Increments the count associated with a word.  Creates an entry 
     * in the map if necessary
     * @param word word to add a count to
     * @param inc how much to add
     */
    public void incrementWordCount(String word, int inc){
        Integer count = (Integer)map.get(word);
        if (count == null)
            count = new Integer(inc);
        else
            count = new Integer(count.intValue() + inc);
        map.put(word, count);
        total += inc;        
    }
    
    public Integer getWordCount(Token token){
        return (Integer)map.get(token.getText().toLowerCase());
    }
    
    public Integer getWordCount(String word){
        return (Integer)map.get(word.toLowerCase());
    }
    
    public Double getWordProportion(String word){
        Integer i = (Integer)map.get(word.toLowerCase());
        if (i == null || total == 0)
            return null;
        else
            return new Double(i.doubleValue() / total);
    }
    
    /**
     * Provides an interator over the (String) words types in this map
     * @return iterator
     */
    public Iterator getVocabularyIterator(){
        return map.keySet().iterator();
    }
    
    public List getVocabularyList(){
        List arr = new ArrayList(map.keySet());
        return arr;
    }
    
    public List getSortedVocabularyList(){
        List arr = getVocabularyList();
        Collections.sort(arr);
        return arr;
    }
    
    public int getTotal(){
        return total;
    }
    
    public int size(){
        return map.size();
    }
    
    public String toString(){
        return "TokenFrequencyMap: total=" + total + "\n" + map.toString();
    }
    
    public static void main(String[] args) throws Exception {
        YKDocument doc = YKDocumentFactory.createDummyDocument("Foo", "Mary and her lamb.  Mary's Lamb.", "UTF-8");
        TokenList tl = TokenizationService.getTokenizationService().tokenize(doc);
        WordFrequencyMap map = new WordFrequencyMap(tl);
        System.out.println(map);
        DocumentFrequencyReport rep = 
            new DocumentFrequencyReport("title", "desc", "dictionary", doc, map);
        JOptionPane.showMessageDialog((JFrame)null, new JScrollPane(new JTable(rep)));
        System.exit(0);
    }
}
