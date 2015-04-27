package edu.harvard.wcfia.yoshikoder.dictionary;

public class PatternEngineFactory {

	private PatternEngineFactory(){}
	
	public static PatternEngine createEngine(String type){
		if (type.equals(PatternEngine.SUBSTRING)){
			return new SubstringPatternEngine();
		} else if (type.equals(PatternEngine.REGEXP)){
			return new RegexpPatternEngine();
		} else {
			// default to pattern engine
			return new SubstringPatternEngine();
		}
	}
	
}
