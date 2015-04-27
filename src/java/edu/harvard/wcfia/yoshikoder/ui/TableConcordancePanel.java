package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.ui.model.ConcordanceTableModel;

/**
 * @author will
 */
public class TableConcordancePanel extends JPanel {

    protected JTable table;
    protected Concordance concordance;

    public TableConcordancePanel(Concordance conc) {
        super(new BorderLayout());
        makeGUI();
        setConcordance(conc);
    }

    public TableConcordancePanel(){
        super(new BorderLayout());
        makeGUI();
    }
    
    public void setDisplayFont(Font f){
    	table.setFont(f);
    }
    
    protected void makeGUI(){
        table = new JTable() {
            public Component prepareRenderer(TableCellRenderer renderer,
                    int rowIndex, int vColIndex) {
                Component c = super.prepareRenderer(renderer, rowIndex,
                        vColIndex);
                if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
                    c.setBackground(Color.lightGray);
                } else {
                    c.setBackground(getBackground());
                }
                if (vColIndex == getColumnCount()/2)
                	c.setFont(c.getFont().deriveFont(Font.BOLD));
                return c;
            }
        };
        table.setTableHeader(null);
        
        JScrollPane scr = new JScrollPane(table);
        scr.setPreferredSize(new Dimension(250,120));
        add(scr, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    }
    
    public void setConcordance(Concordance conc){
        concordance = conc;
        ConcordanceTableModel ctm = new ConcordanceTableModel(concordance);
        table.setModel(ctm);
        
        TableUtil.packTableColumns(table, 5);
        //table.repaint();
    }
    
    public Concordance getConcordance(){
        return concordance;
    }
}
