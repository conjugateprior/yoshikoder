package edu.harvard.wcfia.yoshikoder.util;

import java.awt.Component;

import javax.swing.JDialog;

/**
 * @author will
 */
public abstract class DialogWorker extends SwingWorkerVariant {

    protected JDialog dia;
    protected Exception e;
    
    public DialogWorker(Component aComponent) {
        super(aComponent);
    }

    protected void doNonUILogic() throws RuntimeException {
        try {
            doWork();
        } catch (Exception ex){
            e = ex;
        }
    }

    protected void doUIUpdateLogic() throws RuntimeException {
        if (e == null)
            dia.setVisible(true);
        else {
            onError();
        }
    }
    
    abstract protected void doWork() throws Exception;
    
    abstract protected void onError();

}
