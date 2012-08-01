/*
 * Created on 2012-8-1 下午12:24:15
 * $Id$
 */
package uncertain.cache;

import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.GeneralException;
import uncertain.ocm.IObjectRegistry;

public class CacheUtil {
    
    public static final String KEY_NAMED_CACHE_NOT_FOUND = "uncertain.cache.named_cache_not_found";
    
    public static GeneralException createNamedCacheNotFound( String cache_name ){
        return new GeneralException(KEY_NAMED_CACHE_NOT_FOUND, new Object[]{cache_name}, (Throwable)null );
    }
    
    public static ICache getNamedCache( IObjectRegistry reg, String cache_name ){
        INamedCacheFactory fact = (INamedCacheFactory)reg.getInstanceOfType(INamedCacheFactory.class);
        if(fact==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(null, INamedCacheFactory.class);
        ICache cache_for_data = fact.getNamedCache(cache_name);
        if(cache_for_data==null)
            throw createNamedCacheNotFound(cache_name);
        return cache_for_data;
    }

}
