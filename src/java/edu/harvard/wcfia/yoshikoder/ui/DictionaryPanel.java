package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.dnd.DnDConstants;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;

import edu.harvard.wcfia.yoshikoder.dictionary.DemoDictionary;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.util.Messages;

/**
 * A self-contained tree widget that displays the dictionary from 
 * a YKProject and allows
 * drag-and-drop tree restructuring.
 * 
 * @author will
 */
public class DictionaryPanel extends JPanel {

    protected JTree dictionaryTree;
    protected YKDictionary dictionary;
    
    public DictionaryPanel(YKDictionary dict){
        super(new BorderLayout());
        dictionary = dict;
        makeGUI();
    }
   
    protected void makeGUI() {
        dictionaryTree = new JTree(dictionary);
        dictionaryTree.setSelectionRow(0);
        dictionaryTree.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        ToolTipManager.sharedInstance().registerComponent(dictionaryTree);
        dictionaryTree.setCellRenderer(new DictionaryTreeCellRenderer());
        
        TreeDragSource ds = 
            new TreeDragSource(dictionaryTree, DnDConstants.ACTION_MOVE);
        TreeDropTarget dt = new TreeDropTarget(dictionaryTree);
        
        JScrollPane dTree = new JScrollPane(dictionaryTree);
        dTree.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        dTree.setPreferredSize(new Dimension(200,400));
        add(dTree, BorderLayout.CENTER);
        
        JLabel topLabel = new JLabel(Messages.getString("DictionaryPanel.topLabel"));
        topLabel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        add(topLabel, BorderLayout.NORTH);
    }

    public YKDictionary getDictionary() {
        return (YKDictionary)dictionaryTree.getModel();
    }

    public void setDictionary(YKDictionary dict) {
        dictionaryTree.setModel(dict);
        dictionaryTree.setSelectionRow(0);
    }
        
    public Font getDisplayFont(){
        return dictionaryTree.getFont();
    }
    
    public void setDisplayFont(Font f){
        dictionaryTree.setFont(f);
    }
    
    public JTree getTree(){
        return dictionaryTree;
    }
    
    public Node getSelectedNode(){
        TreePath path = dictionaryTree.getSelectionPath();
        if (path != null)
            return (Node)path.getLastPathComponent();
        else 
            return null;  
    }
    
    public void setSelectedNode(Node n){
        dictionaryTree.setSelectionPath(getTreePath(n));
    }

    protected TreePath getTreePath(Node n){
        List l = new ArrayList();
        l.add(n);
        Node parent = n;
        while ((parent = (Node)parent.getParent()) != null)
            l.add(0, parent);
        return new TreePath( l.toArray(new Node[l.size()]) );
    }
    
    public static void main(String[] args) {
        DemoDictionary dict = new DemoDictionary();
        DictionaryPanel panel = new DictionaryPanel(dict);
        JOptionPane pane = new JOptionPane(panel);
        JDialog dia = pane.createDialog((JFrame)null, "Title");
        dia.show();
        System.exit(0);
    }
    
}