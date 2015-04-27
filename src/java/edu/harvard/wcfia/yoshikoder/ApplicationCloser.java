package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class ApplicationCloser {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.ApplicationCloser");
    
    protected Yoshikoder yoshikoder;
    protected FileDialog projectSaver;
    protected TaskWorker workerv;
    //protected ProjectSaver psaver;
    
    public ApplicationCloser(Yoshikoder yk){
        yoshikoder = yk;
        //psaver = new ProjectSaver(yoshikoder);
    }
    
    protected boolean shouldSave(){
        int val = DialogUtil.askYesNo(yoshikoder, 
                Messages.getString("Yoshikoder.saveProjectMessage"),
                Messages.getString("Yoshikoder.saveProjectTitle"));
        
        return (val==JOptionPane.YES_OPTION);
    }
    
    protected void savePreferences() throws Exception {

    	Preferences preferences = 
    			Preferences.userNodeForPackage(Yoshikoder.class);
    	log.info("got preferences object");
    	preferences.putInt("framesize.x", yoshikoder.getSize().width);
    	preferences.putInt("framesize.y", yoshikoder.getSize().height);
    	log.info("put in framesizes");
    	preferences.put("default.charset", yoshikoder.getDefaultEncoding().name());
    	log.info("put in default.charset");
    	preferences.put("default.locale", yoshikoder.getDefaultLocale().toString());
    	log.info("put in default.locale");
    	int winsize = yoshikoder.getWindowSize();
    	log.info("windowsize from yoshikoder:" +  winsize);
    	preferences.putInt("default.windowsize", winsize);
    	log.info("put in windowsize");

    	YKProject project = yoshikoder.getProject();
    	log.info("project.getName() = " + project.getName());
    	if (project.getLocation() != null){
    		log.info("current project location: " + project.getLocation().toString());
    		preferences.put("last.project", project.getLocation().getAbsolutePath());
    	} else {
    		log.info("current project has no location defined");                    
    	}
    }
    	
    
    public void closeApplication() {

    	if (yoshikoder.hasUnsavedChanges()){
    		int val = JOptionPane.showConfirmDialog(yoshikoder, "The project has unsaved changes.  Would you like to save them?", 
    				"Save Project", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    		if (val == JOptionPane.YES_OPTION){
    			try {
    				yoshikoder.saveProject(false);
    			} catch (IOException ex){
    				DialogUtil.yelp(yoshikoder, "Could not save this project: " + ex.getMessage(), "Error saving project");
    			}
    		} else if (val == JOptionPane.NO_OPTION){
    			//
    		} else {
    			// cancel of some kind
    			return;
    		}
    	}
    	
    	log.info("saving preferences");
    	try {
    		savePreferences();
    	} catch (Exception ex){
    		ex.printStackTrace();
        }
        log.info("done saving preferences");
      
        System.exit(0);
    }
    
}
