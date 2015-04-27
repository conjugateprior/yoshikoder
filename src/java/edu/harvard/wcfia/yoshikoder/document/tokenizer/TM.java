package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

public class TM {
    
    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.document.tokenizer.TM");
    
    public String name;
    public String description; 
    public File location;      // relative to plugins directory
    public String classname;   // Tokenizer implementation to use
    public Locale[] supportedLocales;
    
    /**
     * Metadata concerning a plugin tokenizer.
     * 
     * @param n name
     * @param d description 
     * @param l file location on the client machine
     * @param c fully qualified name of the class implementing Tokenizer
     * @param locs array of locales that this plugin deals with
     */
    public TM(String n, String d, File l, String c, Locale[] locs){
        name = n;
        description = d;
        location = l;
        classname = c;
        supportedLocales = locs;
    }
    
    /**
     * Ignores the file field since this is not part of the metadata for
     * the purposes of determining whether we've got a tokenizer
     * like this already.  Like equals.
     */        
    public int hashCode(){
        int hh = name.hashCode() + 
                 description.hashCode() + 
                 classname.hashCode();
        for (int ii=0; ii<supportedLocales.length; ii++)
            hh += supportedLocales[ii].hashCode();
        return hh;
    }
    
    /**
     * Ignores the file field since this is not part of the metadata for
     * the purposes of determining whether we've got a tokenizer
     * like this already.  Like hashcode.
     */
    public boolean equals(Object o){
        if (o==null) return false; // !!
        
        try {
            TM other = (TM)o;
            if (name.equals(other.name) && 
                description.equals(other.description) &&
                classname.equals(other.classname) &&
                Arrays.equals(supportedLocales, other.supportedLocales))
                return true;
        } catch (ClassCastException cce){
            log.info("Class cast exception");
        }
        return false;
    }
    
    public String toString(){
        return name;
    }
    
    public static void main(String[] args) {
        TM tm = new TM("name", "descri", null, "classname", new Locale[]{Locale.getDefault()});
        TM tm2 = new TM("name", "descri", null, "classname", new Locale[]{Locale.getDefault()});
        
        Set s1 =new HashSet();
        s1.add(tm);
        Set s2 = new HashSet();
        s2.add(tm2);
        
        System.out.println(s1.equals(s2));
        System.out.println(tm.equals(tm2));
    }
}
