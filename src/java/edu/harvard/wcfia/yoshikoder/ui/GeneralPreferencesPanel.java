package edu.harvard.wcfia.yoshikoder.ui;

import java.awt.Font;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.util.CharsetWrapper;
import edu.harvard.wcfia.yoshikoder.util.FileUtil;
import edu.harvard.wcfia.yoshikoder.util.LocaleWrapper;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class GeneralPreferencesPanel extends CommitablePanel {

    private static Logger log = 
        Logger.getLogger("edu.harvard.wcfia.yoshikoder.ui.GeneralPreferencesPanel");
    
    protected Yoshikoder yoshikoder;
    protected SpinnerNumberModel wsModel;
    protected FontPanel fontPanel;
    
    protected JComboBox charsetCombo;
    protected DefaultComboBoxModel charsetModel;

    protected JComboBox localeCombo;
    
    public GeneralPreferencesPanel(Yoshikoder yk) {
        super();
        yoshikoder = yk;
               
        charsetModel = new DefaultComboBoxModel();
        for (Iterator<CharsetWrapper> iterator = FileUtil.getCharsetList().iterator(); iterator.hasNext();) {
			CharsetWrapper wrapper = iterator.next();
			charsetModel.addElement(wrapper);
		}
        charsetCombo = new JComboBox(charsetModel);
        CharsetWrapper thisone = new CharsetWrapper(yoshikoder.getDefaultEncoding());
        charsetCombo.setSelectedItem(thisone);
        
        localeCombo = new JComboBox(FileUtil.getLocaleList().toArray(new LocaleWrapper[]{}));
        localeCombo.setSelectedItem(new LocaleWrapper(yoshikoder.getDefaultLocale()));
        
        wsModel = 
            new SpinnerNumberModel(yoshikoder.getWindowSize(), 0, 100, 1); 
        JSpinner sp = new JSpinner(wsModel);
        addWidgetInlineFixedWidth(Messages.getString("PreferencesPanel.windowSizeLabel"), sp);
        addWidgetInline("Default encoding:", charsetCombo);
        addWidgetInline("Default locale:", localeCombo);

        fontPanel = new FontPanel(yoshikoder.getDisplayFont());
        addWidgetFixedDepth(Messages.getString("fontLabel"), fontPanel);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }
    
    public void commit() throws CommitException {
        int val = ((Integer)wsModel.getValue()).intValue();
        int ws = yoshikoder.getWindowSize();
        if (ws != val){
            yoshikoder.setWindowSize( val );
            //yoshikoder.setUnsavedChanges(true);
        } 
        Font f = fontPanel.getDisplayFont();
        if (!f.equals(yoshikoder.getDisplayFont())){
            yoshikoder.setDisplayFont(f);
            yoshikoder.setUnsavedChanges(true);
        }
        Charset cs = ((CharsetWrapper)charsetCombo.getSelectedItem()).charset;
        yoshikoder.setDefaultEncoding(cs);
        
        Locale loc = ((LocaleWrapper)localeCombo.getSelectedItem()).locale;
        yoshikoder.setDefaultLocale(loc);
    }
}
