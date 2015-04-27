package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.YKDocumentFactory;
import edu.harvard.wcfia.yoshikoder.util.CharsetWrapper;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class ImportDocumentPanel extends CommitableJPanel {
    
    private static int PREVIEW_SIZE = 1024; // in bytes
    private static String NULL_FONT_NAME = "-";
    
    class LocaleWrapper{
        Locale locale;
        LocaleWrapper(Locale l){
            locale = l;
        }
        public String toString(){
            return locale.getDisplayName();
        }
    }
    
    protected Yoshikoder yoshikoder;
    protected File file;
    
    protected DefaultComboBoxModel localeModel;
    protected DefaultComboBoxModel encodingModel;
    protected DefaultComboBoxModel fontModel;
    
    protected JTextField docTitle;
    protected JTextArea preview;
    protected JComboBox localeList;
    protected JComboBox encodingList;
    protected JComboBox fontList;
    
    byte[] stream;
    
    public ImportDocumentPanel(Yoshikoder yk, File f) {
        super(new GridBagLayout());
        yoshikoder = yk;
        file = f;
        
        localeModel = new DefaultComboBoxModel();
        Locale[] locs = FileUtil.getAvailableLocales();
        localeModel.addElement(new LocaleWrapper(Locale.getDefault()));
        for (int ii=0; ii<locs.length; ii++)
            localeModel.addElement(new LocaleWrapper(locs[ii]));
        localeList = new JComboBox(localeModel);
        localeList.setSelectedItem(Locale.getDefault());
        
        encodingModel = new DefaultComboBoxModel();
        //String [] names = FileUtil.getEncodingNames();
        
        
        for (CharsetWrapper wrapper : FileUtil.getCharsetList()) {
			encodingModel.addElement(wrapper);
		}
        encodingList = new JComboBox(encodingModel);
        encodingList.setSelectedItem(new CharsetWrapper(yoshikoder.getDefaultEncoding()));
        // set to locale picker width since that'll be the longest
        encodingList.setPreferredSize( new Dimension(
                localeList.getPreferredSize().width,
                encodingList.getPreferredSize().height) );
        encodingList.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                applyEncoding();
            }
        });
        
        fontModel = new DefaultComboBoxModel();
        fontModel.addElement(NULL_FONT_NAME); // no preference
        for (Iterator iter = DialogUtil.getFontFamilyNames().iterator(); iter.hasNext();) {
            String fname = (String) iter.next();
            fontModel.addElement(fname);
        }
        fontList = new JComboBox(fontModel);
        fontList.setPreferredSize( new Dimension(
                localeList.getPreferredSize().width,
                fontList.getPreferredSize().height) );
        fontList.setSelectedIndex(0);
        fontList.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String ffn = (String)fontList.getSelectedItem();
                if (!NULL_FONT_NAME.equals(ffn))
                    applyFont();
            }
        });
        
        preview = new JTextArea(10,40);
        preview.setEditable(false);
        preview.setLineWrap(true);
        preview.setWrapStyleWord(true);
        preview.setFont(yoshikoder.getDisplayFont());
        
        try {
            stream = FileUtil.getBytes(file, PREVIEW_SIZE);
        } catch (Exception ioe){
            ioe.printStackTrace();
        }
        
        applyEncoding();
        applyFont();
        
        makeGUI();
    }

    protected void makeGUI(){
        GridBagConstraints g = new GridBagConstraints();
        Insets topInsetsLHS = new Insets(0, 0, 0, 10);
        Insets topInsetsRHS = new Insets(0, 0, 0, 0);
        Insets insetsLHS = new Insets(5, 0, 0, 10);
        Insets insetsRHS = new Insets(5, 0, 0, 0);
        JLabel label;
        
        g.gridx = 0; g.gridy = 0;
        g.gridwidth = 1; g.gridheight = 1;
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.EAST;
        g.insets = topInsetsLHS;
        label = new JLabel(Messages.getString("ImportDocumentPanel.title"));
        add(label, g);
        
        // widget
        String fname = file.getName();
        docTitle = new JTextField(fname, 20);
        docTitle.setSelectionStart(0);
        docTitle.setSelectionEnd(fname.length());
        //
        g.gridx = 1; g.gridy = 0;
        g.gridwidth = 2; g.gridheight = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        g.insets = topInsetsRHS;
        add(docTitle, g);
        
        g.gridx = 0; g.gridy = 1;
        g.gridwidth = 1; g.gridheight = 1;
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.EAST;
        g.insets = insetsLHS;
        label = new JLabel(Messages.getString("ImportDocumentPanel.preview"));
        add(label, g);

        // widget
        JScrollPane sp = new JScrollPane(preview);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //
        g.gridx = 1; g.gridy = 1;
        g.gridwidth = 2; g.gridheight= 2;
        g.fill = GridBagConstraints.BOTH; // check
        g.anchor = GridBagConstraints.WEST;
        g.insets = insetsRHS;
        g.weightx = 1; g.weighty = 1; // only this cell gets to stretch when the dialog is resized...
        add(sp, g);
        
        // reverse...
        g.weightx = 0; g.weighty = 0;
        
        g.gridx = 0; g.gridy = 3;
        g.gridwidth = 1; g.gridheight = 1;
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.EAST;
        g.insets = insetsLHS;
        label = new JLabel(Messages.getString("ImportDocumentPanel.encoding"));
        add(label, g);

        g.gridx = 1; g.gridy = 3;
        g.gridwidth = 1; g.gridheight= 1;
        g.fill = GridBagConstraints.HORIZONTAL; // check
        g.anchor = GridBagConstraints.WEST;
        g.insets = insetsRHS;
        add(encodingList, g);

        g.gridx = 0; g.gridy = 4;
        g.gridwidth = 1; g.gridheight = 1;
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.EAST;
        g.insets = insetsLHS;
        label = new JLabel(Messages.getString("ImportDocumentPanel.locale"));
        add(label, g);

        g.gridx = 1; g.gridy = 4;
        g.gridwidth = 1; g.gridheight= 1;
        g.fill = GridBagConstraints.HORIZONTAL; // check
        g.anchor = GridBagConstraints.WEST;
        g.insets = insetsRHS;
        add(localeList, g);
        
        g.gridx = 0; g.gridy = 5;
        g.gridwidth = 1; g.gridheight = 1;
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.EAST;
        g.insets = insetsLHS;
        label = new JLabel(Messages.getString("fontLabel"));
        add(label, g);

        g.gridx = 1; g.gridy = 5;
        g.gridwidth = 1; g.gridheight= 1;
        g.fill = GridBagConstraints.HORIZONTAL; // check
        g.anchor = GridBagConstraints.WEST;
        g.insets = insetsRHS;
        add(fontList, g);
    }
    
    void applyFont(){
        Font newFont = 
            new Font((String)fontList.getSelectedItem(), Font.PLAIN, 
                    yoshikoder.getDisplayFont().getSize());
        preview.setFont(newFont);
    }
    
    // apply the current setting to the byte stream and add as text
    void applyEncoding(){
        Charset enc = ((CharsetWrapper)encodingList.getSelectedItem()).charset;
        
        StringBuffer docbuff = new StringBuffer();
        try {
            ByteArrayInputStream basi = 
                new ByteArrayInputStream(stream);
            BufferedReader in =
                new BufferedReader(new InputStreamReader(basi, enc));
            int next;
            while (((next = in.read()) != -1)) {
                docbuff.append((char)next);
            }
            in.close();
            basi.close();
        } catch (Exception io_e) {
            io_e.printStackTrace();
        }
        preview.setText( docbuff.toString() );
        preview.setCaretPosition(0);
    }
    
    public void commit() throws CommitException {
        String doctitle = docTitle.getText();
        Charset encName = ((CharsetWrapper)encodingList.getSelectedItem()).charset;
        Locale loc = ((LocaleWrapper)localeList.getSelectedItem()).locale;
        String ffn = (String)fontList.getSelectedItem();
        Font f = null;
        if (!NULL_FONT_NAME.equals(ffn))
            f = preview.getFont(); // a preference, null otherwise

        if ((doctitle == null) || doctitle.length()==0){
            throw new CommitException("Please give the document a title");
        }
        
        DocumentList dl = yoshikoder.getDocumentList();
        for (Iterator iter = dl.iterator(); iter.hasNext();) {
            YKDocument doc = (YKDocument) iter.next();
            if (doctitle.equals(doc.getTitle()))
                throw new CommitException("A document with this name already exists.  Please choose another");
        }
        
        YKDocument doc = 
            YKDocumentFactory.createYKDocument(file, doctitle, encName.name(), loc);
        doc.setLocale(loc);
        doc.setPreferedFont(f);
        
        yoshikoder.addDocument(doc);
        yoshikoder.setUnsavedChanges(true);
    }

    
    
    public static void main(String[] args) {
        File f = new File("/Users/will/Documents/algorithm-tokenization.txt");
        Yoshikoder yk = new Yoshikoder();
        yk.show();
        ImportDocumentPanel panel = new ImportDocumentPanel(yk, f);
        JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
        JDialog dia = pane.createDialog(yk, "Import");
        dia.show();
    }
    
}
