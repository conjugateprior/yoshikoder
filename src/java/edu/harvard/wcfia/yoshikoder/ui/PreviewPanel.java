package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.harvard.wcfia.yoshikoder.util.CharsetWrapper;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.TaskWorker;

public class PreviewPanel extends JPanel {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.ui.PreviewPanel");
    
    protected JComboBox encodings;
    protected JTextArea text;
    protected byte[] bytes;
    
    public PreviewPanel(byte[] b, Charset cs){
        super(new BorderLayout());

        bytes = b;
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (CharsetWrapper wrapper : FileUtil.getCharsetList()) {
			model.addElement(wrapper);
		}
        encodings = new JComboBox(model);
        
        text = new JTextArea(20, 30);
        text.setEditable(false);
        try {
            String ns = new String(bytes, FileUtil.systemEncoding);
            text.setText(ns);
            text.setCaretPosition(0);
        } catch (UnsupportedEncodingException uee){
            log.warning("couldn't decode bytes as system encoding");
        }
        encodings.setSelectedItem(new CharsetWrapper(cs));
        encodings.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                TaskWorker tworker = new TaskWorker(PreviewPanel.this.getTopLevelAncestor()){
                    String ns;
                    protected void doWork() throws Exception {
                        Charset newEnc = ((CharsetWrapper)encodings.getSelectedItem()).charset;
                        ns = new String(bytes, newEnc);
                    }
                    protected void onError() {}
                    protected void onSuccess() {
                        text.setText(ns); // actually *this* is the slow bit...
                        text.setCaretPosition(0);                         
                    }
                };
                tworker.start();
            }});
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel enc = new JPanel(new BorderLayout());
        enc.add(encodings, BorderLayout.CENTER);
        enc.add(new JLabel("Encoding: "), BorderLayout.WEST);
        enc.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        add(new JScrollPane(text), BorderLayout.CENTER);
        add(enc, BorderLayout.SOUTH);
    }        
        
    public Charset getSelectedEncoding(){
    	return ((CharsetWrapper)encodings.getSelectedItem()).charset;
    }
    
    public static void main(String[] args) throws Exception {
        File f = new File("/Users/will/Desktop/testfile.txt");
        PreviewPanel ppanel = new PreviewPanel(FileUtil.getBytes(f, 1000), Charset.forName("UTF-8"));
        int resp = JOptionPane.showConfirmDialog(null, ppanel, "Preview", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        System.out.println(resp);
        System.exit(0);
    }
    
}

