/*
 * Created on 2011-6-23 下午12:45:23
 * $Id$
 */
package uncertain.cache.action;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.proc.AbstractEntry;

public abstract class AbstractCacheAction extends AbstractEntry {
    
    INamedCacheFactory      mCacheFactory;
    String                  mCacheName;
    String                  mCacheKey;    
    
    public String getCacheName() {
        return mCacheName;
    }


    public void setCacheName(String cacheName) {
        this.mCacheName = cacheName;
    }


    public String getCacheKey() {
        return mCacheKey;
    }


    public void setCacheKey(String cacheKey) {
        this.mCacheKey = cacheKey;
    }
    
    /**
     * @param cacheFactory An INamedCacheFactory instance to provide cache lookup
     */
    public AbstractCacheAction(INamedCacheFactory cacheFactory) {
        super();
        this.mCacheFactory = cacheFactory;
    }
    
    public ICache   getCache(){
        if(mCacheName!=null){
            ICache cache = mCacheFactory.getNamedCache(mCacheName);
            return cache;
        }else
            throw new ConfigurationError("Can't get key "+mCacheName);
    }
    
    public String getKey( CompositeMap context ){
        return TextParser.parse(mCacheKey, context);
    }



}
