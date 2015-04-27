package edu.harvard.wcfia.yoshikoder.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import edu.harvard.wcfia.yoshikoder.YKProject;
import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNodeImpl;
import edu.harvard.wcfia.yoshikoder.dictionary.DuplicateException;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternEngine;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNodeImpl;
import edu.harvard.wcfia.yoshikoder.dictionary.SimpleDictionary;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;

/**
 * @author will
 */
public class ImportUtil {

    // kinds of XML file we can deal with (number denotes style)
    public static final int UNKNOWN_FILE = 0;
    public static final int YKDICTIONARY_041204_FILE = 1;
    public static final int YKDICTIONARY_050805_FILE = 2;
    public static final int YKPROJECT_050805_FILE = 3;
    public static final int YKCONCORDANCE_050805_FILE = 4;
        
    protected static SAXParser parser;    
            
    public static int getVersion(File f) 
    throws ParserConfigurationException, SAXException, IOException{
        
        if (parser == null){
            SAXParserFactory factory = SAXParserFactory.newInstance();
            parser = factory.newSAXParser();
        }
        
        VersionHandler h = new VersionHandler();
        InputStream stream = new FileInputStream(f);
        parser.parse(stream, h);
        stream.close();
        int ver = h.getFileVersion();
        return ver;
    }
    
    public static YKDictionary importYKDictionary(File f) 
    throws ParserConfigurationException, SAXException, IOException {

        int ver = getVersion(f);
        
        if (ver == YKDICTIONARY_041204_FILE)
            return importOldYKDictionary(f);
        else if (ver == YKDICTIONARY_050805_FILE)
            return import050805YKDictionary(f);
        else
            throw new SAXException("Cannot recognize file format");
    }
    
    // old version
    protected static YKDictionary importOldYKDictionary(File f)
    	throws ParserConfigurationException, SAXException, IOException{
        
        if (parser == null){
            SAXParserFactory factory = SAXParserFactory.newInstance();
            parser = factory.newSAXParser();
        } 
        
        YKOldDictionaryHandler h = new YKOldDictionaryHandler();
        InputStream stream = new FileInputStream(f);
        parser.parse(stream, h);
        stream.close();
        YKDictionary dict = h.getDictionary();
        dict.setName(f.getName());
        
        return dict;
    }
    
    public static YKProject importYKProject(File f) 
    	throws ParserConfigurationException, SAXException, IOException{
        
        if (parser == null){
            SAXParserFactory factory = SAXParserFactory.newInstance();
            parser = factory.newSAXParser();
        } 
        
        YKProjectHandler h = new YKProjectHandler();
        InputStream stream = new FileInputStream(f);
        parser.parse(stream, h);
        stream.close();
        YKProject proj = h.getProject();
                
        return proj;
    }
    
    // new version
    protected static YKDictionary import050805YKDictionary(File f) 
    throws ParserConfigurationException, SAXException, IOException{
        
        if (parser == null){
            SAXParserFactory factory = SAXParserFactory.newInstance();
            parser = factory.newSAXParser();
        } 
        
        YKDictionaryHandler h = new YKDictionaryHandler();
        InputStream stream = new FileInputStream(f);
        parser.parse(stream, h);
        stream.close();
        YKDictionary dict = h.getDictionary();
        
        return dict;
    }
    
    public static Concordance importConcordance(File f)
    	throws ParserConfigurationException, SAXException, IOException{
        
        if (parser == null){
            SAXParserFactory factory = SAXParserFactory.newInstance();
            parser = factory.newSAXParser();
        } 
        
        ConcordanceHandler h = new ConcordanceHandler();
        InputStream stream = new FileInputStream(f);
        parser.parse(stream, h);
        stream.close();
        Concordance conc = h.getConcordance();
        
        return conc;
    }
}