package edu.harvard.wcfia.yoshikoder.reporting;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.table.TableModel;

import edu.harvard.wcfia.yoshikoder.document.DocumentList;

/**
 * Basic report metadata and save functions.
 * @author will
 *
 */
public interface YKReport extends TableModel {

    public String getTitle();
    
    public Date getDate();
    
    public String getDescription();
    
    public String getDictionaryName();
    
    public DocumentList getDocuments();
    
    public void saveAsHtml(File f) throws IOException;
    
    public void saveAsExcel(File f) throws IOException;
    
}
