/*
 * Created on 2009-9-2 ÏÂÎç02:24:17
 * Author: Zhou Fan
 */
package uncertain.proc;

import java.io.IOException;

import org.xml.sax.SAXException;

import uncertain.cache.ICache;
import uncertain.cache.MapBasedCache;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.ocm.OCManager;

/**
 * Default IProcedureManager implementation
 */
public class ProcedureManager implements IProcedureManager {
    
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
    
    private void createCache(){
        if(mCache!=null){
            mCache.clear();
            mCache=null;
        }
        if(mUncertainEngine!=null){
            mCache = mUncertainEngine.createNamedCache("ProcedureConfig");
        }else{
            mCache = new MapBasedCache();
        }
    }

    public boolean getIsCache() {
        return mIsCache;
    }

    public void setIsCache(boolean isCache) {
        mIsCache = isCache;
        if(mIsCache)
            createCache();
        else{
            if(mCache!=null){
                mCache.clear();
                mCache = null;
            }
        }
        
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
        throws IOException, SAXException
    {
        CompositeMap map = mCompositeLoader.loadFromClassPath(name);
        if(map==null)
            throw new IOException("Can't load config file "+name);
        return createProcedure(map);
    }
    
    public Procedure loadProcedure(String name) 
    throws IOException, SAXException
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
    
    public void initContext( CompositeMap context ){        
        if(mUncertainEngine!=null)
            mUncertainEngine.initContext(context);
    }
    
    public void destroyContext( CompositeMap context_map ){
        if(mUncertainEngine!=null)
            mUncertainEngine.destroyContext(context_map);
    }

}
