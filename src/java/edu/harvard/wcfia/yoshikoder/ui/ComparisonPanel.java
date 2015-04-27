package edu.harvard.wcfia.yoshikoder.ui;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;

public class ComparisonPanel extends FormPanel {

    protected Yoshikoder yoshikoder;
    
    protected JComboBox doc1combo;
    protected JComboBox doc2combo;
    
    public ComparisonPanel(Yoshikoder yk) {
        super();
        yoshikoder = yk;
        
        DocumentList dl = yk.getProject().getDocumentList();
        YKDocument[] docarray = 
            (YKDocument[])dl.toArray(new YKDocument[dl.size()]);
        doc1combo = new JComboBox(docarray);
        doc2combo = new JComboBox(docarray);
        
        YKDocument doc = yoshikoder.getSelectedDocument();
        if (doc != null){
            doc1combo.setSelectedItem(doc);
            int index = doc1combo.getSelectedIndex();
            if ((index+1) < docarray.length)
                doc2combo.setSelectedIndex(index+1);
            else if ((index-1 >= 0))
                doc2combo.setSelectedIndex(index-1);
            else
                doc2combo.setSelectedIndex(index); // ?
        } else {
            doc1combo.setSelectedIndex(0);
            doc2combo.setSelectedIndex(1);
        }
        
        addWidgetInlineFixedWidth("Compare", doc1combo);
        addWidgetInlineFixedWidth("to", doc2combo);
        
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }

    public void setFirstDocument(YKDocument doc){
        doc1combo.setSelectedItem(doc);
    }

    public void setSecondDocument(YKDocument doc){
        doc2combo.setSelectedItem(doc);
    }
    
    public YKDocument getFirstDocument(){
        return (YKDocument)doc1combo.getSelectedItem();
    }
    
    public YKDocument getSecondDocument(){
        return (YKDocument)doc2combo.getSelectedItem();
    }
    
    public void commit() throws CommitException {
    }
    
}
