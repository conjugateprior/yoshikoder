package edu.harvard.wcfia.yoshikoder.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.reporting.DictionaryFrequencyReport;
import edu.harvard.wcfia.yoshikoder.reporting.YKReport;
import edu.harvard.wcfia.yoshikoder.ui.TableSorter;
import edu.harvard.wcfia.yoshikoder.ui.TableUtil;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.ExportUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class YKDictionaryReportDialog extends JDialog {

	protected DictionaryFrequencyReport catsOnlyReport;
	protected DictionaryFrequencyReport catsAndPatsReport;
	protected DictionaryFrequencyReport current;
	
	protected TaskWorker worker;
    protected Yoshikoder yoshikoder;
		
    protected JTable table;
    protected boolean catsOnlyFlag;
        
    public YKDictionaryReportDialog(Yoshikoder parent, 
    		DictionaryFrequencyReport cats, DictionaryFrequencyReport catsAndPats, boolean catsOnly){
        super(parent, "Dictionary Report", true);
        yoshikoder = parent;
        
        // data
        catsOnlyReport = cats;
        catsAndPatsReport = catsAndPats;
        if (catsOnly)
        	current = catsOnlyReport;
        else
        	current = catsAndPatsReport;
        catsOnlyFlag = catsOnly;
        
        // interface
        Container cPane = getContentPane();
        cPane.setLayout(new BorderLayout());
        
        // insert current report
        TableSorter sorter = new TableSorter( current );
        table = new JTable(sorter);
        sorter.setTableHeader(table.getTableHeader());
        table.setFont(yoshikoder.getDisplayFont());
        TableUtil.packColumn(table, 0, 2);

        JPanel central = new JPanel(new BorderLayout());
        central.add(new JScrollPane(table), BorderLayout.CENTER);

        /*
        String desc = current.getDescription();
        if (desc != null){
            JTextArea darea = new JTextArea(desc);
            darea.setEditable(false);
            darea.setFont(yoshikoder.getDisplayFont());
            darea.setBackground(this.getBackground());
            darea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            central.add(darea, BorderLayout.NORTH);
        }
        */
        
        central.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        cPane.add(central, BorderLayout.CENTER);

        // 
        final JCheckBox categoriesOnlyCheck = new JCheckBox("Show categories only", catsOnlyFlag);        
        categoriesOnlyCheck.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){ // switch reports if necessary
                catsOnlyFlag = categoriesOnlyCheck.isSelected();
            	System.err.println("cats only flag is set to " + catsOnlyFlag);
                if (catsOnlyFlag && current.getShowPatterns()){
                	current = catsOnlyReport;
                	setReport(current);
                } else if (!catsOnlyFlag && !current.getShowPatterns()){
                	current = catsAndPatsReport;
                	setReport(current);
                }
            }
        });
        JPanel checkPanel = new JPanel(new BorderLayout());
        checkPanel.add(categoriesOnlyCheck, BorderLayout.WEST);
        central.add(checkPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(yoshikoder);
    
    }
    
    public DictionaryFrequencyReport getCurrentReport(){
    	return current;
    }
    
    /*
    protected YKReport makeCatsAndPatsReport(){ // DO show the patterns
        String title = report.getTitle();
        String desc = report.getDescription();
        String dictName = report.getDictionaryName();
        YKDocument doc = (YKDocument)report.getDocuments().get(0);
        EntryFrequencyMap efm = 
            ((DictionaryFrequencyReport)report).getEntryFrequencyMap();
        YKReport rep = 
            new DictionaryFrequencyReport(title, desc, dictName, doc, efm, true);        
        return rep;
    }
    */
    
    protected void setReport(YKReport rep){
        TableSorter sorter = new TableSorter(rep);
        table.setModel(sorter);
        sorter.setTableHeader(table.getTableHeader());
        table.setFont(yoshikoder.getDisplayFont());
        TableUtil.packColumn(table, 0, 2);
    }
    
}
