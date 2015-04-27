package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.DuplicateException;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;

/**
 * Slightly augmented drag and drop code from O'Reilly's Swing book.
 * @author will
 */
public class TreeDropTarget implements DropTargetListener {

    DropTarget target;

    JTree targetTree;

    public TreeDropTarget(JTree tree) {
        targetTree = tree;
        target = new DropTarget(targetTree, this);
    }

    private Node getNodeForEvent(DropTargetDragEvent dtde){
        Point p = dtde.getLocation();
        TreePath path = targetTree.getClosestPathForLocation(p.x, p.y);
        return (Node)path.getLastPathComponent();
    }
    
    // Drop event handlers
    public void dragEnter(DropTargetDragEvent dtde) {
        dragOver(dtde);
    }

    public void dragOver(DropTargetDragEvent dtde) {
        Node node = getNodeForEvent(dtde);
        if (node instanceof PatternNode)
            dtde.rejectDrag();
        else 
            dtde.acceptDrag(DnDConstants.ACTION_MOVE);
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void drop(DropTargetDropEvent dtde) {
        // Figure out where the drop occurred (in relation to the target tree).
        Point pt = dtde.getLocation();
        TreePath parentpath = targetTree.getClosestPathForLocation(pt.x, pt.y);

        // For simplicity's sake, we'll assume that the tree uses the
        // DefaultTreeModel
        // and DefaultMutableTreeNode classes.
        Node parent = (Node)parentpath.getLastPathComponent();

        // Now check to see if it was dropped on a folder. If not, reject it.
        if (parent instanceof PatternNode) {
            dtde.rejectDrop();
            return;
        }

        try {
            // Grab the data.
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (tr.isDataFlavorSupported(flavors[i])) {
                    // It's a usable node, so pull it out of the transferable
                    // object and add it
                    // to our tree.
                    dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                    TreePath p = (TreePath)tr.getTransferData(flavors[i]);
                    Node node = (Node)p.getLastPathComponent();
                    YKDictionary dict = (YKDictionary)targetTree.getModel();
                    if (node instanceof PatternNode)
                        dict.addPattern((PatternNode)node, (CategoryNode)parent);
                    else if (node instanceof CategoryNode)
                    	dict.addCategory((CategoryNode)node, (CategoryNode)parent);    
                    // Last but not least, mark the drop a success.
                    dtde.dropComplete(true);
                    return;
                }
            }
            dtde.rejectDrop();
        } catch (DuplicateException de){
            System.err.println("DuplicateException, probably caused by" + 
                    " dragging onto our parent"); 
            dtde.rejectDrop();
        } catch (Exception e) {
            e.printStackTrace(); // Just for debugging, really 
            dtde.rejectDrop();
        }
    }

}