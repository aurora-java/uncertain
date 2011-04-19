/*
 * Created on 2011-4-10 ÏÂÎç08:47:05
 * $Id$
 */
package uncertain.cache;

import java.util.HashMap;
import java.util.Map;

import uncertain.mbean.MBeanRegister;

public class MapBasedCacheFactory implements INamedCacheFactory {
    
    Map             mNamedCacheMap = new HashMap(1000);
    MapBasedCache   mDefaultCache = new MapBasedCache();
    
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
    public MapBasedCacheFactory(){
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
        ICache cache = (ICache)mNamedCacheMap.get(name);
        if(cache==null){
            cache = new MapBasedCache();
            try{
                MBeanRegister.resiterMBean(name, cache);
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

}
