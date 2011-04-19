/*
 * Created on 2011-3-29 ä¸??07:09:14
 * $Id$
 */
package uncertain.cache;

public interface ICacheFactory {
    
    public ICacheReader getCacheReader();
    
    public ICacheWriter getCacheWriter();
    
    public ICache   getCache();


}
