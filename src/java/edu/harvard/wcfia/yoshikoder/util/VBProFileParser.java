package edu.harvard.wcfia.yoshikoder.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNodeImpl;
import edu.harvard.wcfia.yoshikoder.dictionary.DuplicateException;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternEngine;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNodeImpl;
import edu.harvard.wcfia.yoshikoder.dictionary.SimpleDictionary;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;

public class VBProFileParser {

    public class BadPattern {
        boolean isDuplicate;
        int line;
        String pattern;
        BadPattern(int errorLine, String errorPattern, boolean duplicate){
            line=errorLine;
            pattern = errorPattern;
            isDuplicate = duplicate;
        }
        public String toString(){
            return pattern + " (line " + line + ") " + 
                (isDuplicate ? "is a duplicate" : "could not be compiled");
        }
    }
    
    protected Pattern arrows;
    protected List errors;
        
    public VBProFileParser(){
        arrows = Pattern.compile("^\\>+(.+?)\\<+$");
    }
    
    public YKDictionary parse(File f, String enc) throws IOException {
        String s = FileUtil.slurp(f, enc);
        YKDictionary dict = parse(s);
        return dict;
    }
    
    public YKDictionary parse(String s) {
        errors = new ArrayList();
        YKDictionary dict = new SimpleDictionary();
        
        String newname = "Imported VBPro Dictionary";
        dict.getDictionaryRoot().setName(newname);
        PatternEngine rengine = dict.getPatternEngine();
        dict.setName(newname);
        
        BufferedReader in = new BufferedReader(new StringReader(s));
        String line;
        int ii=0;
        CategoryNode cat = null; // the current category
        int lineNumber = 0;
        try {
            while ((line = in.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim().toLowerCase();
                if (trimmed.startsWith(">") && trimmed.endsWith("<")) { 
                    // a category
                    String categoryName = stripArrows(trimmed);
                    
                    if (categoryName.length() == 0) {
                        categoryName = "Entry_" + ii;
                        ii++;
                    }
                    cat = new CategoryNodeImpl(categoryName);
                    try {
                        dict.addCategory(cat, dict.getDictionaryRoot());
                    } catch (DuplicateException de){
                        errors.add(new BadPattern(lineNumber, trimmed, true));
                    }
                } else if (trimmed.length() > 0) {
                    // no need to fix these for a SubString match-using dictionary
                    //String fixed = fixVBProWildcards(trimmed);
                    try {
                        Pattern regexp = rengine.makeRegexp(trimmed);
                        PatternNode pattern = 
                            new PatternNodeImpl(trimmed, null, regexp);
                        dict.addPattern(pattern, cat);
                    } catch (DuplicateException de){
                        errors.add(new BadPattern(lineNumber, trimmed, true));
                    } catch (Exception re) {
                        errors.add(new BadPattern(lineNumber, trimmed, false));
                    }
                } else {
                    // a blank line...
                }
            }
            in.close(); // also strictly redundant
        } catch (IOException ioe){
            // redundant io catch block (we're a string!)
        }
        return dict;
    }
    
    public List getErrors(){
        return errors; 
    }
        
    protected String stripArrows(String name){
        Matcher m = arrows.matcher(name);
        if (m.matches())
            return m.group(1);
        else
            return name;
    }
    
    public static void main(String[] args) {
        VBProFileParser parser = new VBProFileParser();
        File f = new File("/Users/will/Desktop/testfile.txt");
        
        try {
            YKDictionary dict = parser.parse(f, "GBK");
            JOptionPane.showMessageDialog(null, new JScrollPane(new JTree(dict)));
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        for (Iterator iter = parser.getErrors().iterator(); iter.hasNext();) {
            VBProFileParser.BadPattern bp = (VBProFileParser.BadPattern) iter.next();
            System.out.println(bp);
        }
        System.exit(0);
    }
}
