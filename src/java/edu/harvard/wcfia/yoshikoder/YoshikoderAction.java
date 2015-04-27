package edu.harvard.wcfia.yoshikoder;

import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.DialogWorker;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

abstract public class YoshikoderAction extends AbstractAction {
    
    protected Yoshikoder yoshikoder;
    
    protected DialogWorker dworker;
    
    protected TaskWorker tworker;
    
    public YoshikoderAction(Yoshikoder yk, String subclassFullPath) {
        super();
        yoshikoder = yk;
        String[] path = subclassFullPath.split("\\.");
        String prefix = path[path.length-1];
        
        // Name
        putValue(Action.NAME, Messages.getString(prefix + ".name"));
        
        // Tooltip
        putValue(Action.SHORT_DESCRIPTION, Messages.getString(prefix + ".desc"));
        
        // Mnenomic and menu icons
        if (!FileUtil.isMac()){
            String iconName = Messages.getString(prefix + ".iconName");
            putValue(Action.SMALL_ICON, DialogUtil.getDialogIcon(iconName));

            String mnem = Messages.getString(prefix + ".mnem");
            if (!mnem.startsWith("!"))
                putValue(Action.MNEMONIC_KEY, new Integer(mnem.charAt(0)));
        }
        
        // Accelerator
        String accel = Messages.getString(prefix + ".accel");
        if (accel.startsWith("!")) 
            return;
        else if (accel.startsWith("shift")){
            putValue(Action.ACCELERATOR_KEY, 
                    KeyStroke.getKeyStroke(
                            KeyStroke.getKeyStroke(accel).getKeyCode(), 
                            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() +
                            Event.SHIFT_MASK
                    )
            );                  
        } else {
            putValue(Action.ACCELERATOR_KEY, 
                    KeyStroke.getKeyStroke(
                            KeyStroke.getKeyStroke(accel).getKeyCode(), 
                            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                    )
            ); 
        }   
    }

    // filled in by subclasses
    abstract public void actionPerformed(ActionEvent e);

}
