package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.util.Messages;

/**
 * @author will
 */
public class DictionaryTreeCellRenderer extends DefaultTreeCellRenderer
        implements TreeCellRenderer {
    
    private Icon categoryOpenIcon;
    private Icon categoryClosedIcon;
    private Icon patternIcon;

    public DictionaryTreeCellRenderer() {
        super();
        String openPath = 
        	Messages.getString("DictionaryTreeCellRenderer.categoryOpenIconName");
        String closedPath = 
        	Messages.getString("DictionaryTreeCellRenderer.categoryClosedIconName");
        String patternPath = 
        	Messages.getString("DictionaryTreeCellRenderer.patternIconName");
        try {
            ClassLoader cl = 
                DictionaryTreeCellRenderer.class.getClassLoader();
            categoryOpenIcon = 
                new ImageIcon(cl.getResource(openPath));
            categoryClosedIcon = 
                new ImageIcon(cl.getResource(closedPath));
            patternIcon = 
                new ImageIcon(cl.getResource(patternPath));
        } catch (NullPointerException npe){
            System.err.println("Could not find icons in resource bundle"); //$NON-NLS-1$
            try {
                categoryOpenIcon = 
                	new ImageIcon("resources/" + openPath); //$NON-NLS-1$
                categoryClosedIcon = 
                	new ImageIcon("resources/" + closedPath); //$NON-NLS-1$
                patternIcon = 
                	new ImageIcon("resources/" + patternPath); //$NON-NLS-1$
            } catch (Exception ioe){
                System.err.println("Or in the filesystem"); //$NON-NLS-1$
            }
        }
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean focus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, 
                leaf, row, hasFocus);
        Node n = (Node)value;
        if (n instanceof PatternNode) 
            setIcon(patternIcon);
        else
            if (expanded)
                setIcon(categoryOpenIcon);
            else
                setIcon(categoryClosedIcon);
        setToolTipText(n.getPopup());

        return this;
    }

}