package edu.harvard.wcfia.yoshikoder.dictionary;

public class DemoDictionary extends SimpleDictionary {

    public DemoDictionary() {
        super();
        try {
            CategoryNode root = getDictionaryRoot();
            CategoryNode n = new CategoryNodeImpl("Animal", null, null);
            addCategory(n, root);
            PatternNode p = new PatternNodeImpl("cat", null, null);
            addPattern(p, n);
            p = new PatternNodeImpl("dog", null, null);
            addPattern(p, n);
            p = new PatternNodeImpl("rat", null, null);
            addPattern(p, n);
            
            n = new CategoryNodeImpl("Vegetable", null, null);
            addCategory(n, root);
            p = new PatternNodeImpl("tomato", null, null);
            addPattern(p, n);
            p = new PatternNodeImpl("potato", null, null);
            addPattern(p, n);
            p = new PatternNodeImpl("cauliflower", null, null);
            addPattern(p, n);
            p = new PatternNodeImpl("carrot", null, null);
            addPattern(p, n);
            
            n = new CategoryNodeImpl("Mineral", null, null);
            addCategory(n, root);
            p = new PatternNodeImpl("iron", null, null);
            addPattern(p, n);
            p = new PatternNodeImpl("copper", null, null);
            addPattern(p, n);
            
        } catch (Exception e){
            //
        }
    }
}
