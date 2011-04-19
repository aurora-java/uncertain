/*
 * Created on 2011-4-13 обнГ10:25:40
 * $Id$
 */
package uncertain.cache;

import java.util.Date;
import java.util.HashMap;

public interface MapBasedCacheMBean {
    
    public String getName();
    
    public void clear();
    
    public long getRequestCount();

    public long getHitCount();
    
    public float getHitRate();
    
    public long getUpdateCount();
    
    public int getSize();
    
    public Date getCreationDate();
    
    public String dumpMappings();
    
    public Object getValue(Object key);

}
