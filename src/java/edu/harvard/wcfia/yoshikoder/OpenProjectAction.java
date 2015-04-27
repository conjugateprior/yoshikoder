package edu.harvard.wcfia.yoshikoder;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.ImportUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class OpenProjectAction extends YoshikoderAction {

    //protected ProjectSaver psaver;
    protected FileDialog projectChooser;
    
    public OpenProjectAction(Yoshikoder yk){
        super(yk, OpenProjectAction.class.getName());
        //psaver = new ProjectSaver(yoshikoder);
    }
        
    public void actionPerformed(ActionEvent e) {
    	if (yoshikoder.hasUnsavedChanges()){
    		int resp = DialogUtil.askYesNo(yoshikoder, "Save the current project before opening a new one?", "Save project?");
    		if (resp == JOptionPane.YES_OPTION){
    			try {
    				yoshikoder.saveProject(false);
    			} catch (IOException ex){
    				DialogUtil.yelp(yoshikoder, "Could not save this project", "Error saving project");
    			}
    		}
        }
    	
        if (projectChooser==null)
            projectChooser =  DialogUtil.makeFileDialog(yoshikoder, 
               "Open Project", FileDialog.LOAD, DialogUtil.ykpFilenameFilter); 
            
        projectChooser.setFile(null);
        projectChooser.setVisible(true);
        String file = projectChooser.getFile();
        if (file == null) return;
        
        final File f = new File(projectChooser.getDirectory(), file);
        
        tworker = new TaskWorker(yoshikoder){
            YKProject project;
            protected void doWork() throws Exception {
            	project = ImportUtil.importYKProject(f);
            }
            protected void onSuccess() {
                yoshikoder.setProject(project);
            }
            protected void onError() {   
                DialogUtil.yelp(yoshikoder, "Could not open project", e); 
            }
        };
        tworker.start();
    }
    
}
