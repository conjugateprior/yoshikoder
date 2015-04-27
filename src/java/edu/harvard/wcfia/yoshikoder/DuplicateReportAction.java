package edu.harvard.wcfia.yoshikoder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;

/**
 * Checks for the same pattern in different places in the same dictionary.
 * If duplicate patterns exist, pops up a non-modal dialog listing their locations.
 * 
 * @author will
 *
 */
public class DuplicateReportAction extends YoshikoderAction {
    
    public DuplicateReportAction(Yoshikoder yk) {
        super(yk, DuplicateReportAction.class.getName());
    }

    protected void recurse(Map nameToNodes, Node node){
        if (node instanceof CategoryNode){
            for (Enumeration en = node.children(); en.hasMoreElements();){
                Node n = (Node)en.nextElement();
                recurse(nameToNodes, n);
            }
        } else {
            String nodeName = node.getName(); 
            List l = (List)nameToNodes.get(nodeName);
            if (l == null){
                l = new ArrayList();
                nameToNodes.put(nodeName, l);
            }
            l.add(node);
        } 
    }
    
    public void actionPerformed(ActionEvent e) {
        YKDictionary dict = yoshikoder.getDictionary();
        Map nameToNodes = new HashMap();
        Node root = dict.getDictionaryRoot();
        recurse(nameToNodes, root); // full nameToNodes
        StringBuffer sb = new StringBuffer();
        int duplicatePatterns = 0;
        for (Iterator iter = nameToNodes.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            List l = (List)entry.getValue();
            if (l.size() > 1){
                duplicatePatterns++;
                sb.append("\"" + (String)entry.getKey() + "\" appears in categories:\n");
                for (Iterator iterator = l.iterator(); iterator.hasNext();) {
                    Node node = (Node) iterator.next();
                    TreePath path = dict.getPath((Node)node.getParent());
                    sb.append("\t" + path.getPathComponent(0).toString());
                    for (int ii = 1; ii < path.getPathCount(); ii++) {
                        sb.append(">" + path.getPathComponent(ii).toString()); // path
                    }
                    sb.append("\n");
                }
                sb.append("\n"); // line gap
            }
        }
        if (duplicatePatterns == 0){
            JOptionPane.showMessageDialog(yoshikoder, 
                    "There are no duplicate patterns.", "Duplicate Pattern Report", 
                    JOptionPane.PLAIN_MESSAGE);
        } else {
            JTextArea area = new JTextArea(30, 40);
            area.setFont(yoshikoder.getDisplayFont());
            area.setEditable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setText(sb.toString());
            area.setCaretPosition(0);
            
            JLabel label = 
                new JLabel("There are " + duplicatePatterns + " duplicate patterns", 
                        SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            panel.add(label, BorderLayout.NORTH);
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
            JDialog dia = new JDialog(yoshikoder, "Duplicate Pattern Report", false);
            dia.getContentPane().add(panel);
            dia.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dia.pack();
            dia.setLocationRelativeTo(yoshikoder);
            dia.show();
        }
    }

}
