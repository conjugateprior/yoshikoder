package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.DuplicatePluginException;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.PluginException;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TM;
import edu.harvard.wcfia.yoshikoder.document.tokenizer.TokenizationService;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.Messages;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;


public class TokenizerPluginsPanel extends CommitableJPanel{
    
    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.ui.TokenizerPluginsPanel");
    
    protected Yoshikoder yoshikoder;
    
    protected JList list;
    protected DefaultListModel listModel;
    protected JButton add, remove;
    
    protected FileDialog chooser;
    
    public TokenizerPluginsPanel(Yoshikoder yk){
        super(new BorderLayout());
        yoshikoder = yk;
        chooser = new FileDialog(yoshikoder, Messages.getString("open"), FileDialog.LOAD);
        chooser.setFilenameFilter(DialogUtil.jarFilenameFilter);
                
        makeGUI();
    }

    protected void makeButtons(){
        remove = new JButton(Messages.getString("remove"));
        remove.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){ remove(); }
        });
        
        add = new JButton(Messages.getString("add"));
        add.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){ add(); }
        });

        int width = Math.max( add.getPreferredSize().width,
                remove.getPreferredSize().width);
        add.setPreferredSize(new Dimension(width, add.getPreferredSize().height));
        remove.setPreferredSize(new Dimension(width, remove.getPreferredSize().height));
    }
    
    protected void makeGUI(){
        makeButtons();
        
        listModel = new DefaultListModel();
        Set pluginmd = 
            TokenizationService.getTokenizationService().getTokenizerPluginMetadata();
        for (Iterator iter = pluginmd.iterator(); iter.hasNext();) {
            TM tm = (TM)iter.next();
            listModel.addElement(tm);
        }
        list = new JList(listModel){
            public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                TM item = (TM)getModel().getElementAt(index);
                StringBuffer sb = new StringBuffer();
                sb.append("<html>");
                sb.append(item.description);
                sb.append("<br>");
                sb.append("Suitable for locales:<br>");
                for (int ii=0; ii<item.supportedLocales.length; ii++)
                    sb.append("* " + item.supportedLocales[ii].getDisplayName() + "<br>");
                sb.append("</html>");
                return sb.toString();
            }};
        list.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
        if (listModel.size() > 0){
            list.setSelectedIndex(0);
        }
                
        JPanel bpanel = new JPanel(new BorderLayout());
        Box bbox = Box.createVerticalBox();
        bbox.add(Box.createVerticalGlue());
        bbox.add(add);
        bbox.add(Box.createVerticalStrut(5));
        bbox.add(remove);
        bbox.add(Box.createVerticalGlue());
        bpanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
        bpanel.add(bbox, BorderLayout.NORTH);
        
        JPanel ppanel = new JPanel(new BorderLayout());
        ppanel.add(bpanel, BorderLayout.EAST);
        JScrollPane sp = new JScrollPane(list);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        ppanel.add(sp, BorderLayout.CENTER);
        
        add(ppanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }

    protected void add(){
        chooser.show();
        String fname = chooser.getFile();
        if (fname == null) return;

        final File f = new File(chooser.getDirectory(), fname);
        TaskWorker tworker = new TaskWorker(yoshikoder){
            TM tmd;
            protected void doWork() throws Exception {
                tmd = 
                    TokenizationService.getTokenizationService().addTokenizerPlugin(f);
                log.info(tmd.name + "/" + tmd.description + "/" + tmd.classname + "/" + tmd.location);
            }
            protected void onError(){
                if (e instanceof DuplicatePluginException){
                    int resp = DialogUtil.askYesNo(yoshikoder, 
                            "Replace existing plugin with this name?", 
                            "Replace Tokenizer Plugin");
                    if (resp == JOptionPane.YES_OPTION){
                        // replace on the dispatch thread - not ideal
                        try {
                            TM tm = 
                                TokenizationService.getTokenizationService().replaceTokenizerPlugin(tmd,f);
                            listModel.removeElement(tmd);
                            listModel.addElement(tm);
                            list.setSelectedValue(tm, true);
                            return;
                    
                        } catch (PluginException ple){
                            log.log(Level.WARNING, "Failed to replace tokenizer", ple);
                        }
                    } else {
                        // just leave things as they are
                        return;
                    }
                }
                String mess = 
                    Messages.getString("TokenizerPluginsPanel.metadataExtractionFailure");
                DialogUtil.yelp(yoshikoder, mess, new PluginException(mess)); 
            }
            protected void onSuccess() {
                listModel.addElement(tmd);
                list.setSelectedValue(tmd, true);
            }
        };
        tworker.start();
    }
    
    protected void remove(){
        final int index = list.getSelectedIndex();
        if (index == -1) return; // nothing selected
        
        TaskWorker tworker = new TaskWorker(yoshikoder){
            TM tmd = null;
            protected void doWork() throws Exception {
                tmd = (TM)listModel.get(index);
                TokenizationService.getTokenizationService().removeTokenizerPlugin(tmd);
            }
            protected void onError() {
                String mess = Messages.getString("TokenizerPluginsPanel.removeFailure");
                DialogUtil.yelp(yoshikoder, mess, new PluginException(mess));
            }
            protected void onSuccess() {
                listModel.remove(index);
            }
        };
        tworker.start();
    }
    
    public void commit() throws CommitException {
        //
    }
    
}
