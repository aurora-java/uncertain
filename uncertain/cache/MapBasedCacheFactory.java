/*
 * Created on 2011-4-10 ÏÂÎç08:47:05
 * $Id$
 */
package uncertain.cache;

import java.util.HashMap;
import java.util.Map;

import uncertain.core.UncertainEngine;
import uncertain.mbean.MBeanRegister;

public class MapBasedCacheFactory implements INamedCacheFactory {
    
    Map             mNamedCacheMap = new HashMap(1000);
    MapBasedCache   mDefaultCache = new MapBasedCache();
    boolean         mRegisterMBean = true;
    UncertainEngine mEngine;
    String          mName;
    
    /*
    String          mBeanName;
    
    public void setBeanName(String mBeanName) {
        this.mBeanName = mBeanName;
    }

    public String getBeanName() {
        return mBeanName;
    }

    public String getMBeanName( String cache_name ){
        String name = mBeanName + ",name="+cache_name;
        return name;
    }
    */
    
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public MapBasedCacheFactory(){
        this("MapBasedCacheFactory");
    }
    
    public MapBasedCacheFactory( String name){
        setName(name);
    }
    
    public MapBasedCacheFactory( UncertainEngine engine){
        this.mEngine = engine;
    }

    public ICacheReader getCacheReader() {
        return mDefaultCache;
    }

    public ICacheWriter getCacheWriter() {
        return mDefaultCache;
    }

    public ICache getCache() {
        return mDefaultCache;
    }

    public ICache getNamedCache(String name) {
        if(name==null)
            throw new IllegalArgumentException("Cache name can't be null");
        ICache cache = (ICache)mNamedCacheMap.get(name);
        if(cache==null){
            cache = new MapBasedCache();
            try{
                String mbean_name = mEngine==null?"name="+name:mEngine.getMBeanName("cache", "name="+name);
                MBeanRegister.resiterMBean(mbean_name, cache);
            }catch(Exception ex){
                ex.printStackTrace();
            }
            mNamedCacheMap.put(name, cache);
        }
        return cache;
    }

    public void setNamedCache(String name, ICache cache) {
        mNamedCacheMap.put(name, cache);
    }

    public boolean isCacheEnabled(String name) {
        return true;
    }

}
