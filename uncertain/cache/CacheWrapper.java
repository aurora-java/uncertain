/*
 * Created on 2011-5-19 ÏÂÎç08:19:59
 * $Id$
 */
package uncertain.cache;

public class CacheWrapper implements ICache {
    
    boolean     mEnabled;
    ICache      mCache;
    
    public CacheWrapper(){
        mEnabled = false;
    }
    
    /**
     * @param mEnabled
     * @param mCache
     */
    public CacheWrapper(boolean mEnabled, ICache mCache) {
        this.mEnabled = mEnabled;
        this.mCache = mCache;
    }



    public Object getValue(Object key) {
        return mCache==null?null:mCache.getValue(key);
    }

    public boolean setValue(Object key, Object value) {
        return mCache==null?false:mCache.setValue(key, value);
    }

    public boolean setValue(Object key, int timeout, Object value) {
        return mCache==null?false:mCache.setValue(key, timeout, value);
    }

    public void remove(Object key) {
        if(mCache!=null)
            mCache.remove(key);

    }

    public void clear() {
        if(mCache!=null)
            mCache.clear();
    }
    
    public boolean isEnabled(){
        return mEnabled;
    }

}
