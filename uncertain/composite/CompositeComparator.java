/*
 * Created on 2006-9-15
 */
package uncertain.composite;

import java.util.Comparator;

public class CompositeComparator implements Comparator {
    
    String    field;
    boolean   ascend = true;

    public CompositeComparator(String field){
        this.field = field;
    }
    
    public CompositeComparator(String field, boolean ascend){
        this.field = field;
        this.ascend = ascend;
    }
    public int compare(Object o1, Object o2){
        CompositeMap m1 = (CompositeMap)o1, m2 = (CompositeMap)o2;
        Object value1 = m1.get(field);
        Object value2 = m2.get(field);
        if(value1==null && value2==null) return 0;
        if(value1==null) return ascend ? -1:1;
        if(value2==null) return ascend ? 1:-1;
        if(value1 instanceof Comparable ){
            int rst = ((Comparable)value1).compareTo(value2);
            return ascend? rst:rst*-1;
        }
        return 0;
    }
}
