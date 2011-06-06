/*
 * Created on 2011-3-29 ä¸??04:19:05
 * $Id$
 */
package uncertain.cache;

public interface ICacheWriter {
    
    public boolean setValue( Object key, Object value );
    
    public boolean setValue( Object key, int timeout, Object value );
    
    public void remove( Object key );
    
    public void clear();

}
