package edu.harvard.wcfia.yoshikoder.cl;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.harvard.wcfia.yoshikoder.YKProject;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.BITokenizerImpl;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Token;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Tokenizer;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.ImportUtil;

public class Annotator {

    protected Map tokenToCategorySet;
    protected YKDictionary dictionary;
    protected TokenList tokens;
    
    public Annotator(YKDictionary dict, File document) throws Exception {
        dictionary = dict;
        tokenToCategorySet = new HashMap();

        Tokenizer tl = new BITokenizerImpl();
        String txt = FileUtil.slurp(document);        
        tokens = tl.getTokens(txt);
                
        Enumeration en = dict.getDictionaryRoot().children();
        while (en.hasMoreElements()){
            Node n = (Node)en.nextElement();
            fillFrom(n);
        }
        // System.err.println(tokenToCategorySet);
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = tokens.iterator(); iter.hasNext();) {
            Token token = (Token) iter.next();
            sb.append(token.getText());
            Set s = (Set)tokenToCategorySet.get(token);
            if (s != null){
                sb.append(" [");
                for (Iterator iterator = s.iterator(); iterator.hasNext();) {
                    CategoryNode cat = (CategoryNode) iterator.next();
                    sb.append(cat.getName() + ",");
                }
                sb.delete(sb.length()-1, sb.length());
                sb.append("]");
            } 
            sb.append("\n");
        }
        return sb.toString();
    }
    
    protected void fillFrom(Node n){
        if (n instanceof CategoryNode){
            System.err.println("Processing category " + n.getName());
            TokenList tl = dictionary.getMatchingTokens(tokens, n);

            for (Iterator iter = tl.iterator(); iter.hasNext();) {
                Token token = (Token) iter.next();
                Set s = (Set)tokenToCategorySet.get(token);
                if (s==null){
                    s = new HashSet();
                    tokenToCategorySet.put(token, s);
                }
                s.add(n);
            }
            
            Enumeration en = n.children();
            while (en.hasMoreElements()){
                fillFrom((Node)en.nextElement());
            }
        } 
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        args = new String[]{"/Users/will/work/cca/LIWC/liwc.ykd",
                "/Users/will/work/wscores/WordscoresAPSR2_manifestos/UK/FULUKLAB97a.txt"};
        
        // arg 1 is the dictionary or project
        try {
            YKDictionary d = null;
            File f = new File(args[0]);
            int version = ImportUtil.getVersion(f);
            if (version == ImportUtil.YKPROJECT_050805_FILE){
                YKProject proj = ImportUtil.importYKProject(f);
                d = proj.getDictionary();
            } else if (version == ImportUtil.YKDICTIONARY_050805_FILE){
                d = ImportUtil.importYKDictionary(f);
            } else {
                throw new Exception("Cannot parse dictionary from '" + 
                        f.getCanonicalPath() + "'");
            }
            
            // arg 2 is the file
            File doc = new File(args[1]);
            
            Annotator annot = new Annotator(d, doc);
            System.out.println(annot);
        
        } catch (Exception ex){
            ex.printStackTrace();
            System.exit(1);
        }
    
    }

}
