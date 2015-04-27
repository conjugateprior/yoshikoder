package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.IOException;

import edu.harvard.wcfia.yoshikoder.util.DialogUtil;

public class SaveProjectAsAction extends YoshikoderAction {

    protected FileDialog dia;
    //protected ProjectSaver psaver;
    
    public SaveProjectAsAction(Yoshikoder yk) {
        super(yk, SaveProjectAsAction.class.getName());
        //psaver = new ProjectSaver(yoshikoder);
    }

    public void actionPerformed(ActionEvent e) {
    	try {
    		yoshikoder.saveProject(true);
    	} catch (IOException ex){
    		DialogUtil.yelp(yoshikoder, "Could not save this project: " + ex.getMessage(), "Error saving project");
    	}
    		/*
         * boolean setLocation = psaver.setProjectLocation();
        if (!setLocation)
            return; // user cancelled operation
        psaver.saveProject();
        */
    }

}
