/*
 * Created on 2006-11-20
 */
package uncertain.util;
import java.util.HashMap;

/**
 * Implements static string to integer mapping, to handle string constants 
 * that need by be compared frequently. For use in JDK 1.4 environment where
 * enum keyword is not available.
 * StringEnum
 * @author Zhou Fan
 *
 */
public class StringEnum {
    
    public static final int NON_EXIST_VALUE = Integer.MIN_VALUE; 
    
    HashMap     enum_map = new HashMap();
    
    public StringEnum(){
    }
    
    public StringEnum(final String[] strings, final int[] values){
        assert strings.length==values.length;
        for(int i=0; i<strings.length; i++){
            enum_map.put(strings[i], new Integer(values[i]));
        }
    }
    
    public StringEnum(final String[] strings){
        for(int i=0; i<strings.length; i++){
            enum_map.put(strings[i], new Integer(i));
        }        
    }
    
    public int valueOf(String input){
        Integer i = (Integer)enum_map.get(input);
        if(i==null) return NON_EXIST_VALUE;
        return i.intValue();
    }
    
    public boolean valid(int value){
        return value!=NON_EXIST_VALUE;
    }
}
