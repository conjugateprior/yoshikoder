package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class FontPanel extends JPanel {

	static String previewText = Messages.getString("FontPanel.fontPreviewLabel"); 
	
	SpinnerModel fsModel;
    JComboBox fontFamilyCombo;
    DefaultComboBoxModel fontFamilyComboModel;
    
    Font font;
    
    public FontPanel(Font f) {
		super(new BorderLayout());
		font = f;
		
		final JTextArea preview  = new JTextArea(4,20);
		preview.setText(previewText);
		preview.setFont(f);
		
		fsModel = new SpinnerNumberModel(f.getSize(), 8, 64, 1); 
		final JSpinner sizeSpinner = new JSpinner(fsModel);
		sizeSpinner.setValue(new Integer(f.getSize()));
		sizeSpinner.addChangeListener(new ChangeListener(){
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				Integer val = (Integer)sizeSpinner.getValue();
				font = new Font(font.getFamily(), Font.PLAIN, val.intValue());
				preview.setFont(font);
			}
		});
		
		List ffn = DialogUtil.getFontFamilyNames();
		fontFamilyComboModel = 
			new DefaultComboBoxModel(ffn.toArray(new String[ffn.size()]));
		fontFamilyCombo = new JComboBox(fontFamilyComboModel);
		fontFamilyCombo.setSelectedItem(f.getFamily());
		fontFamilyCombo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String newFamily = (String)fontFamilyCombo.getSelectedItem();
				font = new Font(newFamily, Font.PLAIN, font.getSize());
				preview.setFont(font);
			}
		});
		
		Box bbox = Box.createHorizontalBox();
		bbox.add(fontFamilyCombo);
		bbox.add(Box.createHorizontalStrut(5));
		bbox.add(sizeSpinner);
		bbox.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
		
		add(new JScrollPane(preview), BorderLayout.CENTER);
		add(bbox, BorderLayout.SOUTH);
    }

    public Font getDisplayFont(){
    	return font;
    }
    
}
