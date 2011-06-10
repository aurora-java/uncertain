/*
 * Created on 2011-5-23 ä¸??05:05:59
 * $Id$
 */
package uncertain.util;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalUtil {
    
    static Map      THREAD_LOCAL_MAP = new HashMap();
    
    public synchronized static void put( String key, Object value ){
        ThreadLocal tl = (ThreadLocal)THREAD_LOCAL_MAP.get(key);
        if(tl==null){
            tl = new ThreadLocal();
            THREAD_LOCAL_MAP.put(key, tl);
        }
        tl.set(value);
    }
    
    public synchronized static void append( String key, String msg ){
        ThreadLocal tl = (ThreadLocal)THREAD_LOCAL_MAP.get(key);
        if(tl==null){
            tl = new ThreadLocal();
            THREAD_LOCAL_MAP.put(key, tl);
        }
        StringBuffer buf = (StringBuffer)tl.get();
        if(buf==null){
            buf=new StringBuffer();
            tl.set(buf);
        }
        buf.append(msg).append("\n");
    }
    
    public synchronized static void appendDebugInfo(String msg){
        append("DEBUG", msg);
    }
    
    public static String getDebugInfo(){
        Object v = get("DEBUG");
        return v==null?null:v.toString();
    }
    
    public static Object get( String key ){
        ThreadLocal tl = (ThreadLocal)THREAD_LOCAL_MAP.get(key);
        if(tl==null)
            return null;
        return tl.get();
    }
    
    public synchronized static void remove( String key ){
        ThreadLocal tl = (ThreadLocal)THREAD_LOCAL_MAP.get(key);
        if(tl!=null){
            tl.remove();
        }
    }
    
    public synchronized static void removeDebugInfo(){
        remove("DEBUG");
    }

}
