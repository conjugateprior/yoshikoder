package edu.harvard.wcfia.yoshikoder;

import java.io.File;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

import edu.harvard.wcfia.yoshikoder.util.ImportUtil;

public class YoshikoderOSX extends Yoshikoder {

	protected JMenu makeFileMenu(){
        JMenu menu = super.makeFileMenu();
        int howmany = menu.getMenuComponentCount();
        menu.remove(howmany-1); // the exit action
        menu.remove(howmany-2); // the separator 
        return menu;
	}

	protected JMenu makeHelpMenu(){
        JMenu menu = super.makeHelpMenu();
        int howmany = menu.getMenuComponentCount();
        menu.remove(howmany-1); // the about action
        menu.remove(howmany-2); // the separator 
        
    	helpAction = new MacHelpAction(this);
        ((JMenuItem)menu.getMenuComponent(0)).setAction(helpAction); // replace the help
        return menu;
	}
	
	protected void platformSpecificSetup(){
		
        finishAction = new QuitAction(this);
        aboutAction = new MacAboutAction(this);
        
        Application app = Application.getApplication();
		app.setAboutHandler(new AboutHandler() {
			@Override
			public void handleAbout(AboutEvent arg0) {
				aboutAction.actionPerformed(null);
			}
		});
        app.setQuitHandler(new QuitHandler() {
			public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1) {
				finishAction.actionPerformed(null);
			}
		});
        app.setOpenFileHandler(new OpenFilesHandler() {
			@Override
			public void openFiles(OpenFilesEvent arg0) {
				List<File> ff = arg0.getFiles();
				File first = ff.get(0);
				String nm = first.getName().toLowerCase();
				if (nm.endsWith(".ykd")){
					try {
						YKProject proj = ImportUtil.importYKProject(first);
						setProject(proj);
					} catch (Exception ex){
						JOptionPane.showConfirmDialog(YoshikoderOSX.this, "Could not open " + first.getName(),
								"Could not open project", JOptionPane.INFORMATION_MESSAGE);
					}
				} 
				
				// the semantics of this are not clear - dump the possibly unsaved project in place?
				/* else if (nm.endsWith(".ykd")){
					YKProject proj;
					try {
						proj = new YKProject();
						YKDictionary dict = ImportUtil.importYKDictionary(first);
						proj.setDictionary(dict);
						
						setProject(proj);
					} catch (Exception ex){
						JOptionPane.showConfirmDialog(Yoshikoder.this, "Could not open Yoshikoder dictionary file "  +first.getName(),
								"Could not open dictionary", JOptionPane.INFORMATION_MESSAGE);
					}	
				}
				*/
			}
		});
    }
	
	public YoshikoderOSX() {
		super();
	}
}
