package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * @author will
 */
abstract public class FormPanel extends CommitableJPanel{

    private GridBagConstraints c;
    private Insets topInsetsLHS = new Insets(0, 0, 0, 10);
    private Insets topInsetsRHS = new Insets(0, 0, 0, 10);
    private Insets insetsLHS = new Insets(5, 0, 0, 10);
    private Insets insetsRHS = new Insets(5, 0, 0, 0);
    private int gridY = 0;

    public FormPanel() {
        super(new GridBagLayout());
        c = new GridBagConstraints();
    }

    abstract public void commit() throws CommitException;
    
    public void addField(String label, JTextField field) {
        c.fill = GridBagConstraints.NONE;
        c.weighty = 0.0;
        c.weightx = 0.0;

        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = gridY;
        c.insets = (gridY > 0) ? insetsLHS : topInsetsLHS;
        add(new JLabel(label), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        c.anchor = GridBagConstraints.WEST;
        c.gridx = 1;
        c.gridy = gridY;
        c.insets = (gridY > 0) ? insetsRHS : topInsetsRHS;
        add(field, c);

        gridY++;
    }

    public void addWidgetInline(String label, JComponent comp) {
        c.fill = GridBagConstraints.NONE;
        c.weighty = 0.0;
        c.weightx = 0.0;

        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = gridY;
        c.insets = (gridY > 0) ? insetsLHS : topInsetsLHS;
        add(new JLabel(label), c);

        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.anchor = GridBagConstraints.WEST;
        c.gridx = 1;
        c.gridy = gridY;
        c.insets = (gridY > 0) ? insetsRHS : topInsetsRHS;
        add(comp, c);

        gridY++;
    }

    public void addWidgetInlineFixedWidth(String label, JComponent comp) {
        c.fill = GridBagConstraints.NONE;
        c.weighty = 0.0;
        c.weightx = 0.0;

        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = gridY;
        c.insets = (gridY > 0) ? insetsLHS : topInsetsLHS;
        add(new JLabel(label), c);

        c.weightx = 0.0;

        c.anchor = GridBagConstraints.WEST;
        c.gridx = 1;
        c.gridy = gridY;
        c.insets = (gridY > 0) ? insetsRHS : topInsetsRHS;
        add(comp, c);

        gridY++;
    }

    public void addWidgetFixedDepth(String label, JComponent comp) {
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;

        c.anchor = GridBagConstraints.NORTHEAST;
        c.gridx = 0;
        c.gridy = gridY;
        c.insets = (gridY > 0) ? insetsLHS : topInsetsLHS;
        add(new JLabel(label), c);

        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.BOTH;

        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 1;
        c.gridy = gridY;
        c.insets = (gridY > 0) ? insetsRHS : topInsetsRHS;
        add(comp, c);

        gridY++;
    }

    public void addLabel(String label, String value) {
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        c.weighty = 0.0;

        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = gridY;
        c.insets = (gridY > 0) ? insetsLHS : topInsetsLHS;
        add(new JLabel(label), c);

        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.anchor = GridBagConstraints.WEST;
        c.gridx = 1;
        c.gridy = gridY;
        c.insets = (gridY > 0) ? insetsRHS : topInsetsRHS;
        add(new JLabel(value), c);

        gridY++;
    }
}