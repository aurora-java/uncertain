/*
 * Created on 2011-4-10 ����08:43:30
 * $Id$
 */
package uncertain.cache;

public interface INamedCacheFactory extends ICacheFactory {
    
    /** name of this factory. Every cache factory must has a name for assignment */
    public String   getName();
    
    public boolean isCacheEnabled( String name );
    
    public ICache   getNamedCache( String name );
    
    public void setNamedCache( String name, ICache cache );

}
