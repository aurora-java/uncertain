/*
 * CollectionHandle.java
 *
 * Created on 2001年12月13日, 下午3:57
 */

package uncertain.util;

import java.util.List;
import java.util.Vector;
/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class CollectionHandle implements StringSplitHandle {
    
    List strings;
    
    public CollectionHandle(){
        strings  = new Vector();
    }
    
    public CollectionHandle( Class cls){
        try{
        strings = (List) (cls.newInstance());
        } catch(Exception ex){
        strings  = new Vector();        
        }
    }
       
        
        public void processString( String new_str){
            strings.add( new_str);
        }
        
        public List getStrings(){ return strings; }
        
        public static CollectionHandle newInstance(){ return new CollectionHandle(); }
    }
    