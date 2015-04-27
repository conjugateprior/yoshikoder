package edu.harvard.wcfia.yoshikoder.util;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * @author will
 */
public class DialogUtil {

	private static List fontFamilyNames;

	private static HashMap icons = new HashMap(); // icon cache
    
    private static Logger log = Logger.getLogger("edu.harvard.wcfia.util.DialogUtil");
    
    // filters for native open dialogs
    
    public static FilenameFilter jarFilenameFilter = new FilenameFilter(){
        public boolean accept(File dir, String name) { return name.toLowerCase().endsWith(".jar"); }
    };
   
    public static FilenameFilter ykpFilenameFilter = new FilenameFilter(){
        public boolean accept(File dir, String name) { return name.toLowerCase().endsWith(".ykp"); }
    };
    
    public static FilenameFilter ykdFilenameFilter = new FilenameFilter(){
        public boolean accept(File dir, String name) { return name.toLowerCase().endsWith(".ykd"); }
    };

    public static FilenameFilter ykcFilenameFilter = new FilenameFilter(){
        public boolean accept(File dir, String name) { return name.toLowerCase().endsWith(".ykc"); }
    };

    public static FilenameFilter txtFilenameFilter = new FilenameFilter(){
        public boolean accept(File dir, String name) { return name.toLowerCase().endsWith(".txt"); }
    };
    
    public static FilenameFilter xlsFilenameFilter = new FilenameFilter(){
        public boolean accept(File dir, String name) { return name.toLowerCase().endsWith(".xls"); }
    };
    
    public static FilenameFilter vbpFilenameFilter = new FilenameFilter(){
        public boolean accept(File dir, String name) { return name.toLowerCase().endsWith(".vbp"); }
    };

    public static FilenameFilter htmlFilenameFilter = new FilenameFilter(){
        public boolean accept(File dir, String name) { 
            String n = name.toLowerCase();
            return n.endsWith(".htm") || n.endsWith(".html");
        }
    };

    
    private static FileFilter htmlFilter = new FileFilter(){
        public boolean accept(File f) {
            return (f.isDirectory() || 
                    f.getName().toLowerCase().endsWith(".html") ||  //$NON-NLS-1$
                    f.getName().toLowerCase().endsWith(".htm")); //$NON-NLS-1$
        }
        public String getDescription() {
            return Messages.getString("FileFilterUtil.html"); //$NON-NLS-1$
        }
    };

    private static FileFilter xlsFilter = new FileFilter(){
        public boolean accept(File f) {
            return (f.isDirectory() || 
                    f.getName().toLowerCase().endsWith(".xls")); //$NON-NLS-1$
        }
        public String getDescription() {
            return Messages.getString("FileFilterUtil.xls"); //$NON-NLS-1$
        }
    };

    private static FileFilter ykpFilter = new FileFilter(){
        public boolean accept(File f) {
            return (f.isDirectory() || 
                    f.getName().toLowerCase().endsWith(".ykp")); //$NON-NLS-1$
        }
        public String getDescription() {
            return Messages.getString("FileFilterUtil.ykp"); //$NON-NLS-1$
        }
    };

    private static FileFilter ykcFilter = new FileFilter(){
        public boolean accept(File f) {
            return (f.isDirectory() || 
                    f.getName().toLowerCase().endsWith(".ykc")); //$NON-NLS-1$
        }
        public String getDescription() {
            return Messages.getString("FileFilterUtil.ykc"); //$NON-NLS-1$
        }
    };
    
    private static FileFilter txtFilter = new FileFilter(){
        public boolean accept(File f) {
            return (f.isDirectory() || 
                    f.getName().toLowerCase().endsWith(".txt")); //$NON-NLS-1$
        }
        public String getDescription() {
            return Messages.getString("FileFilterUtil.txt"); //$NON-NLS-1$
        }
    };
   
    private static FileFilter jarFilter = new FileFilter(){
        public boolean accept(File f) {
            return (f.isDirectory() || 
                    f.getName().toLowerCase().endsWith(".jar")); //$NON-NLS-1$
        }
        public String getDescription() {
            return Messages.getString("FileFilterUtil.jar"); //$NON-NLS-1$
        }
    };
        
    public static int askYesNo(Component parent, String message, String title){
    	return JOptionPane.showConfirmDialog(parent, message, title, 
    			JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
    			getDialogIcon("smallQuestionIcon.png"));
    }
    
    public static void yelp(Component parent, String message, String title, Exception e){
        log.log(Level.WARNING, message, e);
        yelp(parent, message, title);
    }

    
    public static void yelp(Component parent, String message, String title){
        JOptionPane.showMessageDialog(parent, message, title, 
                JOptionPane.PLAIN_MESSAGE, 
                getDialogIcon("smallYelpIcon.png")); 
    }
    
    public static void yelp(Component parent, String message, Exception e){
        yelp(parent, message, Messages.getString("error"), e); 
    }    
    
    public static Icon getDialogIcon(String name){
        //System.err.println("Icons: " + icons);
        Icon ic = (Icon)icons.get(name);
        if (ic != null){
            log.info("Found a cached version of the the icon " + name);
            return ic;
        } 
        Icon icon = FileUtil.getIcon(name);
        if (icon != null)
        	icons.put(name, icon);
        return icon;

    }
    
    public static JButton makeToolbarButton(Action a, String messageBundleIconName){
        String iconPath = Messages.getString(messageBundleIconName);
        if (iconPath == null){
            log.warning("No icon reference found at " + messageBundleIconName);   
            return new JButton(a);
        } 
        Icon ic = DialogUtil.getDialogIcon(iconPath.trim()); 
        JButton button = new JButton(a);
        button.setIcon(ic);
        button.setText(null);
        return button;

    }
    
	public static List getFontFamilyNames(){
		if (fontFamilyNames == null){
			GraphicsEnvironment ge = 
				GraphicsEnvironment.getLocalGraphicsEnvironment();
			String[] names = ge.getAvailableFontFamilyNames();
			fontFamilyNames = new ArrayList(names.length);
			for (int ii=0; ii<names.length; ii++)
				fontFamilyNames.add(names[ii]);	
		}
		return fontFamilyNames;
	}
	
	private static Font defaultFont = Font.decode("SansSerif");
	
	public static Font getDefaultFont(){
		return defaultFont;
	}
	
	public static String toString(Font f){
		String str = f.getFamily() + "-PLAIN-" + f.getSize();
		return str;
	}
	
    public static FileDialog makeFileDialog(Frame parent, String name, int type, FilenameFilter filter){
        if (filter == null)
            return new FileDialog(parent, name, type);
        
        FileDialog dia =  new FileDialog(parent, name, type);
        dia.setFilenameFilter(filter);
        return dia;
    }
        
    public static JMenu makeMenu(String prefix){
        JMenu menu = 
            new JMenu(Messages.getString(prefix + ".name")); 
        char mnem = Messages.getString(prefix + ".mnem").charAt(0);
        menu.setMnemonic(mnem);
        if (!FileUtil.isMac())
            menu.setIcon(DialogUtil.getDialogIcon(Messages.getString(prefix + ".iconName")));
        return menu;
    }
    
}
