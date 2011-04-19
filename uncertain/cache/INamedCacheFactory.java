/*
 * Created on 2011-4-10 обнГ08:43:30
 * $Id$
 */
package uncertain.cache;

public interface INamedCacheFactory extends ICacheFactory {
    
    public ICache   getNamedCache( String name );
    
    public void setNamedCache( String name, ICache cache );

}
