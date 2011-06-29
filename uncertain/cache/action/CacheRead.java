/*
 * Created on 2011-6-26 下午10:43:06
 * $Id$
 */
package uncertain.cache.action;

import uncertain.cache.INamedCacheFactory;
import uncertain.proc.ProcedureRunner;

public class CacheRead extends AbstractCacheAction {
    
    boolean writeCacheOnMissing = true;

    public CacheRead(INamedCacheFactory cacheFactory) {
        super(cacheFactory);
        // TODO Auto-generated constructor stub
    }

    public void run(ProcedureRunner runner) throws Exception {
        // TODO Auto-generated method stub

    }
    
    public void addCacheMissingProcedure( CacheMissingProcedure proc ){
        
    }

}
