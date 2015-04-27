package edu.harvard.wcfia.yoshikoder.util;

import java.util.Locale;

public class LocaleWrapper implements Comparable<LocaleWrapper>{

	public Locale locale;
	public LocaleWrapper(Locale l){
		locale = l;
	}
	public String toString(){
		return locale.getDisplayName();
	}

	@Override
	public int compareTo(LocaleWrapper o) {
		return this.toString().compareTo(o.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LocaleWrapper)
			return this.toString().equals(obj.toString());
		return false;
	}
}
