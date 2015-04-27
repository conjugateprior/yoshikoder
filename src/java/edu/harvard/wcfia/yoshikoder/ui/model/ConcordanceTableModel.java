package edu.harvard.wcfia.yoshikoder.ui.model;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceLine;

/**
 * 
 * <b>Do not use.  Not safe for Tokens</b>
 * @author will
 */
public class ConcordanceTableModel 
	extends AbstractTableModel implements TableModel {

    protected Concordance concordance;
    protected int windowSize;
    
    public ConcordanceTableModel(Concordance c) {
        super();
        concordance = c;
        if (c != null)
        	windowSize = c.getWindowSize();	
    }

    public int getColumnCount() {
        return windowSize*2+1;
    }

    public int getRowCount() {
        return concordance != null ? concordance.size() : 0;
    }
    
    // implements an adaptor between the table model and the concordance
    public Object getValueAt(int rowIndex, int columnIndex) {
        ConcordanceLine line = 
            (ConcordanceLine)concordance.getLines().get(rowIndex);
        if (line != null){
            if (columnIndex < windowSize){
                int diff = windowSize-line.getLeftHandSide().size();
                return columnIndex >= diff ? 
                        line.getLeftHandSide().get(columnIndex-diff) : null;
            } else if (columnIndex == windowSize){
                return line.getTarget();
            } else {
                int rIndex = columnIndex - (windowSize+1);
                return rIndex < line.getRightHandSide().size() ? 
                        line.getRightHandSide().get(rIndex) : null;
            }
        } else {
            return null;
        }
    }

    public int getWindowSize(){
        return windowSize;
    }
    
}
