package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.DuplicateException;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;

public class AddWordsToCategoryAction extends YoshikoderAction {

	protected JTextArea area;
	
	public AddWordsToCategoryAction(Yoshikoder yk) {
		super(yk, AddWordsToCategoryAction.class.getName());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Node node = yoshikoder.getSelectedNode();
		YKDictionary dict = yoshikoder.getDictionary();
		CategoryNode cnode = null;
		if (node instanceof CategoryNode)
			cnode = (CategoryNode)node;
		else
			cnode = (CategoryNode)node.getParent();
		
		if (area == null){
			area = new JTextArea(20,30);
			area.setEditable(true);
			area.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			area.setDropMode(DropMode.INSERT);
			area.setLineWrap(true);
			area.setWrapStyleWord(true);
		} else {
			area.setText("");
		}
		
		JScrollPane pane = new JScrollPane(area);
		int resp = JOptionPane.showConfirmDialog(yoshikoder, pane, "Add patterns to " + cnode.getName(), 
			JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (resp != JOptionPane.OK_OPTION)
			return;
		
		String str = area.getText();
		String[] spl = str.split("[^\\w]+");
		List<String> errors = new ArrayList<String>();
		for (int ii = 0; ii < spl.length; ii++) {
			String newpat = spl[ii];
			try {
				dict.addPattern(spl[ii], null, cnode);
			} catch (DuplicateException dex){
				// quietly supress duplicates
			} catch (Exception ex){
				errors.add(spl[ii]);
			}
		}
		yoshikoder.setSelectedNode(cnode);
		
		if (errors.size() > 0){
			StringBuffer sb = new StringBuffer();
			sb.append("There were some problems adding the patterns:\n\n");
			for (Iterator iterator = errors.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				sb.append(string);
				sb.append(" ");
			}
			sb.append("\nThis is usually because " + 
					"a dictionary entry of the same name already exists.");
			
			area.setText(sb.toString());
			JOptionPane.showMessageDialog(yoshikoder, new JScrollPane(area), "Error adding patterns to " + cnode.getName(), 
				JOptionPane.PLAIN_MESSAGE);
		}
	}

	public static void main(String[] args) {
		String[] spl = "oranges are not the only  fruit \n otherwise".split("[^\\w]+");
		for (int ii = 0; ii < spl.length; ii++) {
			System.err.println(">>" + spl[ii] + "<<");
		}
	}
	
}
