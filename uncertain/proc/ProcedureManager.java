/*
 * Created on 2009-9-2 ����02:24:17
 * Author: Zhou Fan
 */
package uncertain.proc;

import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.ocm.OCManager;

/**
 * Default IProcedureManager implementation
 */
public class ProcedureManager implements IProcedureManager {
    
    public static final String CACHE_NAME = "ProcedureConfig";

    static final ProcedureManager DEFAULT_INSTANCE = new ProcedureManager();
    
    public static ProcedureManager getDefaultInstance(){
        return DEFAULT_INSTANCE;
    }
    
    public ProcedureManager(){
        mOCManager = OCManager.getInstance();
        mCompositeLoader = CompositeLoader.createInstanceForOCM(DEFAULT_PROC_EXTENSION);
    }
    
    /**
     * @param uncertainEngine
     */
    public ProcedureManager(UncertainEngine uncertainEngine) {
        super();
        mUncertainEngine = uncertainEngine;
        mOCManager = mUncertainEngine.getOcManager();
        mCompositeLoader = CompositeLoader.createInstanceForOCM(DEFAULT_PROC_EXTENSION);
//        setIsCache(uncertainEngine.getCacheConfigFiles());
    }
    
    public static final String DEFAULT_PROC_EXTENSION = "proc";
    
    UncertainEngine mUncertainEngine;
    OCManager       mOCManager;
    CompositeLoader mCompositeLoader;
    boolean         mIsCache = false;
    ICache          mCache;

    public boolean getIsCache() {
        return mIsCache;
    }

    public void setIsCache(boolean isCache) {
        mIsCache = isCache;
    }

    public ICache getCache() {
        return mCache;
    }

    public void setCache(ICache cache) {
        this.mCache = cache;
    }

    public Procedure createProcedure() {
        Procedure proc = new Procedure();
        return proc;
    }

    public Procedure createProcedure(CompositeMap proc_config) {
        Procedure proc = (Procedure)mOCManager.createObject(proc_config);
        return proc;
    }

    public ProcedureRunner createProcedureRunner() {
        ProcedureRunner runner = new ProcedureRunner();
        return runner;
    }

    private Procedure loadProcedureNC(String name) 
    {
        CompositeMap map = mCompositeLoader.silently().loadFromClassPath(name);
        return createProcedure(map);
    }
    
    public Procedure loadProcedure(String name) 
    {
        if(!mIsCache || mCache==null)
            return loadProcedureNC(name);
        Procedure proc = (Procedure)mCache.getValue(name);
        if(proc==null){
            proc = loadProcedureNC(name);
            mCache.setValue(name, proc);
        }
        return proc;
    }
    
    public Configuration createConfig(){
        return mUncertainEngine==null? new Configuration() : mUncertainEngine.createConfig();
    }

    public CompositeLoader getCompositeLoader() {
        return mCompositeLoader;
    }

    public OCManager getOCManager() {
        return mOCManager;
    }
    
    public void onInitialize(){
    	INamedCacheFactory cacheFactory = (INamedCacheFactory)mUncertainEngine.getObjectRegistry().getInstanceOfType(INamedCacheFactory.class);
    	if(cacheFactory != null){
    		  mCache = cacheFactory.getNamedCache(CACHE_NAME);
    	}
//        mCache = CacheFactoryConfig.getNamedCache(mUncertainEngine.getObjectRegistry(), CACHE_NAME);
        if(mCache!=null){
            setCache(mCache);
            setIsCache(true);
        }else
            setIsCache(false);
    }
    
    public void initContext( CompositeMap context ){        
        if(mUncertainEngine!=null)
            mUncertainEngine.initContext(context);
    }
    
    public void destroyContext( CompositeMap context_map ){
        if(mUncertainEngine!=null)
            mUncertainEngine.destroyContext(context_map);
    }

}
