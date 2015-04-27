package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;

/**
 * Slightly augmented drag and drop code from O'Reilly's Swing book.
 */
public class TreeDragSource implements DragSourceListener, DragGestureListener {

    DragSource source;

    DragGestureRecognizer recognizer;

    TransferableTreeNode transferable;

    Node oldNode;

    JTree sourceTree;

    public TreeDragSource(JTree tree, int actions) {
        sourceTree = tree;
        source = new DragSource();
        recognizer = source.createDefaultDragGestureRecognizer(sourceTree,
                actions, this);
    }

    // Drag gesture handler
    public void dragGestureRecognized(DragGestureEvent dge) {
        TreePath path = sourceTree.getSelectionPath();
        if ((path == null) || (path.getPathCount() <= 1)) {
            // We can't really move the root node (or an empty selection).
            return;
        }
        // Remember which node was dragged off so we can delete it to complete a
        // move
        // operation.
        oldNode = (Node)path.getLastPathComponent();

        // Make a version of the node that we can use in the DnD system.
        transferable = new TransferableTreeNode(path);

        // And start the drag process. We start with a no-drop cursor, assuming
        // that the
        // user won't want to drop the item right where she picked it up.
        source.startDrag(dge, DragSource.DefaultMoveNoDrop, transferable, this);

        // If you support dropping the node anywhere, you should probably start
        // with a
        // valid move cursor:
        //     source.startDrag(dge, DragSource.DefaultMoveDrop, transferable,
        // this);
    }

    // Drag event handlers
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragExit(DragSourceEvent dse) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        if (dsde.getDropSuccess()) {
            YKDictionary dict = (YKDictionary)sourceTree.getModel();
            dict.remove(oldNode);
            
            // Remove the node only if the drop was successful.
            /*             
             ((DefaultTreeModel)sourceTree.getModel())
                    .removeNodeFromParent(oldNode);
             */
        }
    }
}