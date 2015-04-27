package edu.harvard.wcfia.yoshikoder.document.tokenizer;

import java.util.Iterator;

public class SpanList {

    protected int[][] spans;
    
    class Iter implements Iterator{
        int index = -1;
  
        Iter(){
            if (spans != null) index = 0;
        }
        
        public boolean hasNext() { return (index > -1 && index < spans.length); }

        public Object next() { return spans[index++]; }

        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from inside a SpanList iterator");
        }
    };
    
    public SpanList(int [][] sp){
        spans = sp;
    }
    
    public SpanList(){
        //
    }
    
    public void addSpans(int[][] moreSpans){
        if (moreSpans == null || moreSpans.length == 0)
            return;
        else {
            int slength = 0;
            if (spans != null && spans.length > 0)
                slength = spans.length;
            
            int[][] ns = new int[slength + moreSpans.length][2];
            for (int ii = 0; ii < slength; ii++)
                ns[ii] = spans[ii];
            for (int ii = 0; ii < moreSpans.length; ii++)
                ns[ii+slength] = moreSpans[ii];
            spans = ns;
        }
    }
    
    public Iterator iterator(){
        return new Iter();
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = iterator(); iter.hasNext();) {
            int[] span = (int[]) iter.next();
            sb.append("[");
            sb.append(span[0]);
            sb.append(",");
            sb.append(span[1]);
            sb.append("]\n");
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
        int[][] sp = new int[][]{{1,2},{3,4}};
        SpanList sl = new SpanList(sp);
        sp[0][0] = 9;
        sl.addSpans(sp);
        System.out.println(sl);
    }

}
