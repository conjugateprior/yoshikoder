package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceLine;

public class ListConcordancePanel extends JPanel {
	
	class RightListRenderer extends JLabel implements ListCellRenderer{
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean hasFocus){
			setHorizontalAlignment(JLabel.LEFT);
			setHorizontalTextPosition(SwingConstants.LEFT);
			setText(value.toString());
			setFont(list.getFont());
			return this;
		}
	}
	
	class MiddleListRenderer extends JLabel implements ListCellRenderer{
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean hasFocus){
			setHorizontalAlignment(JLabel.CENTER);
			setHorizontalTextPosition(SwingConstants.CENTER);
			setText(value.toString());
			setFont(list.getFont().deriveFont(Font.BOLD));
			return this;
		}
	}	
	
	class LeftListRenderer extends JLabel implements ListCellRenderer{
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean hasFocus){
			setHorizontalAlignment(JLabel.RIGHT);
			setHorizontalTextPosition(SwingConstants.RIGHT);
			setText(value.toString());
			setFont(list.getFont());
			return this;
		}
	}
	
	private Concordance concordance;
	
	private JList conc1list;
	private JList conc2list;
	private JList conc3list;
	
	private DefaultListModel model1;
	private DefaultListModel model2;
	private DefaultListModel model3;
	
	private String[] concat(ConcordanceLine line){
	    String lhs = line.getLeftHandSideView();
        String rhs = line.getRightHandSideView();
        // empty left or right hand sides get a space added to visually 
        // balance the jlist
        String[] arr = 
		    new String[]{(lhs.length()>0 ? lhs : " "), 
		        line.getTargetView(),
		        rhs.length()>0 ? rhs : " "};
		return arr;
	}
	
	public Concordance getConcordance(){
		return concordance;
	}
	
	public void setConcordance(Concordance conc){
		concordance = conc;
		model1.clear();
		model2.clear();
		model3.clear();
		if (conc != null) {
			for (Iterator iter = conc.iterator(); iter.hasNext();) {
				ConcordanceLine line = (ConcordanceLine) iter.next();
				String[] els = concat(line);
				model1.addElement(els[0]);
				model2.addElement(els[1]);
				model3.addElement(els[2]);
			}
		}
	}
	
	public void setDisplayFont(Font f){
    	    conc1list.setFont(f);
    	    conc2list.setFont(f.deriveFont(Font.BOLD));
    	    conc3list.setFont(f);
	}
	
	public ListConcordancePanel(Concordance conc){
		this();
		setConcordance(conc);
	}
	
	public ListConcordancePanel() {
        super(new BorderLayout());

        model1 = new DefaultListModel();
        model2 = new DefaultListModel();
        model3 = new DefaultListModel();

        conc1list = new JList(model1);
        conc1list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);                
        conc1list.setCellRenderer(new LeftListRenderer());

        conc2list = new JList(model2);
        conc2list.setFont(conc2list.getFont().deriveFont(Font.BOLD));
        conc2list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);                
        conc2list.setCellRenderer(new MiddleListRenderer());

        conc3list = new JList(model3);
        conc3list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);                
        conc3list.setCellRenderer(new RightListRenderer());

        JPanel cont = new JPanel();
        cont.setLayout(new BoxLayout(cont, BoxLayout.X_AXIS));
        cont.setBackground(Color.white);
        cont.add(Box.createHorizontalGlue());
        cont.add(conc1list);
        cont.add(Box.createHorizontalStrut(10));
        cont.add(conc2list);
        cont.add(Box.createHorizontalStrut(10));
        cont.add(conc3list);
        cont.add(Box.createHorizontalGlue());
        JScrollPane contscroll = new JScrollPane(cont);

        contscroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contscroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        contscroll.setPreferredSize(new Dimension(600,200));
        
        add(contscroll, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
        JOptionPane.showMessageDialog((JFrame)null, new ListConcordancePanel() );
    }
	
}
