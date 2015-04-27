package edu.harvard.wcfia.yoshikoder.document;

import java.io.File;
import java.util.Locale;

public class YKDocumentFactory {
	
	private YKDocumentFactory(){}
	
	/*
	// make ordinary documents
	public static YKDocument createYKDocument(String title, String contents){
		YKDocument li = new YKDocumentImpl(title, contents);
		return li;
	}

	public static YKDocument createYKDocument(String title, String contents,
											  String csname){
		YKDocument li = new YKDocumentImpl(title, contents, csname);
		return li;
	}
	*/
	
	// make lazy documents
	public static YKDocument createYKDocument(File f){
		YKDocument li = new LazyYKDocument(f.getName(), f);
		return li;
	}

	public static YKDocument createYKDocument(File f, String title){
		YKDocument li = new LazyYKDocument(title, f);
		return li;
	}
	
	public static YKDocument createYKDocument(File f, String title,
											  String csname, Locale loc){ 
		YKDocument li = new LazyYKDocument(title, f, csname);
		li.setLocale(loc);
		return li;
	}
	
	public static YKDocument createDummyDocument(String title, 
	        String txt, String csname){
	    YKDocument doc = new YKDocumentImpl(title, txt, csname);
	    return doc;
	}
}
