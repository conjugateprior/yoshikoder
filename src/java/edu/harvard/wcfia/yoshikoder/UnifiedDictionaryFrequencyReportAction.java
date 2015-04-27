package edu.harvard.wcfia.yoshikoder;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationCache;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationException;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationService;
import edu.harvard.wcfia.yoshikoder.reporting.EntryFrequencyMap;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class UnifiedDictionaryFrequencyReportAction extends YoshikoderAction {

	private static Logger log = Logger.getLogger(UnifiedDictionaryFrequencyReportAction.class.getName()); 
	
	protected JFileChooser chooser = new JFileChooser();
	
	FileFilter csvutf8 = new FileFilter() {
		@Override
		public String getDescription() {
			return "CSV (UTF-8 encoded)";
		}	
		@Override
		public boolean accept(File f) {
			return f.isDirectory();
		}
	};
	
	FileFilter excel = new FileFilter() {
		@Override
		public String getDescription() {
			return "MS Excel";
		}	
		
		@Override
		public boolean accept(File f) {
			return f.isDirectory();
		}
	};
	
	
	
	public UnifiedDictionaryFrequencyReportAction(Yoshikoder yk) {
		super(yk, UnifiedDictionaryFrequencyReportAction.class.getName());
		
		chooser = new JFileChooser();
		chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
		chooser.addChoosableFileFilter(csvutf8);
		chooser.addChoosableFileFilter(excel); 
		chooser.setFileFilter(csvutf8);
	}

	protected int[] getDocumentStats(YKDocument doc, Node[] keys, CategoryNode catnode) throws IOException, TokenizationException {
        
		// tokenize the document
		TokenizationCache tcache = yoshikoder.getTokenizationCache();
        TokenList tl1 = tcache.getTokenList(doc);
        if (tl1 == null)
            tl1 = TokenizationService.getTokenizationService().tokenize(doc);
        
        // compute the dictionary counts
        EntryFrequencyMap efm1 = new EntryFrequencyMap(catnode, tl1);
		
        int[] counts = new int[keys.length+1];
		for (int ii=0; ii<keys.length; ii++) {
			Integer cnt = (Integer) efm1.getEntryCount(keys[ii]);
			counts[ii] = cnt.intValue();
		}
		// add N
		counts[keys.length] = efm1.getTokenTotal();
		
		return counts;
	}
	
	protected void writeExcel(List<YKDocument> documents, File file, CategoryNode node) throws Exception {
		final List<YKDocument> docs = documents;
		final File outputFile = file;
		final FileOutputStream stream = new FileOutputStream(outputFile);        
		final CategoryNode catnode = node;
		
		tworker = new TaskWorker(yoshikoder){
            protected void doWork() throws Exception {
            	// FIRST DOC
            	YKDocument doc1 = (YKDocument)docs.get(0);
            	// tokenize the document
        		TokenizationCache tcache = yoshikoder.getTokenizationCache();
        		TokenList tl1 = tcache.getTokenList(doc1);
                if (tl1 == null)
                	tl1 = TokenizationService.getTokenizationService().tokenize(doc1);
               	YKDictionary dict = yoshikoder.getDictionary();
                
               	// compute the dictionary counts
                EntryFrequencyMap efm1 = new EntryFrequencyMap(catnode, tl1);
                List lkeys = efm1.getSortedCategoryEntries();
                Node[] keys = (Node[])lkeys.toArray(new Node[lkeys.size()]);
                int[] counts = new int[keys.length+1];
        		for (int ii=0; ii<keys.length; ii++) {
        			Integer cnt = (Integer) efm1.getEntryCount(keys[ii]);
        			counts[ii] = cnt.intValue();
        		}
        		// add N
        		counts[keys.length] = efm1.getTokenTotal();
            	
        		HSSFWorkbook wb = new HSSFWorkbook();
                HSSFRow row;
                HSSFCell cell;
                
                HSSFSheet sheet = wb.createSheet("Category frequencies");
                
                // header
                row = sheet.createRow((short)0);
                for (int c=0; c<keys.length; c++){
                	cell = row.createCell((short)(c+1));
                    cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    String nodepath = efm1.getEntryPath(keys[c]);
                    cell.setCellValue(nodepath);
                }
                cell = row.createCell((short)(keys.length+1));
                cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                cell.setCellValue("Total");

                int rownum = 1;
                for (Iterator iter = docs.iterator(); iter.hasNext();) {
					YKDocument d = (YKDocument) iter.next();
					counts = getDocumentStats(d, keys, catnode);

					row = sheet.createRow((short)rownum);
					cell = row.createCell((short)0);
                    cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellValue(d.getTitle());
					
					for (int ii = 0; ii < keys.length; ii++) {
						cell = row.createCell((short)(ii+1));
						cell.setCellValue((double)counts[ii]);
                	}
            		cell = row.createCell((short)(keys.length+1));
            		cell.setCellValue(counts[keys.length]);
            		
            		rownum++;
				}
                wb.write(stream);
            }
            protected void onError() {
            	try {
            		stream.close();
            	} catch (Exception ex){
            		log.info("could not close the file stream");
            		ex.printStackTrace();
            	}
            	if (e instanceof TokenizationException){
                    DialogUtil.yelp(yoshikoder, "Tokenization Error", e);
                } else if (e instanceof IOException){
                    DialogUtil.yelp(yoshikoder, "Input/Ouput Error", e);
                } else {
                    DialogUtil.yelp(yoshikoder, "Error", e);
                }
            } 
            @Override
            protected void onSuccess() {
            	try {
            		stream.close();
            	} catch (Exception ex){
            		ex.printStackTrace();
            		log.info("could not close the file stream");
            	}
            	int resp = JOptionPane.showConfirmDialog(yoshikoder, "Open report file?", 
            			"Open report", JOptionPane.YES_NO_OPTION);
            	if (resp == JOptionPane.YES_OPTION){
            		try {
            			Desktop.getDesktop().open(outputFile);
            		} catch (Exception ex){
            			ex.printStackTrace();
            		}
            	}
            	
            }
        };
        tworker.start();	
	}

	protected void writeCsvUTF8(List<YKDocument> documents, File file, CategoryNode node) throws Exception {
		final List<YKDocument> docs = documents;
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
				Charset.forName("UTF8")));
		final CategoryNode catnode = node;
		
		tworker = new TaskWorker(yoshikoder){
            protected void doWork() throws Exception {
            	// FIRST DOC
            	YKDocument doc1 = (YKDocument)docs.get(0);
            	// tokenize the document
        		TokenizationCache tcache = yoshikoder.getTokenizationCache();
        		TokenList tl1 = tcache.getTokenList(doc1);
                if (tl1 == null)
                	tl1 = TokenizationService.getTokenizationService().tokenize(doc1);
               	YKDictionary dict = yoshikoder.getDictionary();
                
               	// compute the dictionary counts
                EntryFrequencyMap efm1 = new EntryFrequencyMap(catnode, tl1);
                List lkeys = efm1.getSortedCategoryEntries();
                Node[] keys = (Node[])lkeys.toArray(new Node[lkeys.size()]);
                int[] counts = new int[keys.length+1];
        		for (int ii=0; ii<keys.length; ii++) {
        			Integer cnt = (Integer) efm1.getEntryCount(keys[ii]);
        			counts[ii] = cnt.intValue();
        		}
        		// add N
        		counts[keys.length] = efm1.getTokenTotal();
            	
        		for (int ii = 0; ii < keys.length; ii++) {
					String nodepath = efm1.getEntryPath(keys[ii]);
					
					writer.write(",");
					writer.write(FileUtil.escapeForCsv(nodepath));
            	}
        		writer.write(",Total\n");
        
        		// and the rest
        		for (Iterator iter = docs.iterator(); iter.hasNext();) {
					YKDocument d = (YKDocument) iter.next();
					counts = getDocumentStats(d, keys, catnode);

					writer.write(FileUtil.escapeForCsv(d.getTitle()));
            		for (int ii = 0; ii < keys.length; ii++) {
						writer.write("," + counts[ii]);
                	}
            		writer.write("," + counts[keys.length] + "\n");
				}
        		writer.close();
            }
            protected void onError() {
            	try {
            		writer.close();
            	} catch (Exception ex){
            		log.info("could not close the CSV file");
            	}
                if (e instanceof TokenizationException){
                    DialogUtil.yelp(yoshikoder, "Tokenization Error", e);
                } else if (e instanceof IOException){
                    DialogUtil.yelp(yoshikoder, "Input/Ouput Error", e);
                } else {
                    DialogUtil.yelp(yoshikoder, "Error", e);
                }
            } 
            @Override
            protected void onSuccess() {
            	try {
            		writer.close();
            	} catch (Exception ex){
            		log.info("could not close the CSV file");
            	}
            		// dont ask because it won't work cross platform
            	/*
            	int resp = JOptionPane.showConfirmDialog(yoshikoder, "Open report file?", 
            			"Open report", JOptionPane.YES_NO_OPTION);
            	if (resp == JOptionPane.YES_OPTION){
            		try {
            			Desktop.getDesktop().open(outputFile);
            		} catch (Exception ex){
            			ex.printStackTrace();
            		}
            	}
            	*/
            }
        };
        tworker.start();		
	}

	
    public void actionPerformed(ActionEvent e) {
        if (yoshikoder.getProject().getDocumentList().size() > 1){

        	Node n = yoshikoder.getSelectedNode();
            CategoryNode cnode = null;
            if (n instanceof CategoryNode)
            	cnode = (CategoryNode)n;
            else // patternnode
            	cnode = (CategoryNode)n.getParent();
            final CategoryNode catnode = cnode;
        	
        	File file;
        	try {
        		int resp = chooser.showSaveDialog(yoshikoder);
        		if (resp != JFileChooser.APPROVE_OPTION)
        			return;
        		file = chooser.getSelectedFile();
        		if (chooser.getFileFilter().equals(excel)){
        			if (!file.getName().toLowerCase().endsWith(".xls"))
            			file = new File(file.getParent(), file.getName() + ".xls");
            		
        			YKDocument[] docsa = yoshikoder.getSelectedDocuments();      
        			List<YKDocument> docs = new ArrayList<YKDocument>(docsa.length);
        			for (int ii = 0; ii < docsa.length; ii++)
						docs.add(docsa[ii]);
        			
        			writeExcel(docs, file, catnode);
    		
        		} else if (chooser.getFileFilter().equals(csvutf8)){
        			if (!file.getName().toLowerCase().endsWith("-utf8.csv"))
            			file = new File(file.getParent(), file.getName() + "-utf8.csv");
        			
        			YKDocument[] docsa = yoshikoder.getSelectedDocuments();      
        			List<YKDocument> docs = new ArrayList<YKDocument>(docsa.length);
        			for (int ii = 0; ii < docsa.length; ii++)
						docs.add(docsa[ii]);
        			
        			writeCsvUTF8(docs, file, catnode);
        		} 
        	} catch (Exception ex){
        		DialogUtil.yelp(yoshikoder, ex.getMessage(), ex);
        		return;
        	}
        }
    }

}
