/*
 * Created on 2011-6-23 下午12:45:23
 * $Id$
 */
package uncertain.cache.action;

import uncertain.cache.CacheBuiltinExceptionFactory;
import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.proc.AbstractEntry;

public abstract class AbstractCacheAction extends AbstractEntry {
    
    INamedCacheFactory      mCacheFactory;
    String                  mCacheName;
    String                  mCacheKey;
    String                  dataPath;
    
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
            if(cache==null)
                throw CacheBuiltinExceptionFactory.createNamedCacheNotFound(this, mCacheName);
            return cache;
        }else
            return mCacheFactory.getCache();
    }
    
    public String getKey( CompositeMap context ){
        if(mCacheKey==null)
            throw BuiltinExceptionFactory.createAttributeMissing(this, "cacheKey");
        return TextParser.parse(mCacheKey, context);
    }


    public String getDataPath() {
        if(dataPath==null)
            throw BuiltinExceptionFactory.createAttributeMissing(this, "dataPath");
        return dataPath;
    }


    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }



}
