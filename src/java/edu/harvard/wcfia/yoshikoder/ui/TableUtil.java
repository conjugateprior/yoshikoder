package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class TableUtil {

	public static void packTableColumns(JTable table, int margin){
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int ii=0; ii<table.getColumnCount(); ii++){
			packColumn(table, ii, margin);
		}
	}
	
	public static void packColumn(JTable table, int vColIndex, int margin){
		TableModel model = table.getModel();
		DefaultTableColumnModel colModel = 
			(DefaultTableColumnModel)table.getColumnModel();
		TableColumn col = colModel.getColumn(vColIndex);
		int width = 0;
		
		/*
		TableCellRenderer renderer = col.getHeaderRenderer();
		if (renderer == null)
			renderer = table.getTableHeader().getDefaultRenderer();
		Component comp = renderer.getTableCellRendererComponent(
				table, col.getHeaderValue(), false, false, 0, 0);
		width = comp.getPreferredSize().width;
		*/	
			
		TableCellRenderer renderer;
		Component comp;
		for (int r=0; r<table.getRowCount(); r++){
			renderer = table.getCellRenderer(r, vColIndex);
			comp = renderer.getTableCellRendererComponent(
					table, model.getValueAt(r, vColIndex),
					false, false, r, vColIndex);
			width = Math.max(width, comp.getPreferredSize().width);
		}
	
		System.err.println(width);
		width += 2*margin;
		col.setPreferredWidth(width);
	}
	
	public static void main(String[] args) {
		JTable t = new JTable(1, 2);
		t.setValueAt("Foo", 0, 0);
		t.setValueAt("Ggogogogoogog", 0, 1);
		t.setTableHeader(null);
		packTableColumns(t, 2);
		JOptionPane.showMessageDialog(null, new JScrollPane(t));
	}
	
	
}
