package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;

public class RemoveHighlightsAction extends YoshikoderAction {

    public RemoveHighlightsAction(Yoshikoder yk) {
        super(yk, RemoveHighlightsAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        yoshikoder.removeHighlights();
    }

}
