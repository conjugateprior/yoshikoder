package edu.harvard.wcfia.yoshikoder.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class FileUtil{

    private static Logger log = Logger.getLogger("edu.harvard.wcfia.yoshikoder.util.FileUtil");
    
	private static boolean isMac = System.getProperty("os.name").contains("OS X");
	
    private static boolean isWindows = 
        System.getProperty("os.name").startsWith("Windows");
    
	// necessary for pre java 1.5 code
	public static String systemEncoding = 
		(new OutputStreamWriter(new ByteArrayOutputStream())).getEncoding();
	/*
	static {
		// ensure this is the _canonical_ version of the name
	    // just override to be UTF-8
	    //Charset cs = Charset.forName(systemEncoding);
	    Charset cs = Charset.forName("UTF8");
		systemEncoding = cs.displayName();
	} */
	
	private static Locale[] availableLocales;
	
	private static List<LocaleWrapper> localeList; // sorted list
	private static List<CharsetWrapper> charsetList; // sorted list
	
	private FileUtil(){}

	public static boolean isMac(){
	    return isMac;
	}
	
    public static boolean isWindows(){
        return isWindows;
    }
    
    public static List<LocaleWrapper> getLocaleList(){
    	if (localeList == null){
    		Locale[] locs = Locale.getAvailableLocales();
    		localeList = new ArrayList<LocaleWrapper>(locs.length);
    		for (Locale l: locs) {
				LocaleWrapper wrapper = new LocaleWrapper(l);
				localeList.add(wrapper);
			}
    	}
    	Collections.sort(localeList);
    	return localeList;
    }
    
    public static List<CharsetWrapper> getCharsetList(){
    	if (charsetList == null){
    		SortedMap<String, Charset> map = Charset.availableCharsets();
    		charsetList = new ArrayList<CharsetWrapper>(map.size());
    		for (Charset cs : map.values()) {
				CharsetWrapper wrapper = new CharsetWrapper(cs);
				charsetList.add(wrapper);
			}
    	}
    	return charsetList;
    }
    
    // double up double quotes and surround with double quotes if space is involved
    public static String escapeForCsv(String s){
    	String ss = s; 
    	if (ss.contains("\""))
    		ss = s.replace("\"", "\"\"");
    	if (ss.contains(","))
    		ss = "\"" + s + "\"";
    	return ss;
    }
    
	public static Locale[] getAvailableLocales(){
		if (availableLocales == null)
			availableLocales = Locale.getAvailableLocales();
		return availableLocales;
	}
	
	// parse a toString-ed locale into a Locale or null if unparsable
	public static Locale parseLocale(String localeToString){
		String[] bits = localeToString.split("_");
		int bl = bits.length;
		if (bl == 1)
			return new Locale(bits[0]); // language
		else if (bl == 2)
			return new Locale(bits[0], bits[1]); // language country
		else if (bl == 3)
			return new Locale(bits[0], bits[1], bits[2]); // lang country var
		else {
			System.err.println("Could not parse locale");
			return null;
		}
	}
	
	public static String slurp(File f) throws IOException{
		return slurp(f, systemEncoding);
	}
	
	public static String slurp(File f, String csname) 
		throws IOException{
		
		String str = new String(getBytes(f), csname);
		return str;
	}
	
	public static void save(File f, String content, String csname) 
		throws IOException{

		FileOutputStream fos = new FileOutputStream(f);
		Writer out = new OutputStreamWriter(fos, csname);
		out.write(content);
		out.close();
	}

	/*
	public static String[] getEncodingNames(){
		if (encodingNames == null){
			// this may be expensive
			SortedMap m = Charset.availableCharsets();
			List arr = new ArrayList(m.keySet());
			encodingNames = (String[])arr.toArray(new String[arr.size()]);
		}
		return encodingNames;
	}
	*/
	
	public static Icon getIcon(String name){
	   Icon ic = null;
	   try {
	       ClassLoader cl = FileUtil.class.getClassLoader();
	       ic = new ImageIcon(cl.getResource(name));
	       
	       return ic;
	   } catch (Exception e){
	       try {
	           ic = new ImageIcon("resources/" + name);
	           return ic;
	       } catch (Exception ex){
	           ex.printStackTrace();
	           return null;
	       }
	   }
	}
	
	public static void save(File f, String content) throws IOException{
		save(f, content, systemEncoding);
	}
	
    public static String suffix(String filename, String suffix){
        if (filename.toLowerCase().endsWith("." + suffix))
            return filename;
        else
            return filename + "." + suffix;
    }

    public static String suffix(String filename, String preferredSuffix, String nonPreferredSuffix){
        if (filename.toLowerCase().endsWith("." + preferredSuffix) ||
                filename.toLowerCase().endsWith("." + nonPreferredSuffix))
            return filename;
        else
            return filename + "." + preferredSuffix;
    }
    
	public static File suffix(File f, String preferedSuffix, 
			String nonPreferedSuffix){
		if (f == null)
			return null;
		
		String name = f.getName().toLowerCase();
		if (name.endsWith("." + preferedSuffix.toLowerCase()) ||  
			name.endsWith("." + nonPreferedSuffix.toLowerCase())){ 
			return f; // it will do
		} else {
			return suffix(f, preferedSuffix);
		}
	}
	
	public static File suffix(File f, String suffix){
		if (f==null)
			return null;
		
		if (f.getName().toLowerCase().endsWith("." + suffix.toLowerCase())) 
			return f; // it will do
		else
			return new File(f.getParent(), f.getName() + "." + suffix); 
	}
	
	public static void copyFile(File from, File to) throws IOException{
		FileInputStream fin = new FileInputStream(from);
		FileOutputStream fout = new FileOutputStream(to);
		int b;
		while ( (b = fin.read()) != -1 ){
			fout.write(b);
		}
		fin.close();
		fout.close();
	}
	
    /**
     * Attempts to get the smaller of a specified number of bytes
     * and all the bytes in the file. 
     * @param f
     * @param howmany ideal number of bytes returned
     * @return array of bytes
     * @throws IOException
     */
    public static byte[] getBytes(File f, int howmany) throws IOException {
        FileChannel in = null;
        try {
            in = new FileInputStream(f).getChannel();
            
            long size = Math.min(in.size(), howmany);
            if (size > Integer.MAX_VALUE) {
                throw new IOException("File : " + f
                        + " is too large for processing");
            }
            MappedByteBuffer buf = 
                in.map(FileChannel.MapMode.READ_ONLY, 0, size);
            byte[] bytes = new byte[(int) size];
            
            buf.get(bytes);
            return bytes;
        } finally {
            if (in != null) {
                in.close();
            }
        }        
    }
    
	public static byte[] getBytes(File f) throws IOException {
		FileChannel in = null;
		try {
			in = new FileInputStream(f).getChannel();
			
			long size = in.size();
			if (size > Integer.MAX_VALUE) {
				throw new IOException("File : " + f
						+ " is too large for processing");
			}
			MappedByteBuffer buf = 
				in.map(FileChannel.MapMode.READ_ONLY, 0, size);
			byte[] bytes = new byte[(int) size];
			
			buf.get(bytes);
			return bytes;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    
	public static void copyInputStream(InputStream in, OutputStream out)
	    throws IOException {
	    
        byte[] buffer = new byte[1024];
	    int len;
	    while((len = in.read(buffer)) >= 0)
	        out.write(buffer, 0, len);
	    in.close();
	    out.close();
	}

	// unzip a file in the filesystem
	public static void unzip(File zipfile){
	    try {
	        ZipFile zipFile = new ZipFile(zipfile);
	        Enumeration entries = zipFile.entries();
	        
	        while (entries.hasMoreElements()) {
	            ZipEntry entry = (ZipEntry)entries.nextElement();
                File f = new File(zipfile.getParent(), entry.getName());
                
	            if (entry.isDirectory()) {
	                // Assume directories are stored parents first then children.
	                log.info("Extracting directory: " + entry.getName());
	                boolean madeDirectory = f.mkdir();
                    if (!madeDirectory)
                        throw new IOException("Could not create directory " + 
                                f.toString() + " from zipfile " + zipfile.toString() );
                    else 
                        continue;
	            }
	            log.info("Extracting file: " + entry.getName());
	            copyInputStream(zipFile.getInputStream(entry),
	                    new BufferedOutputStream(new FileOutputStream(f)));
	        }
	        zipFile.close();
	        
	    } catch (IOException ioe) {
	        log.log(Level.WARNING, "Failed to unzip zipfile " + zipfile.toString(), ioe);
	        return;
        }
	}

    public static void main(String[] args) {
        File f = new File("/tmp/onlinehelp.zip");
        FileUtil.unzip(f);
    }
    
}
