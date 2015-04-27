package edu.harvard.wcfia.yoshikoder.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.reporting.DictionaryComparisonReport;
import edu.harvard.wcfia.yoshikoder.reporting.EntryFrequencyMap;
import edu.harvard.wcfia.yoshikoder.reporting.YKReport;
import edu.harvard.wcfia.yoshikoder.ui.TableSorter;
import edu.harvard.wcfia.yoshikoder.ui.TableUtil;

public class ComparisonReportDialog extends YKReportDialog {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.ui.dialog.ComparisonReportDialog");
    
    protected YKReport otherReport;
    protected JCheckBox showPatternsCheck;
        
    protected JTable table;
    
    public ComparisonReportDialog(Yoshikoder parent, DictionaryComparisonReport rep){
        super(parent, rep);
        otherReport = makeOtherReport();
    }
    
    protected YKReport makeOtherReport(){
        // make a categories only report
        String title = report.getTitle();
        String desc = report.getDescription();
        String dictName = report.getDictionaryName();
        DocumentList dl = report.getDocuments();
        EntryFrequencyMap efm1 = 
            ((DictionaryComparisonReport)report).getFirstEntryFrequencyMap();
        EntryFrequencyMap efm2 = 
            ((DictionaryComparisonReport)report).getSecondEntryFrequencyMap();
        YKReport rep = 
            new DictionaryComparisonReport(title, desc, dictName, dl, efm1, efm2, false);        
        return rep;
    }
    
    protected void setReport(YKReport rep){
        TableSorter sorter = new TableSorter(rep);
        table.setModel(sorter);
        sorter.setTableHeader(table.getTableHeader());
        table.setFont(yoshikoder.getDisplayFont());
        TableUtil.packColumn(table, 0, 2);
    }
    
    protected void makeGUI(){
        Container cPane = getContentPane();
        cPane.setLayout(new BorderLayout());
        
        JPanel buttons = createButtonPanel();
        cPane.add(buttons, BorderLayout.SOUTH);
        
        table = new JTable(report.getRowCount(), report.getColumnCount());
        setReport(report);

        JPanel central = new JPanel(new BorderLayout());
        central.add(new JScrollPane(table), BorderLayout.CENTER);
        
        showPatternsCheck = new JCheckBox("Show categories only", false);
        
        showPatternsCheck.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                YKReport tmp = report;
                report = otherReport;
                otherReport = tmp;
                setReport(report);
            }
        });
        central.add(showPatternsCheck, BorderLayout.SOUTH);
        
        JPanel description = new JPanel(new GridLayout(3,1));
        DocumentList dl = report.getDocuments();
        YKDocument d1 = (YKDocument)dl.get(0);
        YKDocument d2 = (YKDocument)dl.get(1);
        description.add(new JLabel("Comparing Documents:"));
        description.add(new JLabel("(1) " + d1.getTitle()));
        description.add(new JLabel("(2) " + d2.getTitle()));
        description.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        central.add(description, BorderLayout.NORTH);
        
        central.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        cPane.add(central, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(yoshikoder);        
    }
}
