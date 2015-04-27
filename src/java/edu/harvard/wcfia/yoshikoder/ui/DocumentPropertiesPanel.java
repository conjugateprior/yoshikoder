package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.Color;
import java.io.File;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.YKDocumentFactory;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;

public class DocumentPropertiesPanel extends CommitablePanel {

	class LocaleWrapper{
		Locale locale;
		LocaleWrapper(Locale l){
			locale = l;
		}
		public String toString(){
			return locale.getDisplayName();
		}
	}
	
	protected YKDocument document;
	
	protected JTextField titleField;
	protected JTextField charsetField;
	protected JComboBox localeList;
	protected JTextField locationField;
	protected JComboBox fontChoice;
	protected JTextField fileField;
    
	public DocumentPropertiesPanel(YKDocument doc) {
		super();
		document = doc;
		
		titleField = new JTextField();
		titleField.setText(document.getTitle());
		
		Color bg = getBackground();
		
		locationField = 
			new JTextField(document.getLocation().getAbsolutePath());
		locationField.setEditable(false);
		locationField.setBackground(bg);
		
		charsetField = new JTextField(document.getCharsetName());
		charsetField.setEditable(false);
		charsetField.setBackground(bg);
		
		Locale[] locs = FileUtil.getAvailableLocales();
		DefaultComboBoxModel dcm = new DefaultComboBoxModel();
		for (int ii=0; ii<locs.length; ii++)
			dcm.addElement(new LocaleWrapper(locs[ii]));
		localeList = new JComboBox(dcm);
		LocaleWrapper lw = new LocaleWrapper(document.getLocale());
		localeList.setSelectedItem(lw);

		/*
        Font font = document.getPreferredFont();
		List fontNames = DialogUtil.getFontFamilyNames();
		
        fontChoice = 
            new JComboBox(fontNames.toArray(new String[fontNames.size()]));
         
		fontChoice.setSelectedItem(doc.getPreferredFont().getFamily());
		*/
        
		fileField = new JTextField();
		fileField.setText(doc.getLocation().getAbsolutePath());
		fileField.setEditable(false);
		
		addField("Title:", titleField);
		addField("File:", fileField);
		addWidgetInline("Locale:", localeList);
		addField("Encoding:", charsetField);
		//addWidgetInline("Font", fontChoice);
        
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }

	public void commit() throws CommitException {
		//Locale l = 
		//	((LocaleWrapper)localeList.getSelectedValue()).locale;
		//document.setLocale(l);
	}
    
    public static void main(String[] args) {
        JDialog dia = new JDialog((JFrame)null, "Properties");
        YKDocument doc = 
            YKDocumentFactory.createYKDocument(new File("/Users/will/bosnia.csv"),
                    "bosnia.csv", "UTF-8", Locale.getDefault());
        DocumentPropertiesPanel panel = new DocumentPropertiesPanel(doc);
        dia.getContentPane().add(panel);
        dia.pack();
        dia.show();
    }

}
