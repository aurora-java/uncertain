/*
 * Created on 2011-3-29 �??10:06:52
 * $Id$
 */
package uncertain.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import uncertain.mbean.MBeanUtil;

public class MapBasedCache implements ICache, MapBasedCacheMBean,ICacheClone{
    
    public static final int DEFAULT_CACHE_SIZE = 1000;
    
    String      mName;
    long         mRequestCount = 0;
    long         mHitCount = 0;
    long         mUpdateCount = 0;
    HashMap    mCacheMap;    
    Date        mCreationDate = new Date();
    
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
    
    public MapBasedCache(){
        mCacheMap = new HashMap(DEFAULT_CACHE_SIZE);
    }
    
    public MapBasedCache(HashMap map){
        mCacheMap = map;
    }
    
    public MapBasedCache(int capacity){
        mCacheMap = new HashMap( capacity);
    }


    public MapBasedCache(int capacity, float load_factor) {
        mCacheMap = new HashMap(capacity, load_factor);
    }

    public boolean setValue(Object key, Object value) {
        mCacheMap.put(key,value);
        mUpdateCount++;
        return true;
    }

    public boolean setValue(Object key, int timeout, Object value) {
        setValue(key, value);
        return true;
    }

    public void remove(Object key) {
        mCacheMap.remove(key);
    }

    public Object getValue(Object key) {
        mRequestCount++;
        Object v = mCacheMap.get(key);
        if(v!=null)
            mHitCount++;
        return v;
    }
    
    public void clear(){
        mCacheMap.clear();
        mHitCount = 0;
        mUpdateCount = 0;
        
    }
    
    public long getRequestCount() {
        return mRequestCount;
    }

    public long getHitCount() {
        return mHitCount;
    }
    
    public float getHitRate(){
        return mRequestCount==0?0:(float)mHitCount/(float)mRequestCount;
    }

    public long getUpdateCount() {
        return mUpdateCount;
    }    
    
    public int getSize(){
        return mCacheMap.size();
    }
    
    public Date getCreationDate(){
        return mCreationDate;
    }
    
    public String dumpMappings(){
        return MBeanUtil.dumpMap(mCacheMap);
    }

	public Object cacheClone() {
		return new MapBasedCache((HashMap)mCacheMap.clone());
	}

	public Iterator iterator() {
		return mCacheMap.entrySet().iterator();
	}
	
	public Map getMap(){
		return mCacheMap;
	}

	public void cacheCopy(ICacheClone cache) {
		if(cache instanceof MapBasedCache){
			mCacheMap.putAll(((MapBasedCache)cache).getMap());
		}else{
			Iterator<Entry<String, Object>> it = cache.iterator();
			 if(it != null){
				 for(;it.hasNext();){
					 Entry entry = it.next();
					 mCacheMap.put(entry.getKey(), entry.getValue());
				 }
			 }
		}
	}

}
