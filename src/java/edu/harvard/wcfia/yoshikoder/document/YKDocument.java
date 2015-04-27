package edu.harvard.wcfia.yoshikoder.document;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

public interface YKDocument extends Serializable{

	public String getText() throws IOException;

	public String getTitle();
	public void setTitle(String title);

	public File getLocation();
	public void setLocation(File f);
		
	public String getCharsetName();
	public void setCharsetName(String csname);

	public Locale getLocale();
	public void setLocale(Locale loc);
	
	public Font getPreferredFont();
	public void setPreferedFont(Font f);
}
