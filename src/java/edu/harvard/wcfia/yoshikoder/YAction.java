package edu.harvard.wcfia.yoshikoder;

import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class YAction extends AbstractAction{
    
    protected Method method;
    protected Object app;
    
    public YAction(String key, Object obj, Method meth){
        super();
        app = obj;
        method = meth;
        init(key);
    }
    
    /**
     * Generate the name, description, mnemonic, and accelerator
     * values for this action from a resource bundle.  
     * <p>
     * Prefix is 
     * used as a string base to retrieve from the bundle.  The 
     * resource bundle keys are:
     * <ul>
     * <li>prefix.name (name)
     * <li>prefix.desc (tooltip description)
     * <li>prefix.mnem (mnemonic key)
     * <li>prefix.accel (accelerator key)
     * </ul>
     * If the bundle returns a string beginning ! then the budle value
     * is ignored
     * 
     * @param prefix
     */
    protected void init(String prefix){
        String name = Messages.getString(prefix + ".name");
        if (name.startsWith("!"))
            name = "NAMELESS_ACTION";
        putValue(Action.NAME, name);
        
        if (!FileUtil.isMac()){ // no icons are ever in OSX menubars
            String iconName = Messages.getString(prefix + ".iconName");
            if (!iconName.startsWith("!")){
                Icon icon = DialogUtil.getDialogIcon(iconName);
                putValue(Action.SMALL_ICON, icon);
            }
        }
        
        String desc = Messages.getString(prefix + ".desc");
        if (!desc.startsWith("!"))
            putValue(Action.SHORT_DESCRIPTION, desc);
        
        // TODO check this char -> int will always work for non-ascii
        if (!FileUtil.isMac()){ // no mnemonics used on OSX menubars
            String mnem = Messages.getString(prefix + ".mnem");
            if (!mnem.startsWith("!"))
                putValue(Action.MNEMONIC_KEY, new Integer(mnem.charAt(0)));
        }
        
        String accel = Messages.getString(prefix + ".accel");
        if (!accel.startsWith("!")){ // i.e. no resource available
            if (accel.startsWith("shift")){
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
    }
    
    /**
     * Invoke the noargs method on the object (both passed in by the
     * constructor).
     */
    public void actionPerformed(ActionEvent e) {
        try {
            method.invoke(app, new Object[]{});
        } catch (IllegalAccessException ia){
            ia.printStackTrace();
        } catch (InvocationTargetException ite){
            ite.printStackTrace();
        }    		
    }
}
