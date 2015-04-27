package edu.harvard.wcfia.yoshikoder.ui.dialog;

import java.awt.Dialog;
import java.awt.Frame;

/**
 * Non-modal scrolling results stream.  Results are appended to the text area
 * and may be cleared.  The representation is intended for users to cut and paste 
 * out of.
 * @author will
 *
 */
public class DocumentScoreDialog extends TextResultsDialog {
    
    public DocumentScoreDialog(Frame f){
        super(f, "Document Scoring Results");
        init();
        setLocationRelativeTo(f);
    }
    
    public DocumentScoreDialog(Dialog dia){
        super(dia, "Document Scoring Results");
        init();
        setLocationRelativeTo(dia);
    }
    
    
}
