package edu.harvard.wcfia.yoshikoder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class VersionHandler extends DefaultHandler {
    
    private int version = ImportUtil.UNKNOWN_FILE;     
    private boolean done = false;
    
    // check the first, top level element only
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException{
        if (!done){
            String vers = attributes.getValue("style");
            if (vers != null){
                if (qName.equals("dictionary") && "041204".equals(vers)) 
                    version = ImportUtil.YKDICTIONARY_041204_FILE;
                else if (qName.equals("dictionary") && "050805".equals(vers))
                    version = ImportUtil.YKDICTIONARY_050805_FILE;
                else if (qName.equals("ykproject") && "050805".equals(vers))
                    version = ImportUtil.YKPROJECT_050805_FILE;
                else if (qName.equals("concordance") && "050805".equals(vers))
                    version = ImportUtil.YKCONCORDANCE_050805_FILE; 
            }
            done = true;
        }
    }   
    
    public int getFileVersion(){
        return version;
    }
    
    public static void main(String[] args) {
        
        if (args.length==0)
            args = new String[]{"/Users/will/java/foo/yk2/ReleaseNotes.txt"};
            
        File f = new File(args[0]);
        if (!f.exists()){
            System.out.println("File does not exist");
            System.exit(0);
        }
        
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            
            VersionHandler h = new VersionHandler();
            InputStream stream = new FileInputStream(f);
            parser.parse(stream, h);
            stream.close();
            int ver = h.getFileVersion();
            
            System.out.println("YKDictionary 041204 style?  " + (ver==ImportUtil.YKDICTIONARY_041204_FILE));
            System.out.println("YKDictionary 050805 style?  " + (ver==ImportUtil.YKDICTIONARY_050805_FILE));
            System.out.println("YKProject 050805 style?     " + (ver==ImportUtil.YKPROJECT_050805_FILE));
            System.out.println("YKConcordance 050805 style? " + (ver==ImportUtil.YKCONCORDANCE_050805_FILE));
           
        } catch (Exception ex){
            if (ex instanceof SAXException)
                System.out.println("Unknown file type...");
            else 
                ex.printStackTrace();
        }
    }
}
