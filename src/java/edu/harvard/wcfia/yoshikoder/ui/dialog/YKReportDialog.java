package edu.harvard.wcfia.yoshikoder.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.reporting.YKReport;
import edu.harvard.wcfia.yoshikoder.ui.TableSorter;
import edu.harvard.wcfia.yoshikoder.ui.TableUtil;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.ExportUtil;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class YKReportDialog extends JDialog {

    protected JButton saveButton;
    protected JButton closeButton;

    protected FileDialog htmlExporter, excelExporter;
    protected ExportDialog exportDialog;
    
    protected TaskWorker worker;
    protected Yoshikoder yoshikoder;
    
    protected YKReport report;
    
    public YKReportDialog(Yoshikoder parent, YKReport rep){
        super(parent, rep.getTitle(), true); // name the dialog after the report
              
        yoshikoder = parent;
        report = rep;
                
        makeGUI();
    }
 
    protected void makeGUI(){        
        Container cPane = getContentPane();
        cPane.setLayout(new BorderLayout());
        
        JPanel buttons = createButtonPanel();
        //cPane.add(buttons, BorderLayout.SOUTH);
        
        TableSorter sorter = new TableSorter(report);
        JTable table = new JTable(sorter);
        sorter.setTableHeader(table.getTableHeader());
        table.setFont(yoshikoder.getDisplayFont());
        TableUtil.packColumn(table, 0, 2);

        JPanel central = new JPanel(new BorderLayout());
        central.add(new JScrollPane(table), BorderLayout.CENTER);

        String desc = report.getDescription();
        if (desc != null){
            JTextArea darea = new JTextArea(desc);
            darea.setEditable(false);
            darea.setFont(yoshikoder.getDisplayFont());
            darea.setBackground(this.getBackground());
            darea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            central.add(darea, BorderLayout.NORTH);
        }
        
        central.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        cPane.add(central, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(yoshikoder);
    }
    
    protected JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout());

        Box bbox = Box.createHorizontalBox();
        saveButton = new JButton(Messages.getString("export")); 
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
                    DialogUtil.yelp(YKReportDialog.this, "Could not save as HTML", e);  // TODO loc
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
                    DialogUtil.yelp(YKReportDialog.this, "Could not save as Excel", e);  // TODO loc
                }
            };
            worker.start();
        }
    }
    
}
