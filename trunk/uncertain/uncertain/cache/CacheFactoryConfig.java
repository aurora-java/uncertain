/*
 * Created on 2011-5-5 ����10:30:42
 * $Id$
 */
package uncertain.cache;

import java.util.HashMap;
import java.util.Map;

import uncertain.core.ConfigurationError;
import uncertain.core.IGlobalInstance;
import uncertain.core.ILifeCycle;
import uncertain.ocm.IObjectRegistry;

public class CacheFactoryConfig implements INamedCacheFactory, ILifeCycle {

    
    public static ICache getNamedCache( IObjectRegistry reg, String name ){
        INamedCacheFactory fact = (INamedCacheFactory)reg.getInstanceOfType(INamedCacheFactory.class);
        if(fact!=null){
            if(!fact.isCacheEnabled(name))
                return null;
            else
                return fact.getNamedCache(name);
        }else
            return null;
    }
    
    private static final CacheWrapper NOT_ENABLED_CACHE = new CacheWrapper();
    
    String                  mName;


    INamedCacheFactory      mDefaultCacheFactory;
    String                  mDefaultCacheFactoryName;
    INamedCacheFactory[]    mNamedCacheFactoryArray;
    CacheMapping[]          mCacheMappingArray;
    
    // factory name -> factory instance
    Map                 mCacheFactoryMap = new HashMap();
    // cache name -> factory instance
    Map                 mPredefinedCacheMap = new HashMap();
    
    public CacheFactoryConfig(){
        
    }
    
    public CacheFactoryConfig( IObjectRegistry reg ){
        reg.registerInstance(INamedCacheFactory.class, this);
        reg.registerInstance(ICacheFactory.class, this);
    }
    

    public String getDefaultCacheFactory() {
        return mDefaultCacheFactoryName;
    }

    public void setDefaultCacheFactory(String mDefaultCacheFactory) {
        this.mDefaultCacheFactoryName = mDefaultCacheFactory;
    }

    public ICacheReader getCacheReader() {
        return mDefaultCacheFactory==null?null:mDefaultCacheFactory.getCacheReader();
    }

    public ICacheWriter getCacheWriter() {
        return mDefaultCacheFactory==null?null:mDefaultCacheFactory.getCacheWriter();
    }

    public ICache getCache() {
        return mDefaultCacheFactory==null?null:mDefaultCacheFactory.getCache();
    }

    public String getName() {
        return mName;
    }
    
    public void setName(String name) {
        this.mName = name;
    }    

    public ICache getNamedCache(String name) {
        Object o = mPredefinedCacheMap.get(name);
        if(o==null)
            return mDefaultCacheFactory==null?null:mDefaultCacheFactory.getNamedCache(name);
        else{
            if(NOT_ENABLED_CACHE.equals(o))
                return null;
            else
                return ((INamedCacheFactory)o).getNamedCache(name);
        }
    }
/*
    public void setNamedCache(String name, ICache cache) {
        if(mDefaultCacheFactory!=null)
            mDefaultCacheFactory.setNamedCache(name, cache);
    }
*/    
    public void addCacheFactories( INamedCacheFactory[] factories){
        mNamedCacheFactoryArray = factories;
    }
    
    public void addCacheMappings( CacheMapping[] mappings){
        mCacheMappingArray = mappings;
    }
    
    public boolean startup() {
        for( int i=0; i<mNamedCacheFactoryArray.length; i++)
            mCacheFactoryMap.put(mNamedCacheFactoryArray[i].getName(), mNamedCacheFactoryArray[i]);
        for( int i=0; i<mCacheMappingArray.length; i++){
            CacheMapping cm = mCacheMappingArray[i];
            INamedCacheFactory fact = (INamedCacheFactory)mCacheFactoryMap.get(cm.getCacheFactory());
            if(fact==null)
                throw new ConfigurationError("Can't find cache factory named "+cm.getCacheFactory());
            if(!cm.getEnabled()){
                mPredefinedCacheMap.put(cm.getName(), NOT_ENABLED_CACHE);
            }else{
                mPredefinedCacheMap.put(cm.getName(), fact);
            }
        }
        if(mDefaultCacheFactoryName!=null){
            mDefaultCacheFactory = (INamedCacheFactory)mCacheFactoryMap.get(mDefaultCacheFactoryName);
            if(mDefaultCacheFactory==null)
                throw new ConfigurationError("Can't find cache factory named "+mDefaultCacheFactoryName);
        }
        return true;
    }

    public boolean isCacheEnabled(String name) {
        Object o = mPredefinedCacheMap.get(name);
        if(o==null)
            return mDefaultCacheFactory==null?false:mDefaultCacheFactory.isCacheEnabled(name);
        else{
            if(NOT_ENABLED_CACHE.equals(o))
                return false;
            else
                return true;
        }
    }
    
    public void shutdown(){
        if(mNamedCacheFactoryArray==null)
            return;
        for(int i=0; i<mNamedCacheFactoryArray.length; i++){
            Object o = mNamedCacheFactoryArray[i];
            if(o instanceof ILifeCycle){
                ILifeCycle s = (ILifeCycle)o;
                s.shutdown();
            }
        }
        
    }
    /*
    public void onShutdown(){
        shutdown();
    }
    */

}
