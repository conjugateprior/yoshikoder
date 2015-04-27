package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.util.FileUtil;

public class TokenizerSource {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizerSource");
    
    public static final String propertiesFilename = "tokenizer.properties";
    
    public static final String UNLOADED_TOKENIZER = "Unloaded Tokenizer";
    
    protected Map localeToTM;
    protected Map tMToTokenizer;
    
    protected File pluginsDirectory;
    
    public TokenizerSource(File pluginsDir){
        log.info("In TokenizerSource contructor");
        pluginsDirectory = pluginsDir;

        if (!pluginsDirectory.exists()) 
            pluginsDirectory.mkdirs();
    
        tMToTokenizer = new HashMap();
        localeToTM = new HashMap();

        log.info("initializing with the contents of " + pluginsDir);
        File[] contents = pluginsDir.listFiles(); // fill up the maps
        for (int ii=0; ii<contents.length; ii++){
            try {
                log.info("Getting metadata for existing plugin: " + contents[ii]);
                TM tm = getPluginMetadata(contents[ii]);
                tm.location = contents[ii];
                tMToTokenizer.put(tm, UNLOADED_TOKENIZER);
                for (int jj=0; jj<tm.supportedLocales.length; jj++)
                    localeToTM.put(tm.supportedLocales[jj], tm);
            } catch (PluginException pe){
                log.log(Level.WARNING, "Error importing existing plugin metadata", pe);
            }
        }
    }
    
    public Set getAvailableTokenizerPlugins(){
        log.info("Returning the keySet from TM->Tokenizer map");
        return tMToTokenizer.keySet();
    }

    public Tokenizer getTokenizerPlugin(Locale loc) throws PluginException {
        TM tm = (TM)localeToTM.get(loc);
        if (tm == null) 
            return null;
        
        Object tok = tMToTokenizer.get(tm);
        if (!tok.equals(TokenizerSource.UNLOADED_TOKENIZER))
            return (Tokenizer)tok;
        else {
            Tokenizer t = loadTokenizer(tm);
            return t;
        }
        
    }
    
    public void removeTokenizerPlugin(TM tm) {
        log.info("Deleting the tokenizer");
        tMToTokenizer.remove(tm);
        for (int ii=0; ii<tm.supportedLocales.length; ii++)
            localeToTM.remove(tm.supportedLocales[ii]);
        boolean del = tm.location.delete();
        log.info("Did we successfully delete from " + tm.location + "? " + del);
    }
    
    /**
     * Removes the existing plugin, then adds the plugin in jarFile.
     * @param existingPlugin
     * @param jarFile
     * @return tokenizer plugin metadata
     * @throws PluginException
     */
    public TM replaceTokenizerPlugin(TM existingPlugin, File jarFile) throws PluginException {
        removeTokenizerPlugin(existingPlugin);
        addTokenizerPlugin(jarFile);
        return existingPlugin;
    }

    protected TM getPluginMetadata(File jarFile) throws PluginException {
        log.info("Examining "+ jarFile.getAbsolutePath());
        Properties props = null;
        try {
            JarFile jf = new JarFile(jarFile);
            JarEntry e = jf.getJarEntry(propertiesFilename);
            InputStream is = jf.getInputStream(e);
            props = new Properties();
            props.load(is);
        } catch (IOException ioe){
            throw new PluginException("Couldn't read the jar file properties", ioe);
        }
        
        String cname = props.getProperty("classname");
        String name = props.getProperty("name");
        String description = props.getProperty("description", "");
        String locales = props.getProperty("locales");
        if (cname==null || name==null || locales==null) 
            throw new PluginException("Missing property in " + propertiesFilename);
        
        String[] line = locales.split("[ ]+");
        Locale[] locs = new Locale[line.length];
        for (int ii=0; ii<locs.length; ii++)
            locs[ii] = FileUtil.parseLocale(line[ii]);
        
        TM tm = new TM(name, description, null, cname, locs); // equals ignores location 
        return tm;
    }
    
    public TM addTokenizerPlugin(File jarFile) throws PluginException{
        log.info("Adding a tokenizer from jar file: " + jarFile);
        
        TM tm = getPluginMetadata(jarFile);
        
        boolean contained = tMToTokenizer.containsKey(tm);
        log.info("Checking whether we have this tokenizer already");
        if (contained) 
            throw new DuplicatePluginException("Tokenizer already exists"); 
        File unique = makeUniqueName(jarFile);
        log.info("Made unique name: " + unique);
        
        log.info("Assigning unique name in metadata");
        tm.location = unique;
        
        log.info("Copying in the filesystem");
        try {
            FileUtil.copyInputStream(
                    new FileInputStream(jarFile), 
                    new FileOutputStream(tm.location));
        } catch (IOException ioe){
            throw new PluginException("Could not transfer plugin to the plugin directory", ioe);
        }
        log.info("Inserting tokenizer metadata into TM->Tokenizer map");
        tMToTokenizer.put(tm, UNLOADED_TOKENIZER);
        log.info("Inserting tokenizer metadata into Locale->TM map");
        for (int ii=0; ii<tm.supportedLocales.length; ii++)
            localeToTM.put(tm.supportedLocales[ii], tm);
        
        return tm;
        
    }
    
    protected boolean clashes(File f, File directory){
        File[] files = directory.listFiles();
        for (int ii=0; ii<files.length; ii++){
            if (files[ii].getName().equals(f.getName()))
                return true;
        }
        return false;
    }
    
    protected File makeUniqueName(File jarFile){
        File newname = new File(pluginsDirectory, jarFile.getName());
        int ii=1;
        while (clashes(newname, pluginsDirectory)){
            newname = new File(pluginsDirectory, ii + "-" + jarFile.getName());
            ii++;
        }
        return newname;
    }
    
    protected Tokenizer loadTokenizer(TM tm) throws PluginException {
        try {
            URL url = new URL("file://" + tm.location.getAbsolutePath());
            URLClassLoader ucl = new URLClassLoader(new URL[]{url});
            Class klass = ucl.loadClass(tm.classname);
            Object obj = klass.newInstance();
            Tokenizer tokenizer = (Tokenizer)obj;
            tMToTokenizer.put(tm, tokenizer);
            
            return tokenizer;
            
        } catch (MalformedURLException ex){
            throw new PluginException(ex);
        } catch (ClassNotFoundException cnf){
            throw new PluginException(cnf);  
        } catch (InstantiationException ie){
            throw new PluginException(ie);
        } catch (IllegalAccessException iae){
            throw new PluginException(iae);
        }
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("Plugins directory " + pluginsDirectory.getAbsolutePath() + "\n");
        sb.append("Locale -> Metadata: " + localeToTM + "\n");
        sb.append("Metadata -> Tokenizer: " + tMToTokenizer);
        return sb.toString();
    }
    
}
