package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

abstract public class CommitableJPanel extends JPanel implements Commitable {

    public CommitableJPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public CommitableJPanel(LayoutManager layout) {
        super(layout);
    }

    public CommitableJPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public CommitableJPanel() {
        super();
    }

    abstract public void commit() throws CommitException;
}
