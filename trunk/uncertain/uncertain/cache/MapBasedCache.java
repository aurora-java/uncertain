/*
 * Created on 2011-3-29 ä¸??10:06:52
 * $Id$
 */
package uncertain.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uncertain.mbean.MBeanUtil;

public class MapBasedCache implements ICache, MapBasedCacheMBean {
    
    public static final int DEFAULT_CACHE_SIZE = 1000;
    
    String      mName;
    long         mRequestCount = 0;
    long         mHitCount = 0;
    long         mUpdateCount = 0;
    HashMap     mCacheMap;    
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

    public boolean setValue(Object key, long timeout, Object value) {
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

}
