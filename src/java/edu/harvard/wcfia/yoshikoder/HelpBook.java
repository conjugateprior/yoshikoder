package edu.harvard.wcfia.yoshikoder;

/** 
 * Java-JNI bridge for involking Apple Help Viewer, 
 * taken from the JarBundler Ant Task example project,
 * courtesy of Will Gilbert.
 *  
 * @author Will Lowe
 */
public class HelpBook {
    static {
        System.loadLibrary("HelpBookJNI");
    }
    public static native void launchHelpViewer();
}
