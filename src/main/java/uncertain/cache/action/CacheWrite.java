/*
 * Created on 2011-6-24 上午12:49:44
 * $Id$
 */
package uncertain.cache.action;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

public class CacheWrite extends AbstractCacheAction {
    
    String  sourcePath;

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    /**
     * @param cacheFactory
     */
    public CacheWrite(INamedCacheFactory cacheFactory) {
        super(cacheFactory);
    }

    public void run(ProcedureRunner runner) throws Exception {
        CompositeMap    context = runner.getContext();
        Object data = context.getObject(sourcePath);
        if(data!=null){
            ICache cache = getCache();
            if(cache==null)
                //throw new GeneralException("");
                // throw exception;
                ;
            else
                cache.setValue( getKey(context), data);
        }
            
    }

}
