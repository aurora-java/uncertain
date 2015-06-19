/*
 * Created on 2011-7-13 下午02:07:32
 * $Id$
 */
package uncertain.cache;

import uncertain.exception.GeneralException;
import uncertain.util.resource.ILocatable;

public class CacheBuiltinExceptionFactory {
    
    public static GeneralException createNamedCacheNotFound( ILocatable locatable, String cache_name){
        throw new GeneralException("uncertain.cache.named_cache_not_found", new Object[]{cache_name}, locatable );
    }

}
