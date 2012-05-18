/*
 * Created on 2012-5-18 下午02:35:27
 * $Id$
 */
package uncertain.cache;

/**
 * Object that can manage cache: initialize cache data, reload
 */
public interface ICacheManager {
    
    /** performs init works, such as load cache data */
    public void initialize();
    
    /** reload cache data */
    public void reload();
    
    /** get ICache instance under management */
    public ICache getCache();
    
    /** name of cache uncer management */
    public String getCacheName();
    
    /** description of cache under management */
    public String getCacheDesc();   
    

}
