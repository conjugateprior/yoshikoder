package edu.harvard.wcfia.yoshikoder.ui;

import java.util.regex.Pattern;

import javax.swing.JTextField;

import edu.harvard.wcfia.yoshikoder.Yoshikoder;
import edu.harvard.wcfia.yoshikoder.dictionary.CategoryNode;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNode;
import edu.harvard.wcfia.yoshikoder.dictionary.PatternNodeImpl;
import edu.harvard.wcfia.yoshikoder.util.Messages;

public class NewPatternPanel extends CommitablePanel {
    
    protected JTextField name;
    protected JTextField score;
    
    protected Yoshikoder yoshikoder;
    protected CategoryNode parent;
    
    public NewPatternPanel(Yoshikoder yk, CategoryNode parentnode){
        super();
        yoshikoder = yk;
        parent = parentnode;
        makeGUI();
    }
    
    public void commit() throws CommitException {
        // wrap all exceptions
        try {
            if (name.getText() == null || 
                    name.getText().length()==0){
                throw new Exception(Messages.getString("noEntryName")); 
            }
            Pattern p = 
                yoshikoder.getDictionary().getPatternEngine()
                .makeRegexp(name.getText());
            
            Double d = getScore();
            PatternNode node = 
                new PatternNodeImpl(name.getText(), d, p);
            yoshikoder.getDictionary().addPattern(node, parent);
            yoshikoder.setSelectedNode(parent);
            yoshikoder.setUnsavedChanges(true);
            
        } catch (Exception ex){
            throw new CommitException(ex.getMessage(), ex);
        }
    }
    
    protected void makeGUI(){
        name = new JTextField(15);
        name.setFont(yoshikoder.getDisplayFont());
        name.addKeyListener(keyListener);
        addField(Messages.getString("nameLabel"), name); 
        
        //score = new JTextField(5);
        //score.addKeyListener(keyListener);
        //addWidgetInlineFixedWidth(Messages.getString("scoreLabel"), score); 
    }
    
    public void setName(String n){
        name.setText(n);
    }
    
    public String getName(){
        return name.getText();
    }
    
    public void setScore(Double d){
        score.setText(d.toString());
    }
    
    public Double getScore() throws Exception {
        /*
    	if (score.getText() != null && score.getText().length()>0){
            double d = Double.parseDouble(score.getText());
            return new Double(d);
        }
        */
        return null;
    }
}
