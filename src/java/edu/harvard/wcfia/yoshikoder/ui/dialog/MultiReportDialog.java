package edu.harvard.wcfia.yoshikoder.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.document.YKDocument;
import edu.harvard.wcfia.yoshikoder.reporting.YKReport;
import edu.harvard.wcfia.yoshikoder.ui.TableSorter;
import edu.harvard.wcfia.yoshikoder.ui.TableUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class MultiReportDialog extends JDialog {

    protected JButton saveButton;
    protected JButton closeButton;

    protected FileDialog htmlExporter, excelExporter;
    protected ExportDialog exportDialog;
    
    protected TaskWorker worker;
    protected Yoshikoder yoshikoder;
    
    protected List reports;
    protected JTabbedPane tabs;
    
    
    public MultiReportDialog(Yoshikoder parent, List reps){
        super(parent, true);
              
        yoshikoder = parent;
        reports = reps;
        
        makeGUI();
    }
 
    protected void makeGUI(){        
        Container cPane = getContentPane();
        cPane.setLayout(new BorderLayout());
        
        JPanel buttons = createButtonPanel();
        cPane.add(buttons, BorderLayout.SOUTH);
        
        tabs = new JTabbedPane();
        for (Iterator iter = reports.iterator(); iter.hasNext();) {
            YKReport report = (YKReport) iter.next();
            
            TableSorter sorter = new TableSorter(report);
            JTable table = new JTable(sorter);
            sorter.setTableHeader(table.getTableHeader());
            table.setFont(yoshikoder.getDisplayFont());
            TableUtil.packColumn(table, 0, 2);
            
            YKDocument doc = (YKDocument)report.getDocuments().get(0);
            JPanel cent = new JPanel(new BorderLayout());
            cent.add( new JScrollPane(table), BorderLayout.CENTER );
            cent.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            tabs.addTab(doc.getTitle(), cent);
        }
        
        JPanel central = new JPanel(new BorderLayout());
        central.add(tabs, BorderLayout.CENTER);
        central.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        cPane.add(central, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(yoshikoder);
    }
    
    protected JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout());

        Box bbox = Box.createHorizontalBox();
        saveButton = new JButton(Messages.getString("export")); 
        // XXX make multidocument export work
        saveButton.setEnabled(false);
        
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                handleSave();
            }
        });
        closeButton = new JButton(Messages.getString("close")); 
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                dispose();
            }
        });
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener( new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                dispose();} });
        
        bbox.add(Box.createHorizontalGlue());
        bbox.add(saveButton);
        bbox.add(Box.createHorizontalStrut(15));
        bbox.add(closeButton);
        bbox.add(Box.createHorizontalGlue());
        bbox.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                Color.GRAY));
        buttonPanel.add(bbox, BorderLayout.EAST);

        return buttonPanel;
    }
    
    protected void handleSave(){
        // XXX make multi report saving work
        /*
        // throw up export dialog
        if (exportDialog == null)
            exportDialog = 
                new ExportDialog(this, new String[]{ExportUtil.HTML_FORMAT, 
                     ExportUtil.EXCEL_FORMAT});

        exportDialog.show();
        String format = exportDialog.getChosenFormat();
        if (format==null) return;
            
        if (format == ExportUtil.HTML_FORMAT){
            if (htmlExporter == null)
                htmlExporter = 
                    DialogUtil.makeFileDialog(yoshikoder, "Export as HTML", 
                            FileDialog.SAVE, DialogUtil.htmlFilenameFilter); // TODO loc
            
            htmlExporter.show();
            String f = htmlExporter.getFile();
            if (f==null) return;
            
            final File file = new File(htmlExporter.getDirectory(), f);
            
            worker = new TaskWorker(this){
                protected void doWork() throws Exception {
                    report.saveAsHtml(FileUtil.suffix(file, "html", "htm"));
                }
                protected void onError() { 
                    DialogUtil.yelp(MultiReportDialog.this, "Could not save as HTML", e);  // TODO loc
                }
            };
            worker.start();
        } else {
            if (excelExporter == null)
                excelExporter = 
                    DialogUtil.makeFileDialog(yoshikoder, "Export as Excel", 
                            FileDialog.SAVE, DialogUtil.xlsFilenameFilter); // TODO loc
            
            excelExporter.show();
            String f = excelExporter.getFile();
            if (f==null) return;
            
            final File file = new File(excelExporter.getDirectory(), f);
            
            worker = new TaskWorker(this){
                protected void doWork() throws Exception {
                    report.saveAsExcel(FileUtil.suffix(file, "xls"));
                }
                protected void onError() { 
                    DialogUtil.yelp(MultiReportDialog.this, "Could not save as Excel", e);  // TODO loc
                }
            };
            worker.start();
        }
        */
    }

    
}
