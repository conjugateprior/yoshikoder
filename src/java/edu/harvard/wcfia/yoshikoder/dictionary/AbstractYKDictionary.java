package edu.harvard.wcfia.yoshikoder.dictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceImpl;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceLine;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceLineImpl;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Token;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenImpl;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenListImpl;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public abstract class AbstractYKDictionary extends DefaultTreeModel
	implements YKDictionary{
	
	protected File location;
	protected PatternEngine patternEngine;
    
	protected Comparator comparator = new Comparator(){
	    public int compare(Object o1, Object o2) {
	        String o1name = ((Node)o1).getName();
	        String o2name = ((Node)o2).getName();
	        return o1name.compareTo(o2name);
	    }
	};

    public AbstractYKDictionary(String dictionaryName, String patternEngineType){
        super(new CategoryNodeImpl(dictionaryName));
        patternEngine = PatternEngineFactory.createEngine(patternEngineType);        
    }
	
	public CategoryNode getDictionaryRoot(){
	    return (CategoryNode)getRoot();
	}
	
	public void setDictionaryRoot(CategoryNode node){
	    setRoot(node);
	}
	
	public File getLocation() {
		return location;
	}
	
	public void setLocation(File f) {
		location = f;
	}

	/*
	public int getWindowSize() {
		return windowSize;
	}
	
	public void setWindowSize(int winsize) {
		windowSize = winsize;
	}
	*/

	public String getName(){
		return getDictionaryRoot().getName();
	}
	
	public void setName(String n){
	    getDictionaryRoot().setName(n);
	}
	
	public TreePath getPath(Node n){
	    TreeNode[] path = getPathToRoot(n);
	    TreePath p = new TreePath(path);
	    return p;
	}
	
	protected void add(Node child, Node parent) throws DuplicateException {
		for (Enumeration en = parent.children(); en.hasMoreElements();){
		    Node n = (Node)en.nextElement();
		    String name = n.getName();
		    if (name.equals(child.getName())){
		        throw new DuplicateException();
		    }
		}
		insertNodeInto(child, parent);
	}

	public void addCategory(String name, Double score, String desc, CategoryNode parent)
        throws DuplicateException {
	    CategoryNode n = new CategoryNodeImpl(name, score, desc);
        add(n, parent);
    }
    
    public void addPattern(String name, Double score, CategoryNode parent)
        throws PatternSyntaxException, DuplicateException {
        PatternNode n = 
            new PatternNodeImpl(name, score, patternEngine.makeRegexp(name));
        add(n, parent);
    }

    
    public void addCategory(CategoryNode cat, CategoryNode parent)
		throws DuplicateException {
	    add(cat, parent);
	}
		
	public void addPattern(PatternNode pattern, CategoryNode parent) 
		throws DuplicateException{
	    add(pattern, parent);
	}
	
	public void remove(Node node) {
	    if (node.getParent() != null)
	        removeNodeFromParent(node);
	}

	public void replace(Node cat, Node repl) throws DuplicateException{
	    Node parent = (Node)cat.getParent();
	    String ourname = cat.getName();
	    String repname = repl.getName();
	    
	    if (!ourname.equals(repname)){ // no clash if they are the same
	        if (parent != null){
	            for (Enumeration en = parent.children(); en.hasMoreElements();){
	                Node n = (Node)en.nextElement();
	                String name = n.getName();
	                if (name.equals(repname)){
	                    throw new DuplicateException();
	                }
	            }
	        }
	    }

	    List l = new ArrayList(); // can't reparent in enumeration w/out losing nodes
	    for (Enumeration en = cat.children(); en.hasMoreElements();){
	        Node child = (Node)en.nextElement();
	        l.add(child);
	    }
	    for (Iterator iter = l.iterator(); iter.hasNext();) {
            Node child = (Node)iter.next();
            insertNodeInto(child, repl);
        } 
	    remove(cat);
	    if (parent==null) // we're root then
	        setRoot(repl);
	    else {
	        insertNodeInto(repl, parent);
	    }
	}
	
    public TokenList getMatchingTokens(TokenList tl, Node node) {
        if (node instanceof CategoryNode)
            return getMatchingTokens(tl, (CategoryNode)node);
        else
            return getMatchingTokens(tl, (PatternNode)node);
    }
    
    protected TokenList getMatchingTokens(TokenList tl, CategoryNode node){
        
        TokenList list = new TokenListImpl();
        for (Enumeration enumeration = node.children(); enumeration.hasMoreElements();) {
            Object o = enumeration.nextElement();
            TokenList locs = new TokenListImpl();
            // yuck
            if (o instanceof CategoryNode) 
                locs = getMatchingTokens(tl, (CategoryNode)o);
            else
                locs = getMatchingTokens(tl, (PatternNode)o);
            list.addAll(locs);
        }
        return list;
    }

    protected TokenList getMatchingTokens(TokenList tl, PatternNode node){
        TokenList list = new TokenListImpl();
        Pattern p = node.getPattern();
        for (Iterator titer = tl.iterator(); titer.hasNext();) {
            Token token = (Token) titer.next();
            if (p.matcher(token.getText()).matches()){
                list.add(token);
            }
        }
        return list;
    }
   
	protected Concordance getConcordance(TokenList tokens, PatternNode pnode, int wsize){
        Pattern p = pnode.getPattern();
        
	    int tlength = tokens.size();
	    Concordance conc = new ConcordanceImpl( wsize );
	    int counter = -1;
	    for (Iterator iter = tokens.iterator(); iter.hasNext();) {
	        Token token = (Token) iter.next();
	        counter++;
	        if (p.matcher(token.getText()).matches()){
	            int lhsStart = Math.max(counter - wsize, 0);
	            int rhsStart = Math.min(counter+1, tlength);
	            int rhsEnd   = Math.min(counter+1 + wsize, tlength);
	            
                TokenList lhs = new TokenListImpl();
                for (int ii=lhsStart; ii<counter; ii++)
                    lhs.add( tokens.get(ii) );
             
                TokenList rhs = new TokenListImpl();
                for (int ii=rhsStart; ii<rhsEnd; ii++)
                    rhs.add( tokens.get(ii) );
            
                ConcordanceLine line = 
	                new ConcordanceLineImpl(lhs, token, rhs);
	            conc.addLine(line);
	        }
	    }
	    return conc;
	}
        
    public Concordance getConcordance(TokenList tl, Node node, int wsize){
        if (node instanceof CategoryNode){
            return getConcordance(tl, (CategoryNode)node, wsize);    
        } else {
            return getConcordance(tl, (PatternNode)node, wsize);
        }
    }
    
    protected Concordance getConcordance(TokenList tokens, CategoryNode cnode, int wsize){
        Concordance conc = new ConcordanceImpl( wsize );
        for (Enumeration enumeration = cnode.children(); enumeration.hasMoreElements();) {
            Object o = enumeration.nextElement();
            Concordance c = new ConcordanceImpl( wsize );
            // yuck
            if (o instanceof CategoryNode) 
                c = getConcordance(tokens, (CategoryNode)o, wsize);
            else
                c = getConcordance(tokens, (PatternNode)o, wsize);
            conc.addConcordance(c);
        }
        return conc;
    }
    	
	// alphabetizing implementation of insertNodeInto	
	protected int findIndexFor(Node child, Node parent){
		int cc = parent.getChildCount();
		if (cc==0){
			return 0;
		} 
		if (cc==1){
			return comparator.compare(child, parent.getChildAt(0)) 
				<= 0 ? 0 :1; 
		}
		return findIndexFor(child, parent, 0, cc-1); // first and last
	}

	protected int findIndexFor(Node child, Node parent, int i1, int i2){
		if (i1==i2){
			return comparator.compare(child, parent.getChildAt(i1))
				<= 0 ? i1 : i1+1;
		}
		int half = (i1 + i2) / 2;
		if (comparator.compare(child, parent.getChildAt(half)) <= 0){
			return findIndexFor(child, parent, i1, half);
		}
		return findIndexFor(child, parent, half+1, i2);
	}
	
	public void insertNodeInto(Node child, Node parent){
		int index = findIndexFor(child, parent);
		super.insertNodeInto(child, parent, index);
	}
	
	public void insertNodeInto(Node child, Node parent, int ind){
		insertNodeInto(child, parent);
	}
	
	private void recurse(StringBuffer sb, Node n){
		sb.append(getPath(n).toString() + "\n"); 
		for (Enumeration enumeration=n.children(); enumeration.hasMoreElements();){
			Node child = (Node)enumeration.nextElement();
			recurse(sb, child);
		}
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		CategoryNode n = getDictionaryRoot();
		recurse(sb, n);
		return sb.toString();
	}
	
    public PatternEngine getPatternEngine(){
        return patternEngine;
    }
    
    public void setPatternEngine(PatternEngine pe){
        patternEngine = pe;
    }
    
    public static void main(String[] args) {
        //A test to see how fast we can make the matching
        
        class Dict extends AbstractYKDictionary{
            public Dict(String name, String type){
                super(name, type);
            }
            public void setPatternEngine(PatternEngine eng){}
            public PatternEngine getPatternEngine(){return null;}
            public long[] test(){
                TokenList tl = new TokenListImpl();
                for (int ii = 0; ii < 1000; ii++) {
                    tl.add(new TokenImpl("china", 0, 5));
                    tl.add(new TokenImpl("sausage", 0, 5));
                }
                PatternNode p = new PatternNodeImpl("chin*", null, Pattern.compile("chin*"));
                System.out.println(p.getPattern());
                long start1 = new Date().getTime();
                List l = getMatchingTokens(tl, p);
                long end1 = new Date().getTime();
                //List ll = getMatchingTokens2(tl, p);
                long end2 = new Date().getTime();
                return new long[]{end1-start1, end2-end1};
            }
        };
        
        Dict d = new Dict("Name", PatternEngine.SUBSTRING);
        long[] tres = d.test();
        System.out.println(tres[0] + ", " + tres[1]);
        
    }
    
}
