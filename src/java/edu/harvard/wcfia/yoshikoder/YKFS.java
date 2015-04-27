package edu.harvard.wcfia.yoshikoder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import edu.harvard.wcfia.yoshikoder.util.ApplicationDetails;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;

public class YKFS {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.YKFS");
    
    private File dotYoshikoder;
    private File versionFile;
    private File pluginsDirectory;
    private File pluginTokenizersDirectory;
    private File onlinehelpDirectory;
    private File runningFile; //  a flag used to ensure we don't run two instances
    private File logFile;
    private String currentVersion = 
        ApplicationDetails.getString("Yoshikoder.application.version").trim();
    
    
    private static YKFS instance;
    
    public static YKFS getYKFS(){
        if (instance == null)
            instance = new YKFS();
        return instance;
    }
        
    /**
     * Removes all the local storage.  This is mostly for testing purposes.
     * @return whether cleanup succeeded
     */
    public boolean cleanUp(){
        return FileUtil.deleteDir(dotYoshikoder);
    }
    
    /**
     * Gets the top level help file - usually 
     * <code>~/.yoshikoder/onlinehelp/index.html</code>
     * @return index file for online help
     */
    public File getOnlineHelpIndex(){
        return new File(onlinehelpDirectory, "index.html");
    }
    
    public File getLogFile(){
        return logFile;
    }

    protected YKFS(){
    	try {
    		dotYoshikoder = new File(System.getProperty("user.home"), ".yoshikoder");
    		if (!dotYoshikoder.exists()){
    			boolean b = dotYoshikoder.mkdir();
    			if (!b)
    				throw new IOException("Could not create " + dotYoshikoder.toString());
    		}
    	} catch (IOException ex1){
    		log.warning(ex1.getMessage());
    		try {
    			dotYoshikoder = File.createTempFile("yoshikoder", ".tmp" );
    			log.info("creating dotYoshikoder in " + dotYoshikoder);
    		} catch (IOException ex2){
    			ex2.printStackTrace();
    		}
    	}
    	
    	versionFile = new File(dotYoshikoder, "version_number");
        pluginsDirectory = new File(dotYoshikoder, "plugins");
        pluginTokenizersDirectory = new File(pluginsDirectory, "tokenizers");
        onlinehelpDirectory = new File(dotYoshikoder, "onlinehelp");
        logFile = new File(dotYoshikoder, "logging");
        runningFile = new File(dotYoshikoder, "RUNNING");
        
        if (!isFileSystemPrepared())
            prepareFileSystem();        
        else {
            if (isOutOfDate())
                updateOnlineHelp();
        }
        
        // setup loggers
        try {
            FileHandler handler = 
                new FileHandler(logFile.getAbsolutePath().toString());
            handler.setFormatter(new SimpleFormatter());
            // Add to the desired logger (the parent of all others)
            Logger logger = Logger.getLogger("");
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected boolean isOutOfDate(){        
        try {
            BufferedReader reader = 
                new BufferedReader(new FileReader(versionFile));
            String fileVersion = reader.readLine().trim();
            reader.close();
            
            return currentVersion.compareTo(fileVersion) > 0;                
            
        } catch (Exception ex){
            log.log(Level.WARNING, "Error comparing version numbers", ex);
            return true;
        }
    }

    protected boolean isFileSystemPrepared(){
        return (dotYoshikoder.exists() &&
                versionFile.exists() &&
                pluginsDirectory.exists() &&
                pluginTokenizersDirectory.exists() &&
                onlinehelpDirectory.exists());
    }
    
    protected boolean updateOnlineHelp(){
        try {
            FileUtil.deleteDir(onlinehelpDirectory);
            ClassLoader cl = YKFS.class.getClassLoader();
            InputStream str = cl.getResourceAsStream("onlinehelp.zip");
            File helpzip = new File(dotYoshikoder, "onlinehelp.zip");
            FileOutputStream fout = new FileOutputStream(helpzip);
            
            log.info("Copying onlinehelp from resources into filesystem");
            FileUtil.copyInputStream(str, fout);
            log.info("Unzipping online help in filesystem");
            FileUtil.unzip(helpzip);
            log.info("Deleting online help zip file");
            helpzip.delete();
            
            return true;
        
        } catch (Exception ex){
            log.log(Level.WARNING, "Failed to update online help", ex);
            return false;
        }
    }
    
    public boolean getRunningYoshikoderFlag(){
        return runningFile.exists();
    }
    
    protected boolean prepareFileSystem(){
        try {
            dotYoshikoder.mkdir();
            pluginsDirectory.mkdir();
            pluginTokenizersDirectory.mkdir();
            onlinehelpDirectory.mkdir();
            versionFile.createNewFile();
            BufferedWriter writer = 
                new BufferedWriter(new FileWriter(versionFile));
            writer.write(currentVersion);
            writer.close();   
            updateOnlineHelp();
            
            return true;
        } catch (Exception ex){
            log.log(Level.WARNING, "Failed to prepare filesystem", ex);
            return false;
        }
    }
}
