package edu.harvard.wcfia.yoshikoder.ui;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * @author will
 */
public class ReportTable extends JTable {

    /**
     * @param dm
     */
    public ReportTable(TableModel dm) {
        super(dm);
    }

    /**
     * @param dm
     * @param cm
     */
    public ReportTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    /**
     * @param dm
     * @param cm
     * @param sm
     */
    public ReportTable(TableModel dm, TableColumnModel cm,
            ListSelectionModel sm) {
        super(dm, cm, sm);
    }

}
