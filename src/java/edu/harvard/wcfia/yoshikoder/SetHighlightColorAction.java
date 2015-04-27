package edu.harvard.wcfia.yoshikoder;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JColorChooser;

public class SetHighlightColorAction extends YoshikoderAction {

    protected Color currentColor = Color.yellow;
    
    public SetHighlightColorAction(Yoshikoder yk) {
        super(yk, SetHighlightColorAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        Color c = 
            JColorChooser.showDialog(yoshikoder, "Choose a highlight color", currentColor);
        if (c != null){
            yoshikoder.setHighlightColor(c);
            currentColor = c;
        } 
    }

}
