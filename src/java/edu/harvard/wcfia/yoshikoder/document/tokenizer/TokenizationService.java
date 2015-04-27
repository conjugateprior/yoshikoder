package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.harvard.wcfia.yoshikoder.document.YKDocument;

/**
 * An object that tokenizes documents on the basis of their locale.
 * @author will
 *
 */
public class TokenizationService {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationService");

    private static TokenizationService ts;
    
    protected File pluginsDir;
    protected TokenizerSource tokenizerSource;
    protected Set tokenizerMetadata;
    
    private TokenizationService(){
        Properties props = new Properties();
        try {
            InputStream str = 
                TokenizationService.class.getClassLoader().getResourceAsStream("plugins.properties");
            props.load(str);
        } catch (IOException ioe){
            log.log(Level.WARNING, 
                    "Couldn't get tokenizer plugins directory from plugins.properties", 
                    ioe);
        }
        String dir = 
            props.getProperty("tokenizer.plugins.dir", 
                    ".yoshikoder/plugins/tokenizers");
        pluginsDir = new File(System.getProperty("user.home"), dir);
        
        log.info("Making a new TokenizerSource with pluginDir: " + pluginsDir);
        tokenizerSource = new TokenizerSource(pluginsDir);
        log.info("Getting available tokenizer plugins from tokenizer source");
        Set md = tokenizerSource.getAvailableTokenizerPlugins();
        log.info("Initializing tokenizerMetadata with the sources available plugins");
        tokenizerMetadata = new HashSet(md);
    }
    
    public static TokenizationService getTokenizationService(){
        if (ts == null)
            ts = new TokenizationService();
        return ts;
    }
    
    protected Tokenizer getTokenizer(Locale loc) {
        try {
            Tokenizer tok = 
                tokenizerSource.getTokenizerPlugin(loc);
            if (tok != null)
                return tok;
            
        } catch (PluginException ple){
            log.log(Level.WARNING, 
                    "Failed to load existing plugin tokenizer.  " + 
                    "Falling back to BITokenizer", ple);
        }
        return new BITokenizerImpl(loc);
    }
    
    public TokenList tokenize(YKDocument doc) throws TokenizationException, IOException {
        Locale loc = doc.getLocale();
        if (loc == null)
            loc = Locale.getDefault();
        
        Tokenizer tok = getTokenizer(loc);
        String text = doc.getText();
        TokenList tl = tok.getTokens(text);   
        return tl;
    }

    public TM addTokenizerPlugin(File f) throws PluginException {
        log.info("Calling addTokenizerPlugin in TokenizationService");
        TM metadatum = tokenizerSource.addTokenizerPlugin(f);
        log.info("Adding the metadata handed back to the tokenizerMetadata");
        tokenizerMetadata.add(metadatum);
        log.info("returning the metadata");
        return metadatum;
    }
    
    public TM replaceTokenizerPlugin(TM existingPlugin, File newPlugin) throws PluginException{
        TM latest = 
            tokenizerSource.replaceTokenizerPlugin(existingPlugin, newPlugin);
        tokenizerMetadata.remove(existingPlugin);
        tokenizerMetadata.add(latest); // should be the same
        return latest;
    }
    
    public void removeTokenizerPlugin(TM metadatum){
        tokenizerSource.removeTokenizerPlugin(metadatum);
        tokenizerMetadata.remove(metadatum);
    }
    
    public Set getTokenizerPluginMetadata(){
        log.info("returning the plugin metadata from TokenizationService");
        return tokenizerMetadata;
    }
    
    public static void main(String[] args) throws Exception {
        // clear the plugins dir first...
        File f = new File(System.getProperty("user.home"), ".yoshikoder/plugins/tokenizers");
        File[] contents = f.listFiles();
        for (int ii=0; ii<contents.length; ii++){
            contents[ii].delete();
        }
        
        System.out.println("CASE 1: No duplicates, empty plugins directory");
        TokenizationService service = TokenizationService.getTokenizationService();
        TM tm = 
            service.addTokenizerPlugin(new File("/Users/will/java/chinese-tokenizers/SCTokenizer.jar"));
        System.out.println(tm);
        service.removeTokenizerPlugin(tm);

        contents = f.listFiles();
        for (int ii=0; ii<contents.length; ii++){
            System.out.println(contents[ii] + "should not exist...");
        }
        
        System.out.println("CASE 2: One duplicate, should throw a Duplicate Exception");
        tm = 
            service.addTokenizerPlugin(new File("/Users/will/java/chinese-tokenizers/SCTokenizer.jar"));
        try {
            service.addTokenizerPlugin(new File("/Users/will/java/chinese-tokenizers/SCTokenizer.jar"));
        } catch (DuplicatePluginException de){
            log.info("DUPLICATE EXCEPTION!");
        }
        System.out.println("CASE 3: One duplicate, replaced");
        TM tmdup = null;
        TM newdup = null;
        try {
            tmdup = 
                service.addTokenizerPlugin(new File("/Users/will/java/chinese-tokenizers/SCTokenizer.jar"));
        } catch (DuplicatePluginException dp){
            log.info("Replacing plugin");
            newdup = 
                service.replaceTokenizerPlugin(tm, new File("/Users/will/java/chinese-tokenizers/SCTokenizer.jar"));
        }
        service.removeTokenizerPlugin(newdup);
        
        contents = f.listFiles();
        for (int ii=0; ii<contents.length; ii++){
            System.out.println(contents[ii] + "should not exist...");
        }

        
        
    }
    
}
