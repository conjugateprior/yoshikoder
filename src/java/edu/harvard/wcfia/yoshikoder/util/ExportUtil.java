package edu.harvard.wcfia.yoshikoder.util;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.harvard.wcfia.yoshikoder.YKProject;
import edu.harvard.wcfia.yoshikoder.concordance.Concordance;
import edu.harvard.wcfia.yoshikoder.concordance.ConcordanceLine;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.Node;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.dictionary.YKDictionary;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.Token;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenList;

/**
 * @author will
 */
public class ExportUtil {
    
    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.util.ExportUtil");
    
    public static String XML_FORMAT = Messages.getString("XML");
    public static String EXCEL_FORMAT = Messages.getString("XLS");
    public static String HTML_FORMAT = Messages.getString("HTML");
    public static String TXT_FORMAT = Messages.getString("TXT");
    
    // a decent templating system would be nice...
    private static String htmlHeader = 
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +  
        "<html>\n<head>\n<meta content=\"text/html; charset=UTF-8\" " +  
        "http-equiv=\"content-type\">\n" +  
        "<title>yoshikoder</title>\n</head><style type=\"text/css\">" +
        "ul {list-style-type: disc;}</style>\n<body>\n"; 
    private static String htmlFooter = "</body></html>"; 
    
    private static String xmlHeader = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"; 
    
    
    static String escapeXML(String str) {
        StringBuffer sb = new StringBuffer();
        char[] strc = str.toCharArray();
        for (int ii = 0; ii < strc.length; ii++) {
            if (strc[ii] == '<') {
                sb.append("&lt;"); 
            } else if (strc[ii] == '>') {
                sb.append("&gt;"); 
            } else if (strc[ii] == '&') {
                sb.append("&amp;"); 
            } else if (strc[ii] == '\'') {
                sb.append("&apos;"); 
            } else if (strc[ii] == '"') {
                sb.append("&quot;"); 
            } else {
                sb.append(strc[ii]);
            }
        }
        return sb.toString();
    }
    
    public static void exportAsHTML(YKDictionary dict, File f) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(htmlHeader.replaceAll("yoshikoder", dict.getName())); 
        sb.append("<h4>");
        sb.append(Messages.getString("ExportUtil.dictionaryEntriesLabel")); 
        sb.append("</h4>\n");
        sb.append("<ul>"); 
        recurseHTML(dict.getDictionaryRoot(), sb);
        sb.append("</ul>");         
        sb.append(htmlFooter);
        FileUtil.save(f, sb.toString(), "UTF-8"); 
    }
    
    public static void exportAsHTML(YKProject proj, File f) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(htmlHeader.replaceAll("yoshikoder", proj.getName())); 
        sb.append("<h4>");
        sb.append(Messages.getString("ExportUtil.projectLabel") + " "); 
        sb.append(escapeXML(proj.getName()));
        sb.append("</h4>"); 
        sb.append("<table>"); 
        
        sb.append("<tr><td align=\"right\">");
        sb.append(Messages.getString("ExportUtil.dateLabel"));
        sb.append("</td><td>" + (new java.util.Date()) + "</td></tr>\n");
        
        sb.append("<tr><td align=\"right\">");
        sb.append(Messages.getString("descriptionLabel"));
        sb.append("</td><td>"); 
        if (proj.getDescription() != null) {
            sb.append(escapeXML(proj.getDescription()));
        }
        sb.append("</td></tr>\n"); 
        
        sb.append("<tr><td align=\"right\">");
        sb.append(Messages.getString("ExportUtil.locationLabel"));
        sb.append("</td><td>"); 
        if (proj.getLocation() != null) {
            String loc = escapeXML(proj.getLocation()
                    .getAbsolutePath());
            sb.append(loc);
        }
        sb.append("</td></tr>\n"); 
        sb.append("</table>"); 
        
        YKDictionary dict = proj.getDictionary();
        sb.append("<h4>");
        sb.append(Messages.getString("ExportUtil.dictionaryEntriesLabel")); 
        sb.append("</h4>\n");
        sb.append("<ul>"); 
        recurseHTML(dict.getDictionaryRoot(), sb);
        sb.append("</ul>"); 
        
        sb.append("<h4>");
        sb.append(Messages.getString("ExportUtil.documentsLabel")); 
        sb.append("</h4>");
        sb.append("<ul>\n"); 
        for (Iterator iter = proj.getDocumentList().iterator(); iter
        .hasNext();) {
            YKDocument doc = (YKDocument)iter.next();
            sb.append(toHTML(doc));
        }
        sb.append("</ul>\n"); 
        sb.append(htmlFooter);
        
        //System.out.println(sb);
        
        FileUtil.save(f, sb.toString(), "UTF-8"); 
        
    }
    
    protected static String toHTML(YKDocument doc){
        StringBuffer sb = new StringBuffer();
        sb.append("<li>" +  
                escapeXML(doc.getTitle()) + "</li>"); 
        return sb.toString();
    }
    
    protected static void recurseHTML(Node n, StringBuffer sb){
        if (n instanceof CategoryNode){
            CategoryNode cn = (CategoryNode)n;           
            sb.append(toHTML(cn));
            sb.append("<ul>\n"); 
            Enumeration en = cn.children();
            while (en.hasMoreElements()){
                Node child = (Node)en.nextElement();
                recurseHTML(child, sb);
            }
            sb.append("</ul>\n"); 
        } else {
            PatternNode pn = (PatternNode)n;
            sb.append(toHTML(pn));
        }
    }
    
    // lacks a closing />
    protected static String toHTML(CategoryNode node){
        StringBuffer sb = new StringBuffer();
        sb.append("<li>"); 
        sb.append("<b>" + escapeXML(node.getName()) + "</b>");  
        if (node.getScore() != null)
            sb.append(" (" + node.getScore() + ")");  
        if (node.getDescription() != null)
            sb.append(" <i>" + escapeXML(node.getDescription()) + "</i>");   
        sb.append("</li>\n"); 
        
        return sb.toString();
    }
    
    // a complete pattern
    protected static String toHTML(PatternNode node){
        StringBuffer sb = new StringBuffer();
        sb.append("<li>"); 
        sb.append(escapeXML(node.getName()));
        if (node.getScore() != null)
            sb.append(" (" + node.getScore() + ")");  
        sb.append("</li>\n"); 
        
        return sb.toString();
    }
    
    protected static void initializeVelocity() throws Exception {        
        Properties props = new Properties();
        InputStream str = 
            ExportUtil.class.getClassLoader().getResourceAsStream("velocity.properties");
        props.load(str);
        Velocity.init(props);
    }
    
    public static void exportAsHTML(Concordance conc, File f) throws IOException{
        try {
            initializeVelocity();
        } catch (Exception e){
            throw new IOException("Could not initialize templating engine");
        }
        
        VelocityContext context = new VelocityContext();
        context.put("title", Messages.getString("concordance"));
        context.put("concordance", conc);
        context.put("date", new Date());
        FileOutputStream fos = new FileOutputStream(f);
        Writer out = new OutputStreamWriter(fos, "UTF-8"); // *export* in UTF-8
        try { 
            // assume that the templates are all *stored* in UTF-8
            Velocity.mergeTemplate("templates/concordance.vm", "UTF-8", context, out);
        } catch (Exception mie){
            log.log(Level.WARNING, "Could not fill template", mie);
            throw new IOException(mie.getMessage());
        } finally {
            try { 
                out.close(); 
            } catch (Exception ex){ }
        }
    }
    
    /*
    public static void exportAsHTML(CorpusReport rep, File f) throws IOException{
        
     if (false){
        VelocityContext context = new VelocityContext();
        context.put("title", Messages.getString("ExportUtil.corpusReport"));
        context.put("report", rep);
        
        YKDocument d = (YKDocument)rep.getDocuments().get(0);
        DocumentReport report = rep.getReport(d);
        List words = report.getFrequencyMap().getWords();
        context.put("vocabulary", words);
        
        context.put("date", new Date());
        FileOutputStream fos = new FileOutputStream(f);
        Writer out = new OutputStreamWriter(fos, "UTF-8"); // *export* in UTF-8
        try { 
            // assume that the templates are all *stored* in UTF-8
            Velocity.mergeTemplate("templates/corpusreport.vm", "UTF-8", context, out);
        } catch (Exception mie){
            log.log(Level.WARNING, "Could not fill template", mie);
            throw new IOException(mie.getMessage());
        } finally {
            try { 
                out.close(); 
            } catch (Exception ex){ }
        }
    } else {
        StringBuffer sb = new StringBuffer();
        sb.append( htmlHeader.replaceFirst("yoshikoder", 
                Messages.getString("ExportUtil.corpusReport")) );          
        sb.append("<h4>");
        sb.append(Messages.getString("ExportUtil.corpusReport")); 
        sb.append("</h4>");
        List docs = rep.getDocuments();
        for (Iterator iter = docs.iterator(); iter.hasNext();) {
            YKDocument doc = (YKDocument)iter.next();
            sb.append("<h4>");
            sb.append(Messages.getString("ExportUtil.documentLabel"));
            sb.append(doc.getTitle() + "</h4>\n");
            sb.append("<table>\n"); 
            sb.append("<tr><td>");
            sb.append(Messages.getString("word"));
            sb.append("</td><td>");
            sb.append(Messages.getString("count"));
            sb.append("</td></tr>\n");
            
            DocumentReport dr = rep.getReport(doc);
            FrequencyMap map = dr.getFrequencyMap();
            for (Iterator iterator = map.getWords().iterator(); iterator.hasNext();) {
                String word = (String)iterator.next();
                Integer count = map.getFrequency(word);
                sb.append("<tr><td>" + escapeXML(word) +  
                        "</td><td>" + count + "</td></tr>\n");  
            }
            sb.append("</table>"); 
        }
        sb.append(htmlFooter);            
        FileUtil.save(f, sb.toString(), "UTF-8"); 
        }
    }
    
    public static void exportAsHTML(DictionaryReport rep, File f) throws IOException{
        StringBuffer sb = new StringBuffer();
        sb.append( htmlHeader.replaceFirst("yoshikoder", 
                Messages.getString("ExportUtil.dictionaryReport")) );          
        sb.append("<h4>");
        sb.append(Messages.getString("ExportUtil.dictionaryReport")); 
        sb.append("</h4>");
        List docs = rep.getDocuments();
        for (Iterator iter = docs.iterator(); iter.hasNext();) {
            YKDocument doc = (YKDocument)iter.next();
            sb.append("<h4>");
            sb.append(Messages.getString("ExportUtil.documentLabel"));
            sb.append(doc.getTitle() + "</h4>\n");  
            sb.append("<table>\n"); 
            sb.append("<tr><td><b>");
            sb.append(Messages.getString("ExportUtil.path"));
            sb.append("</b></td><td><b>");
            sb.append(Messages.getString("count"));
            sb.append("</b></td>"); 
            sb.append("<td><b>");
            sb.append(Messages.getString("score"));
            sb.append("</b></td></tr>\n"); 
            
            SubReport sr = rep.getSubReport(doc);
            EntryStatisticsMap map = sr.getEntryStatisticsMap();
            List entries = map.getEntries();
            Collections.sort(entries);
            for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
                String catpath = (String)iterator.next();
                Integer count = map.getCount(catpath);
                
                sb.append("<tr><td>" + escapeXML(catpath) +  
                        "</td><td>" + count + "</td>");  
                sb.append("<td>"); 
                Double d = map.getScore(catpath);
                if (d != null)
                    sb.append((count.intValue() * d.doubleValue()));	
                sb.append("</td></tr>\n"); 
            }
            sb.append("</table>"); 
        }
        sb.append(htmlFooter);
        
        FileUtil.save(f, sb.toString(), "UTF-8"); 
    }
    
    public static void exportAsHTML(ComparisonReport rep, File f) throws IOException{
        StringBuffer sb = new StringBuffer();
        sb.append( htmlHeader.replaceFirst("yoshikoder", 
                Messages.getString("ExportUtil.documentComparison")) );          
        sb.append("<h4>");
        sb.append(Messages.getString("ExportUtil.documentComparison")); 
        sb.append("</h4>");
        sb.append("<p>\n");
        sb.append(Messages.getString("ExportUtil.documentLabel") + "<br/>\n");
        sb.append("(1) " + escapeXML(rep.getFirstDocument().getTitle()) + "<br/>");
        sb.append("(2) " + escapeXML(rep.getSecondDocument().getTitle()) + "</p>\n");
        sb.append("<table>");
        sb.append("<tr><td><b>");
        sb.append(Messages.getString("ExportUtil.path"));
        sb.append("</b></td><td><b>");
        sb.append(Messages.getString("count") + " (1)");
        sb.append("</b></td><td><b>");
        sb.append(Messages.getString("count") + " (2)");
        sb.append("</b></td><td><b>");
        sb.append(Messages.getString("ExportUtil.ratio"));
        sb.append("</b></td></tr>\n");

        Object[][] dat = rep.getData();
        for (int ii=0; ii<dat.length; ii++){
            sb.append("<tr><td>");
            sb.append(escapeXML(dat[ii][0].toString()));
            sb.append("</td><td>");
            sb.append(escapeXML(dat[ii][1].toString()));
            sb.append("</td><td>");
            sb.append(escapeXML(dat[ii][2].toString()));
            sb.append("</td><td>");
            Double d = (Double)dat[ii][3];
            if (d != null)
                sb.append(escapeXML(dat[ii][0].toString()));
            else 
                sb.append("-");
            sb.append("</td></tr>\n");
        }
        sb.append("</table>");
        sb.append(htmlFooter);
        
        FileUtil.save(f, sb.toString(), "UTF-8"); 
    }

    public static void exportAsXLS(ComparisonReport rep, File f) throws IOException {
        
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFRow row;
        HSSFCell cell;

        YKDocument doc1 = rep.getFirstDocument();
        YKDocument doc2 = rep.getSecondDocument();
        
        HSSFSheet sheet = wb.createSheet(Messages.getString("ExportUtil.documentComparison"));
        
        row = sheet.createRow((short)0);
        
        cell = row.createCell((short)0);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue(Messages.getString("ExportUtil.documentLabel"));
        
        row = sheet.createRow((short)2);
        
        cell = row.createCell((short)0);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue("(1) " + escapeXML(doc1.getTitle()));
        
        row = sheet.createRow((short)3);
        
        cell = row.createCell((short)0);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue("(2) " + escapeXML(doc2.getTitle()));
        
        row = sheet.createRow((short)5);
        
        cell = row.createCell((short)0);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue(Messages.getString("ExportUtil.path"));
        cell = row.createCell((short)1);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue("(1) " + Messages.getString("count"));
        cell = row.createCell((short)2);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue("(2) " + Messages.getString("count"));
        cell = row.createCell((short)3);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue(Messages.getString("ExportUtil.ratio"));
        
        Object[][] dat = rep.getData();
        for (int ii=0; ii<dat.length; ii++){
            row = sheet.createRow((short)(6+ii));
            
            cell = row.createCell((short)0);
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellValue(escapeXML(dat[ii][0].toString()));
            cell = row.createCell((short)1);
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellValue(escapeXML(dat[ii][1].toString()));
            cell = row.createCell((short)2);
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellValue(escapeXML(dat[ii][2].toString()));
            cell = row.createCell((short)3);
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            Double d = (Double)dat[ii][3];
            if (d == null)
                cell.setCellValue("-");
            else
                cell.setCellValue(escapeXML(dat[ii][3].toString()));
            
            FileOutputStream fout = new FileOutputStream(f);
            wb.write(fout);
            fout.close();                    
        }        
    }
        
    public static void exportAsXLS(CorpusReport rep, File f) throws IOException {
        
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFRow row;
        HSSFCell cell;
        
        HSSFSheet sheet = null;
        for (Iterator iter = rep.getDocuments().iterator(); iter.hasNext();) {
            YKDocument doc = (YKDocument) iter.next();
            DocumentReport drep = rep.getReport(doc);
            
            sheet = wb.createSheet(doc.getTitle());
            FrequencyMap map = drep.getFrequencyMap();
            int ii = 0;
            for (Iterator iterator = map.getWords().iterator(); 
            iterator.hasNext();) {
                
                row = sheet.createRow((short)ii);
                
                String word = (String) iterator.next();
                cell = row.createCell((short)0);
                cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                cell.setCellValue(word);
                
                Integer count = map.getFrequency(word);
                cell = row.createCell((short)1);
                cell.setCellValue(count.intValue());
                
                ii++;
            }
        }
        FileOutputStream fout = new FileOutputStream(f);
        wb.write(fout);
        fout.close();                    
    }
    
    public static void exportAsXLS(DictionaryReport rep, File f) throws IOException{
        
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFRow row;
        HSSFCell cell;
        
        HSSFSheet sheet = null;
        for (Iterator iter = rep.getDocuments().iterator(); iter.hasNext();) {
            YKDocument doc = (YKDocument) iter.next();
            SubReport srep = rep.getSubReport(doc);
            
            sheet = wb.createSheet(doc.getTitle());
            EntryStatisticsMap map = srep.getEntryStatisticsMap();
            int ii = 0;
            List entries = map.getEntries();
            Collections.sort(entries);
            for (Iterator iterator = entries.iterator(); 
            iterator.hasNext();) {
                
                row = sheet.createRow((short)ii);
                
                String catname = (String) iterator.next();
                cell = row.createCell((short)0);
                cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                cell.setCellValue(catname);
                
                Integer count = map.getCount(catname);
                cell = row.createCell((short)1);
                cell.setCellValue(count.intValue());
                
                Double sc = map.getScore(catname);
                if (sc != null){
                    double comb = sc.doubleValue() * count.intValue();
                    cell = row.createCell((short)2);
                    cell.setCellValue(comb);
                }
                ii++;
            }
        }
        FileOutputStream fout = new FileOutputStream(f);
        wb.write(fout);
        fout.close();                
    }
    */
    
    public static void exportAsXLS(Concordance concordance, File f) throws IOException{
        String localTitle = 
            Messages.getString("concordance"); 
        
        // excel worksheet
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(localTitle);
        
        HSSFRow row;
        HSSFCell cell;
        
        HSSFCellStyle lhs = wb.createCellStyle();
        lhs.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        
        HSSFCellStyle tar = wb.createCellStyle();
        tar.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        
        HSSFCellStyle rhs = wb.createCellStyle();
        rhs.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        
        int ii;
        row = sheet.createRow((short)0); // blank line
        cell = row.createCell((short)0);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue(localTitle);
        
        if (concordance != null) {
            ii = 0;
            for (Iterator iter = concordance.getLines().iterator(); iter.hasNext();) {
                ConcordanceLine line = (ConcordanceLine)iter.next();
                // contents
                row = sheet.createRow((short)ii + 2);
                
                cell = row.createCell((short)0);
                cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                cell.setCellStyle(lhs);
                cell.setCellValue(line.getLeftHandSideView());
                
                cell = row.createCell((short)1);
                cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                cell.setCellStyle(tar);
                cell.setCellValue(line.getTargetView());
                
                cell = row.createCell((short)2);
                cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                cell.setCellStyle(rhs);
                cell.setCellValue(line.getRightHandSideView());
                
                ii++;
            }
        }
        FileOutputStream fout = new FileOutputStream(f.toString());
        wb.write(fout);
        fout.close();        
    }    
    
    protected static void recurse(Node n, StringBuffer sb){
        if (n instanceof CategoryNode){
            CategoryNode cn = (CategoryNode)n;
            sb.append(toXML(cn));
            Enumeration en = cn.children();
            while (en.hasMoreElements()){
                Node child = (Node)en.nextElement();
                recurse(child, sb);
            }
            sb.append("</cnode>\n"); 
        } else {
            PatternNode pn = (PatternNode)n;
            sb.append(toXML(pn));
        }
    }
    
    public static void exportAsXML(YKDictionary dict, File f) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(xmlHeader);
        sb.append("<dictionary style=\"050805\"");  
        
        String pclassname = 
            escapeXML(dict.getPatternEngine().getType());
        sb.append(" patternengine=\"" + pclassname + "\">\n");         
        recurse((CategoryNode)dict.getRoot(), sb);
        sb.append("</dictionary>\n"); 
        
        FileUtil.save(f, sb.toString(), "UTF-8");
    }
    
    // XML
    public static void exportAsXML(YKProject proj, File f) throws IOException{
        StringBuffer sb = new StringBuffer();
        sb.append(xmlHeader);
        sb.append("<ykproject style=\"050805\""); 
        sb.append(" name=\"" + escapeXML(proj.getName()) + "\"");  
        String fname = DialogUtil.toString(proj.getDisplayFont());
        sb.append(" font=\"" + escapeXML(fname) + "\"");  
        
        if (proj.getDescription() != null)
            sb.append(" desc=\"" + escapeXML(proj.getDescription()) + "\"");  
        if (proj.getLocation()!=null){
            String loc = escapeXML(proj.getLocation().getAbsolutePath());
            sb.append(" location=\"" + loc + "\"");  
        }
        sb.append(" creationdate=\"" + (new java.util.Date()) + "\">\n");  
        YKDictionary dict = proj.getDictionary();
        //sb.append("<dictionary windowsize=\"" + proj.getWindowSize() + "\"");
        sb.append("<dictionary ");
        
        // XXX skip reporter in export
        /*
        String classname = 
            escapeXML(dict.getReporter().getClass().getName());
        sb.append(" reporterclassname=\"" + classname + "\"");  
        */
        
        String pclassname = 
            escapeXML(dict.getPatternEngine().getType());
        sb.append(" patternengine=\"" + pclassname + "\">\n");         
        recurse((CategoryNode)dict.getRoot(), sb);
        sb.append("</dictionary>\n"); 
        
        sb.append("<documentlist>\n"); 
        for (Iterator iter = proj.getDocumentList().iterator(); iter.hasNext();) {
            YKDocument doc = (YKDocument)iter.next();
            sb.append(toXML(doc));
        }
        sb.append("</documentlist>\n"); 
        sb.append("</ykproject>\n"); 
        
        FileUtil.save(f, sb.toString(), "UTF-8"); 
    }
    
    protected static String toXML(YKDocument doc){
        StringBuffer sb = new StringBuffer();
        sb.append("<document title=\"" +  
                escapeXML(doc.getTitle()) + "\""); 
        
        if (doc.getLocation()!=null)
            sb.append(" location=\"" +  
                    escapeXML(doc.getLocation().getAbsolutePath()) + 
            "\""); 
        if (doc.getLocale()!=null)
            sb.append(" locale=\"" +  
                    escapeXML(doc.getLocale().toString()) + 
            "\""); 
        if (doc.getCharsetName()!=null)
            sb.append(" charsetname=\"" +  
                    escapeXML(doc.getCharsetName()) + 
            "\""); 
        Font pf = doc.getPreferredFont();
        if (pf != null){
            sb.append(" font=\"" +  
                    escapeXML(pf.getFamily() + "-PLAIN-" + pf.getSize()) +  
            "\""); 
        }
        sb.append("/>\n"); 
        return sb.toString();
    }
    // lacks a closing
    protected static String toXML(CategoryNode node){
        StringBuffer sb = new StringBuffer();
        sb.append("<cnode name=\"" + escapeXML(node.getName()) + "\"");  
        if (node.getScore() != null)
            sb.append(" score=\"" + node.getScore() + "\"");  
        if (node.getDescription() != null)
            sb.append(" desc=\"" + escapeXML(node.getDescription()) + "\"");  
        sb.append(">\n"); 
        return sb.toString();
    }
    
    // a complete pattern
    protected static String toXML(PatternNode node){
        StringBuffer sb = new StringBuffer();
        sb.append("<pnode name=\"" + escapeXML(node.getName()) + "\"");  
        if (node.getScore() != null)
            sb.append(" score=\"" + node.getScore() + "\"");  
        sb.append("/>\n"); 
        return sb.toString();
    }
    
    public static void exportAsXML(Concordance conc, File f) 
    throws IOException{
        StringBuffer sb = new StringBuffer();
        sb.append(xmlHeader);
        sb.append("<concordance style=\"050805\""); 
        sb.append(" windowsize=\"" + conc.getWindowSize() + "\"");  
        sb.append(" creationdate=\"" +  
                (new java.util.Date()) + 
        "\">\n"); 
        for (Iterator iter = conc.iterator(); iter.hasNext();) {
            ConcordanceLine line = (ConcordanceLine)iter.next();
            sb.append("    <line>"); 
            TokenList lhs = line.getLeftHandSide();
            for (Iterator iterator = lhs.iterator(); iterator.hasNext();) {
                Token word = (Token)iterator.next();
                sb.append("<w txt=\"" + escapeXML(word.getText()) + "\"/>");  
            }
            sb.append( "<w txt=\"" + escapeXML(line.getTarget().getText()) +  
            "\" target=\"true\"/>"); 
            TokenList rhs = line.getRightHandSide();
            for (Iterator iterator = rhs.iterator(); iterator.hasNext();) {
                Token word = (Token)iterator.next();
                sb.append("<w txt=\"" + escapeXML(word.getText()) + "\"/>");  
            }
            sb.append("</line>\n"); 
        }
        sb.append("</concordance>\n"); 
        
        FileUtil.save(f, sb.toString(), "UTF-8"); 
    }
    
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        InputStream str = 
            ExportUtil.class.getClassLoader().getResourceAsStream("velocity.properties");
        props.load(str);
        Velocity.init(props);
        
        VelocityContext context = new VelocityContext();
        context.put("foo", "<<A>>");
        StringWriter sw = new StringWriter();
        Velocity.mergeTemplate("templates/test.vm", "UTF-8", context, sw);
        System.out.println(sw.toString());
        
        /*
        Concordance c = 
            ImportUtil.importConcordance(new File("/Users/will/Documents/conc.ykc"));
        exportAsHTML(c, new File("conc.html"));
        */
    }
    
}
