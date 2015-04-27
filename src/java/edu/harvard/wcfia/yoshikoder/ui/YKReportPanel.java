package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.reporting.YKReport;

public class YKReportPanel extends JPanel {

    protected YKReport report;
    protected Yoshikoder yoshikoder;
    
    public YKReportPanel(Yoshikoder yk, YKReport rep){
        report = rep;
        yoshikoder = yk;
    
        TableModel model = rep;
        TableSorter sorter = new TableSorter(model);
        JTable table = new JTable(sorter);
        sorter.setTableHeader(table.getTableHeader());
        table.setFont(yoshikoder.getDisplayFont());
        TableUtil.packColumn(table, 0, 2);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public YKReport getReport(){
        return report;
    }
    
}
