package edu.harvard.wcfia.yoshikoder.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.harvard.wcfia.yoshikoder.document.DocumentList;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;


/**
 * A report on a single document with respect to a particular dictionary
 * @author will
 *
 */
abstract public class AbstractReport extends AbstractTableModel implements YKReport {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.reporting.AbstractReport");
    
    protected String title;
    protected String description;
    protected Date date;
    protected String dictionaryName;
    protected DocumentList documentList;
    
    protected Object[][] data; 
    
    public AbstractReport(String reportTitle, String desc, String dictName, DocumentList dl){
        super();
        title = reportTitle;
        date = new Date();
        description = desc;
        dictionaryName = dictName;
        documentList = dl;
        //data = initData(obj);
    }
    
    abstract protected Object[][] initData(); // subclasses decide what they want to show
    
    // Partial TableModel implementation
    
    abstract public String getColumnName(int column);
    
    abstract public Class getColumnClass(int colIndex);
    
    public int getColumnCount() {
        return data[0].length;
    }
    
    public int getRowCount() {
        return data.length;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }
    
    // report metadata
    
    public Date getDate() {
        return date;
    }
    
    public String getDescription() {
        return description;
    }

    public DocumentList getDocuments() {
        return documentList;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

    public String getTitle() {
        return title;
    }

    public void saveAsHtml(File f) throws IOException {
        Properties props = new Properties();
        InputStream str = 
            AbstractReport.class.getClassLoader().getResourceAsStream("velocity.properties");
        props.load(str);
        Writer out = null;
        try {
            Velocity.init(props);
            VelocityContext context = new VelocityContext();
            context.put("title", getTitle());
            if (dictionaryName != null)
                context.put("dictionaryName", dictionaryName);
            else
                context.put("dictionaryName", "None");
            context.put("date", getDate());
            context.put("description", getDescription());
            context.put("documentList", getDocuments());
            
            List columnHeadings = new ArrayList();
            for (int i = 0; i < getColumnCount(); i++) 
                columnHeadings.add(getColumnName(i));
            context.put("columnHeadings", columnHeadings);
            context.put("rowCount", new Integer(getRowCount()-1));
            context.put("colCount", new Integer(getColumnCount()-1));
            context.put("tableModel", this);
                       
            FileOutputStream fos = new FileOutputStream(f);
            out = new OutputStreamWriter(fos, "UTF-8"); // *export* in UTF-8
                // assume that the templates are all *stored* in UTF-8
                Velocity.mergeTemplate("templates/htmlreport.vm", "UTF-8", context, out);
        } catch (Exception mie){
            log.log(Level.WARNING, "Could not fill template", mie);
            throw new IOException(mie.getMessage());
        } finally {
            try { 
                out.close(); 
            } catch (Exception ex){ }
        }
    }
    
    protected void setExcelValue(HSSFRow row, int column, String value){
        HSSFCell cell = row.createCell((short)column);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue(value);        
    }
    
    public void saveAsExcel(File f) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFRow row;
        HSSFCell cell;
        
        HSSFSheet sheet = wb.createSheet(getTitle());
        
        row = sheet.createRow((short)0);
        setExcelValue(row, 0, "Title:");
        setExcelValue(row, 1, getTitle());
        
        row = sheet.createRow((short)1);
        setExcelValue(row, 0, "Description:");
        setExcelValue(row, 1, getDescription());
        
        row = sheet.createRow((short)2);
        setExcelValue(row, 0, "Date:");
        setExcelValue(row, 1, getDate().toString());
        
        row = sheet.createRow((short)3);
        setExcelValue(row, 0, "Dictionary:");
        if (getDictionaryName() == null)
            setExcelValue(row, 1, "None");
        else 
            setExcelValue(row, 1, getDictionaryName());
        
        row = sheet.createRow((short)4);
        setExcelValue(row, 0, "Documents:");
        
        int ii=5;
        DocumentList dl = getDocuments();
        for (Iterator iterator = dl.iterator(); iterator.hasNext();) {
            YKDocument d = (YKDocument) iterator.next();
            row = sheet.createRow((short)ii);
            setExcelValue(row, 0, d.getTitle());
            ii++;
        }
        ii++;
        
        // now the table model
        
        row = sheet.createRow((short)ii);
        for (int c=0; c<getColumnCount(); c++){
            setExcelValue(row, c, getColumnName(c));
        }
        ii++;

        for (int r=0; r<getRowCount(); r++){
            row = sheet.createRow((short)(ii+r));
            for (int c=0; c<getColumnCount(); c++){
                cell = row.createCell((short)c);
                Object val = getValueAt(r,c);
                if (val == null)
                    continue;
                else if (val instanceof Integer)
                    cell.setCellValue(((Integer)val).intValue());
                else if (val instanceof Double)
                    cell.setCellValue(((Double)val).doubleValue());
                else if (val instanceof String){
                    cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellValue((String)val);
                } else  {                  
                    cell.setCellValue(val.toString());
                }
            }
        }
        FileOutputStream fout = new FileOutputStream(f);
        wb.write(fout);
        fout.close();                
    }
    
}