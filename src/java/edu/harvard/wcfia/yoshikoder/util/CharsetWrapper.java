package edu.harvard.wcfia.yoshikoder.util;

import java.nio.charset.Charset;

public class CharsetWrapper implements Comparable<CharsetWrapper>{

	public Charset charset;
	public CharsetWrapper(Charset c) {
		charset = c;
	}

	public String toString(){
		return charset.name(); // canonical name
	}

	@Override
	public int compareTo(CharsetWrapper o) {
		return charset.compareTo(o.charset);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharsetWrapper)
			return charset.equals( ((CharsetWrapper)obj).charset );
		return false;
	}
}
