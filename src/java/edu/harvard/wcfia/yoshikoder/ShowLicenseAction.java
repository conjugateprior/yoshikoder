package edu.harvard.wcfia.yoshikoder;

import java.awt.event.ActionEvent;
import java.io.InputStream;

import edu.harvard.wcfia.yoshikoder.ui.dialog.MessageDialog;
import edu.harvard.wcfia.yoshikoder.util.DialogUtil;
import edu.harvard.wcfia.yoshikoder.util.DialogWorker;

public class ShowLicenseAction extends YoshikoderAction {

    protected String license;
    
    public ShowLicenseAction(Yoshikoder yk) {
        super(yk, ShowLicenseAction.class.getName());
    }

    public void actionPerformed(ActionEvent e) {
        dworker = new DialogWorker(yoshikoder){
            protected void doWork() throws Exception {
                if (license==null){
                    InputStream str = 
                        ShowLicenseAction.class.getClassLoader()
                        .getResourceAsStream("LICENSE.txt");
                    StringBuffer sb = new StringBuffer();
                    int ii;
                    while ((ii = str.read()) != -1){
                        sb.append((char)ii);
                    }
                    license = sb.toString();
                    str.close();
                }
                dia = new MessageDialog(yoshikoder, "License", license);
            }
            protected void onError() {
                DialogUtil.yelp(yoshikoder, "Error: Could not show the Yoshikoder license", e);
            }
        }; 
        dworker.start();
    }

}
