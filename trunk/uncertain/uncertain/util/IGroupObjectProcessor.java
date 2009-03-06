/*
 * Created on 2005-11-17
 */
package uncertain.util;

/**
 * IGroupObjectProcessor
 * @author Zhou Fan
 * 
 */
public interface IGroupObjectProcessor {
    
    public static final int SUM = 0;
    public static final int AVG = 1;
    public static final int MAX = 2;
    public static final int MIN = 3;
    public static final int COUNT = 4;
    public static final int FIRST = 5;
    public static final int LAST = 6;
    
    public static final String[] GROUP_FUNCITONS = {
            "sum","avg","max","min","count","first","last"
    };

    public void process(Object o);
    
    public Object getObject();
    
    public double getNumericValue();

}
