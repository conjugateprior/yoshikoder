package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.ui.PreviewPanel;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.ImportUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;
import edu.harvard.wcfia.yoshikoder.util.VBProFileParser;

public class OpenDictionaryAction extends YoshikoderAction {
    
    protected JFileChooser chooser;
    
    FileFilter ykd = new FileFilter() {
		@Override
		public String getDescription() {
			return "Yoshikoder Dictionary";
		}	
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().endsWith(".ykd");
		}
	};
	
	FileFilter vbpro = new FileFilter() {
		@Override
		public String getDescription() {
			return "VB-PRO Dictionary";
		}	
		
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().endsWith(".vbpro");
		}
	};
    
    public OpenDictionaryAction(Yoshikoder yk) {
        super(yk, OpenDictionaryAction.class.getName());
        chooser = new JFileChooser();
		chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
		chooser.addChoosableFileFilter(ykd);
		chooser.addChoosableFileFilter(vbpro); 
		chooser.setFileFilter(ykd);
    }
    
    public void actionPerformed(ActionEvent e) {
    	int resp = chooser.showOpenDialog(yoshikoder);
    	if (resp != JFileChooser.APPROVE_OPTION)
    		return;
    	final File f = chooser.getSelectedFile();
    	
    	if (chooser.getFileFilter().equals(ykd)){
    		
    		tworker = new TaskWorker(yoshikoder){
                YKDictionary dict;
                protected void doWork() throws Exception {
                    dict = ImportUtil.importYKDictionary(f);
                    if (dict == null) 
                        throw new Exception("Null dictionary returned");
                }
                protected void onSuccess() {
                    yoshikoder.setDictionary(dict);
                    yoshikoder.setUnsavedChanges(true);
                }
                protected void onError() {
                    DialogUtil.yelp(yoshikoder, "Could not open dictionary", e); 
                }
            };
            tworker.start();
    		
    	} else if (chooser.getFileFilter().equals(vbpro)){
    		byte[] fileBytes = null;
    		try {
                fileBytes = FileUtil.getBytes(f, 1000);
            } catch (IOException ioe){
                DialogUtil.yelp(yoshikoder, "Could not open file " + f.getName(), ioe);
                return;
            }
            
            final PreviewPanel preview = new PreviewPanel(fileBytes, yoshikoder.getDefaultEncoding());
            int i = JOptionPane.showConfirmDialog(yoshikoder, preview, 
                    "Preview Dictionary",  JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.PLAIN_MESSAGE);    
            if (i != JOptionPane.OK_OPTION)
                return;
            
            tworker = new TaskWorker(yoshikoder){
                VBProFileParser parser = new VBProFileParser();
                YKDictionary importedDictionary; 
                
                protected void doWork() throws Exception {
                    importedDictionary = parser.parse(f, preview.getSelectedEncoding().name());
                    importedDictionary.setName(f.getName());
                    if (parser.getErrors().size()>0)
                        throw new Exception("non-fatal import errors");
                }
                protected void onSuccess() {
                    yoshikoder.setDictionary(importedDictionary);
                    yoshikoder.setUnsavedChanges(true);
                }
                protected void onError() {
                    if (parser.getErrors() == null){
                        // serious error occurred before parser had a chance to work
                        DialogUtil.yelp(yoshikoder, "Could not parse contents of VBPro file", e);
                    } else {
                        // nonfatal errors
                        List l = parser.getErrors();
                        if (l.size() > 0){
                            JTextArea area = new JTextArea(20, 40); 
                            area.setFont(yoshikoder.getDisplayFont());
                            area.setEditable(false);
                            area.setLineWrap(true);
                            area.setWrapStyleWord(true);
                            area.append("There were some problems importing a VBPro dictionary from " +
                                    f.getName() + "\n\n");
                            area.append("Unparseable or duplicate patterns are listed below.\n\n");
                            area.append("Would you like to discard these patterns and import the rest?\n\n");
                            for (Iterator iter = l.iterator(); iter.hasNext();) {
                                VBProFileParser.BadPattern bp = 
                                    (VBProFileParser.BadPattern)iter.next();
                                area.append(bp + "\n");
                            }
                            area.setCaretPosition(0);
                            // throw it in anyway if they want us to
                            int resp = 
                                JOptionPane.showConfirmDialog(yoshikoder, new JScrollPane(area),
                                    "Problem Patterns in Import", 
                                    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                            if (resp == JOptionPane.YES_OPTION){
                                yoshikoder.setDictionary(importedDictionary);
                                yoshikoder.setUnsavedChanges(true);
                            } 
                        }
                    }
                }
            };
            tworker.start();
    	}

    }
    
}
