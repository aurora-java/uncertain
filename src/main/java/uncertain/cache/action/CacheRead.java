/*
 * Created on 2011-6-26 下午10:43:06
 * $Id$
 */
package uncertain.cache.action;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;

public class CacheRead extends AbstractCacheAction {
    
    boolean         writeCacheOnMissing = true;
    
    Procedure       mProcedure;

    public CacheRead(INamedCacheFactory cacheFactory) {
        super(cacheFactory);
    }

    public void run(ProcedureRunner runner) throws Exception {
        CompositeMap context = runner.getContext();
        String key = getKey(context);
        String path = getDataPath();
        ICache cache = getCache();
        
        Object data = cache.getValue(key);
        if(data==null){
            if( mProcedure!=null){
                runner.run(mProcedure);
                data = context.getObject(dataPath);
                if(data!=null && writeCacheOnMissing)
                    cache.setValue(key, data);
            }
        }else{
            context.putObject(path, data, true);
        }
    }
    
    public void addProcedure( Procedure proc ){
        mProcedure = proc;
    }
    
    public Procedure getProcedure(){
        return mProcedure;
    }

}
