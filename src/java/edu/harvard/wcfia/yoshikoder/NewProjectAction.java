package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import edu.harvard.wcfia.yoshikoder.ui.dialog.NewProjectDialog;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;

public class NewProjectAction extends YoshikoderAction {
    
    //protected ProjectSaver psaver;
    
    public NewProjectAction(Yoshikoder yk) {
        super(yk, NewProjectAction.class.getName());
        //psaver = new ProjectSaver(yoshikoder);
    }

    public void actionPerformed(ActionEvent e) {
    	if (yoshikoder.hasUnsavedChanges()){
    		int resp = DialogUtil.askYesNo(yoshikoder, "Save the current project before starting a new one?", "Save project?");
    		if (resp == JOptionPane.YES_OPTION){
    			try {
    				yoshikoder.saveProject(false);
    			} catch (IOException ex){
    				DialogUtil.yelp(yoshikoder, "Could not save this project", "Error saving project");
    			}
    		}
        }
        //psaver.saveProject();
        
        JDialog dia = new NewProjectDialog(yoshikoder);
        dia.setVisible(true);
    }

}
