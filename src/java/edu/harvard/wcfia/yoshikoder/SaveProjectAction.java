package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.IOException;

import edu.harvard.wcfia.yoshikoder.util.DialogUtil;

public class SaveProjectAction extends YoshikoderAction {

    //protected FileDialog dia;
    //protected ProjectSaver psaver;
    
    public SaveProjectAction(Yoshikoder yk) {
        super(yk, SaveProjectAction.class.getName());
        //psaver = new ProjectSaver(yoshikoder);
    }
    
    public void actionPerformed(ActionEvent e) {
    	try {
    		yoshikoder.saveProject(false);
    	} catch (IOException ex){
    		DialogUtil.yelp(yoshikoder, "Could not save this project", "Error saving project");
    	}
    	/*
    	if (yoshikoder.getProject().getLocation()==null){
            boolean setLocation = psaver.setProjectLocation();
            if (!setLocation)
                return; // user cancelled operation
        }
        psaver.saveProject();
        */
    }

}
