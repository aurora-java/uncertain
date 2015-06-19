/*
 * Created on 2011-4-26 ÏÂÎç04:30:00
 * $Id$
 */
package uncertain.mbean;

import java.util.Iterator;
import java.util.Map;

public class MBeanUtil {
    
    public static String dumpMap( Map map ){
        StringBuffer buf = new StringBuffer();
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            String key = entry.getKey()==null?"null":entry.getKey().toString();
            String value = entry.getValue()==null?"null":entry.getValue().toString();
            buf.append(key).append("=").append(value).append("\r\n");
        }
        return buf.toString();
    }

}
