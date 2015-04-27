package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author will
 */
public abstract class CommitablePanel extends FormPanel implements Commitable{
    
    protected boolean commit = false;
    
    protected KeyListener keyListener = new KeyListener() {
        public void keyTyped(KeyEvent e) {}
        public void keyReleased(KeyEvent e) {}
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                setCommit(true);
            }
        }
    };
    
    public CommitablePanel(){
        super();
    }
    
    public boolean getCommit(){
        return commit;
    }
    
    public void setCommit(boolean b){
        boolean oldcommit = commit;
        commit = b;
        firePropertyChange("commit", oldcommit, commit);
    }
    
    abstract public void commit() throws CommitException;
}
