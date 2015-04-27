package edu.harvard.wcfia.yoshikoder;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.DocumentListImpl;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationCache;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationException;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationService;
import edu.harvard.wcfia.yoshikoder.reporting.EntryFrequencyMap;
import edu.harvard.wcfia.yoshikoder.reporting.UnifiedDocumentFrequencyReport;
import edu.harvard.wcfia.yoshikoder.reporting.WordFrequencyMap;
import edu.harvard.wcfia.yoshikoder.reporting.YKReport;
import edu.harvard.wcfia.yoshikoder.ui.dialog.YKReportDialog;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.DialogWorker;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class UnifiedWordFrequencyReportAction extends YoshikoderAction {

    private static final Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.UnifiedFrequencyReportAction");
    	
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
	
	public UnifiedWordFrequencyReportAction(Yoshikoder yk) {
		super(yk, UnifiedWordFrequencyReportAction.class.getName());
		
		chooser = new JFileChooser();
		chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
		chooser.addChoosableFileFilter(csvutf8);
		// chooser.addChoosableFileFilter(excel); // NOTE: Excel is never used!
		chooser.setFileFilter(csvutf8);
	}

	// first pass to get vocab
	protected List<String> getVocab(List<YKDocument> docs) throws IOException, TokenizationException {
		Set<String> vocab = new HashSet<String>();
		TokenizationCache tcache = yoshikoder.getTokenizationCache();
		for (YKDocument doc : docs) {
			TokenList tl = tcache.getTokenList(doc);
			if (tl == null){
				tl = TokenizationService.getTokenizationService().tokenize(doc);
				tcache.putTokenList(doc, tl);
			}
			WordFrequencyMap map = new WordFrequencyMap(tl);
			vocab.addAll(map.getVocabularyList());
		}
		List<String> list = new ArrayList<String>();
		list.addAll(vocab);
		return list;
	}
	
	// second pass to push out counts
	protected void pushOutCountsCSVUtf8(List<YKDocument> docs, Writer writer) throws IOException, TokenizationException {
		List<String> vocab = getVocab(docs);
		Collections.sort(vocab); // alphabetical
		
		// write header
		for (String word : vocab)
			writer.write("," + FileUtil.escapeForCsv(word));
		writer.write(",Total\n");
		
		TokenizationCache tcache = yoshikoder.getTokenizationCache();
		for (YKDocument doc : docs) {
			TokenList tl = tcache.getTokenList(doc);
			if (tl == null){
				tl = TokenizationService.getTokenizationService().tokenize(doc);
				tcache.putTokenList(doc, tl);
			}
			WordFrequencyMap map = new WordFrequencyMap(tl);
			writer.write( FileUtil.escapeForCsv(doc.getTitle()) );
			for (String vocabWord: vocab) {
				Integer count = map.getWordCount(vocabWord);
				if (count == null)
					writer.write(",0");
				else
					writer.write("," + count.toString());
			}
			writer.write("," + map.getTotal() + "\n");
		}
		// something else should close the file
	}
	
	protected void writeCsvUTF8(List<YKDocument> documents, File file) throws Exception {
		final List<YKDocument> docs = documents;
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
				Charset.forName("UTF8")));
		
		tworker = new TaskWorker(yoshikoder){
            protected void doWork() throws Exception {
            	pushOutCountsCSVUtf8(docs, writer);
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

	// second pass to push out counts
	protected void pushOutCountsExcel(List<YKDocument> docs, FileOutputStream stream) throws IOException, TokenizationException {
		List<String> vocab = getVocab(docs);
		Collections.sort(vocab); // alphabetical

		HSSFWorkbook wb = new HSSFWorkbook();
        HSSFRow row;
        HSSFCell cell;
        HSSFSheet sheet = wb.createSheet("Word frequencies");
        
        // header
        row = sheet.createRow((short)0);
        for (int ii = 0; ii < vocab.size(); ii++) {
        	cell = row.createCell((short)(ii+1));
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellValue(FileUtil.escapeForCsv(vocab.get(ii)));
		}
        cell = row.createCell((short)(vocab.size()+1));
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue("Total");
		
        int rowNumber = 1;
		TokenizationCache tcache = yoshikoder.getTokenizationCache();
		for (YKDocument doc : docs) {
			TokenList tl = tcache.getTokenList(doc);
			if (tl == null){
				tl = TokenizationService.getTokenizationService().tokenize(doc);
				tcache.putTokenList(doc, tl);
			}
			WordFrequencyMap map = new WordFrequencyMap(tl);
			
			row = sheet.createRow((short)rowNumber);
			cell = row.createCell((short)0);
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellValue(doc.getTitle());
			
            for (int ii = 0; ii < vocab.size(); ii++) {
	        	cell = row.createCell((short)(ii+1));
	            Integer count = map.getWordCount(vocab.get(ii));
				if (count == null)
					cell.setCellValue((double)0);
				else
					cell.setCellValue((double)count.doubleValue());
			}	
			cell = row.createCell((short)(vocab.size()+1));
			cell.setCellValue((double)map.getTotal());
			
			rowNumber++;
		}
		wb.write(stream);
		// something else should close the file
	}
	
	protected void writeExcel(List<YKDocument> documents, File file) throws Exception {
		final List<YKDocument> docs = documents;
		final File outputFile = file;
		final FileOutputStream stream = new FileOutputStream(outputFile);

		tworker = new TaskWorker(yoshikoder){
            protected void doWork() throws Exception {
            	pushOutCountsExcel(docs, stream);
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
            	int resp = JOptionPane.showConfirmDialog(yoshikoder, "Open report file?", "Open report", JOptionPane.YES_NO_OPTION);
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
	
    public void actionPerformed(ActionEvent e) {
        if (yoshikoder.getProject().getDocumentList().size() > 1){
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
					
        			writeExcel(docs, file);
    		
        		} else if (chooser.getFileFilter().equals(csvutf8)){
        			if (!file.getName().toLowerCase().endsWith("-utf8.csv"))
            			file = new File(file.getParent(), file.getName() + "-utf8.csv");
        			
        			YKDocument[] docsa = yoshikoder.getSelectedDocuments();      
        			List<YKDocument> docs = new ArrayList<YKDocument>(docsa.length);
        			for (int ii = 0; ii < docsa.length; ii++)
						docs.add(docsa[ii]);

        			writeCsvUTF8(docs, file);       		
        		}        	
        	} catch (Exception ex){
        		DialogUtil.yelp(yoshikoder, ex.getMessage(), ex);
        		return;
        	}
        }
    }
    
}
