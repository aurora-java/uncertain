/*
 * Created on 2011-7-27 下午10:25:57
 * $Id$
 */
package uncertain.cache.action;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

public class CacheClear extends AbstractCacheAction {

    public CacheClear(INamedCacheFactory cacheFactory) {
        super(cacheFactory);
    }

    public void run(ProcedureRunner runner) throws Exception {
        ICache cache = getCache();
        cache.clear();
    }

}
