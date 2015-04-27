package edu.harvard.wcfia.yoshikoder;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.xml.sax.SAXException;

import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.DocumentListImpl;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.YKDocumentFactory;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.ImportUtil;

public class YKCommandLine {

	protected YKDictionary dictionary;
	protected File[] files;
	
	public YKCommandLine(String[] args){
		try {
			parseCommandline(args);
			Object[][] results = run();
			System.out.println(csv(results));
		} catch (Exception e){
			System.err.println(e.getMessage());
		}
	}
	
	protected String csv(Object [][] results){
		StringBuffer sb = new StringBuffer();
		for (int ii=0; ii<results.length; ii++){
			for (int jj=0; jj<results[ii].length; jj++){
				Object o = results[ii][jj];
				if (o instanceof String)
					sb.append("\"" + o + "\",");	
				else 
					sb.append(o + ",");
			}
			sb.setCharAt(sb.length()-1, '\n');
		}
		return sb.toString();
	}
	
	protected String html(Object [][] results){
		StringBuffer sb = new StringBuffer();
		sb.append("<html><body><table border=\"1\">\n");
		for (int ii=0; ii<results.length; ii++){
			sb.append("<tr>");
			if (ii==0){
				for (int jj=0; jj<results[ii].length; jj++){
					sb.append("<th>" + results[ii][jj] + "</th>");
				}
			} else {
				for (int jj=0; jj<results[ii].length; jj++){
					sb.append("<td>" + results[ii][jj] + "</td>");
				} 
			}
			sb.append("</tr>\n");
		}
		sb.append("</table></body></html>");
		return sb.toString();
		
	}
	
	protected Object[][] run(){
		// do the analysis
		DocumentList list = new DocumentListImpl();
		for (int ii=0; ii<files.length; ii++){
			YKDocument doc = YKDocumentFactory.createYKDocument(files[ii]); 
			list.add(doc);
		}
        return null;
    }
        // XXX use new reporting framework
		/*
        Object[][] table = null;
		try {
			Map results = new HashMap(); // doc -> report
			DictionaryReport rep = dictionary.makeDictionaryReport(list);
			List docs = rep.getDocuments();
			for (Iterator iter = docs.iterator(); iter.hasNext();) {
				YKDocument ykdoc = (YKDocument) iter.next();
				EntryStatisticsMap m = rep.getSubReport(ykdoc).getEntryStatisticsMap();
				results.put(ykdoc, m);
			}
			
			// get paths from first entrymap
			Iterator it = results.keySet().iterator();
			YKDocument ykd = (YKDocument)it.next();
			EntryStatisticsMap esm = (EntryStatisticsMap)results.get(ykd);
			List paths = esm.getCategoryEntries();
			Collections.sort(paths);
			
			table = new Object[paths.size()+1][list.size()+2];
			
			// table header
			table[0][0] = dictionary.getName() + " category";
			table[0][1] = "Score";
			int counter = 2;
			for (Iterator diter = docs.iterator(); diter.hasNext();) {
				YKDocument doc = (YKDocument) diter.next();
				table[0][counter] = doc.getTitle();
				counter++;
			} 
			
			counter = 1;
			for (Iterator iter = paths.iterator(); iter.hasNext();) {
				String path = (String) iter.next();
				//sb.append(path);
				table[counter][0] = path;
				
				Double scored = esm.getScore(path);
				String sc = (scored==null) ? "None" : scored.toString();
				table[counter][1] = sc;
				
				// loop over documents
				int count = 2;
				for (Iterator iterator = docs.iterator(); iterator.hasNext();) {
					YKDocument d = (YKDocument) iterator.next();
					EntryStatisticsMap map = (EntryStatisticsMap)results.get(d);
					Integer i = map.getCount(path);
					table[counter][count] = i;
					count++;
				}
				counter++;
			}
		} catch (Exception ioe){
			ioe.printStackTrace();
			System.err.println("Could not make a reports: " + ioe.getMessage());
		}
		return table;
	}
	*/
        
	protected void parseCommandline(String[] args) throws Exception{
		File f = new File(args[0]);
		if (!f.exists()){
			throw new Exception(f.getName() + " does not exist");
		}
		try {
			YKProject proj = ImportUtil.importYKProject(f);
			dictionary = proj.getDictionary();
		} catch (SAXException sax){
			throw new Exception("Could not parse the dictionary in " + f.getName());
		} catch (Exception ex){
			throw new Exception("Could not read " + f.getName());
		}

		f = new File(args[1]);
		if (!f.exists()){
			throw new Exception("Directory " + f.getName() + " does not exist");
		}
		if (!f.isDirectory()){
			throw new Exception(f.getName() + " must be a directory");
		}
		
		FileFilter filter = 
			new FileFilter(){
				public boolean accept(File arg0) { 
					return arg0.getName().endsWith(".txt");
				}
			};
		files = f.listFiles(filter);
		
		System.err.println("Analysing files:");
		for (int ii = 0; ii < files.length; ii++) {
			File file = files[ii];
			System.err.println("\t" + file.getName());
		}
		System.err.println("using dictionary " + dictionary.getName());
	}
	
	protected String parseFile(File f) throws IOException {
		String s = FileUtil.slurp(f, "UTF-8");
		return s;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new YKCommandLine(args);
		
		// assume that the first argument is a dictionary file
		// and the second is a directory
		// output goes to stdout
	}

}
