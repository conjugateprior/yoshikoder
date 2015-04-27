package edu.harvard.wcfia.yoshikoder.util;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author will
 */
public abstract class TaskWorker extends SwingWorkerVariant {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.util.TaskWorker");
    
    protected Exception e;
    
    public TaskWorker(Component aComponent) {
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
        if (e != null){
            log.log(Level.WARNING, "Error thrown on worker thread", e);
            onError();
        } else
            onSuccess();
    }
    
    abstract protected void doWork() throws Exception;
    
    protected void onSuccess(){}
    
    abstract protected void onError();
}
