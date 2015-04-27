package edu.harvard.wcfia.yoshikoder.dictionary;

import java.io.File;
import java.io.Serializable;
import java.util.regex.PatternSyntaxException;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;

public interface YKDictionary extends TreeModel, Serializable{

	public String getName();
    
	public void setName(String name);
	
	public File getLocation();
	
    public void setLocation(File f);
	
	//public int getWindowSize();
	
    //public void setWindowSize(int winsize);

	public PatternEngine getPatternEngine();
	
    public void setPatternEngine(PatternEngine engine);
	
	public void addCategory(CategoryNode cat, CategoryNode parent)
		throws DuplicateException;
    
    public void addCategory(String name, Double score, String desc, CategoryNode parent)
        throws DuplicateException;
	
    public void addPattern(String name, Double score, CategoryNode parent)
        throws PatternSyntaxException, DuplicateException;
    
	public void addPattern(PatternNode pattern, CategoryNode parent) 
		throws DuplicateException;
	
	public void remove(Node node);

	public void replace(Node cat, Node repl) throws DuplicateException;

    // filters the token list for the ones that match the pattern
    public TokenList getMatchingTokens(TokenList tl, Node n);
    
    public Concordance getConcordance(TokenList tl, Node node, int wsize);
	
	public TreePath getPath(Node n);
	
	public CategoryNode getDictionaryRoot();
    
	public void setDictionaryRoot(CategoryNode node);

}
