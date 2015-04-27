package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class PreferencePanel extends CommitablePanel {

    protected Yoshikoder yoshikoder;
    protected JTabbedPane tabs;
    protected GeneralPreferencesPanel general;
    protected TokenizerPluginsPanel tokenizers;
    
    public PreferencePanel(Yoshikoder yk){
        super();
        setLayout(new BorderLayout());        
        
        yoshikoder = yk;
        tabs = new JTabbedPane();
        
        general = new GeneralPreferencesPanel(yoshikoder);
        tabs.addTab(Messages.getString("PreferencePane.generalTabLabel"), null, general, 
                Messages.getString("PreferencePane.generalTabTooltip"));
        tokenizers = new TokenizerPluginsPanel(yoshikoder);
        tabs.addTab(Messages.getString("PreferencePane.tokenizersTabLabel"), null, tokenizers, 
                Messages.getString("PreferencePane.tokenizersTabTooltip"));
        
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(tabs, BorderLayout.CENTER);
    }
    
    public void commit() throws CommitException {
        general.commit();
    }

}
