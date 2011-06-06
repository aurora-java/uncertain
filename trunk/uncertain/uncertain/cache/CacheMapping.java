/*
 * Created on 2011-5-18 обнГ11:13:29
 * $Id$
 */
package uncertain.cache;

public class CacheMapping {

    String mName;
    String mCacheFactory;
    boolean mEnabled;

    public boolean getEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getCacheFactory() {
        return mCacheFactory;
    }

    public void setCacheFactory(String cacheFactory) {
        this.mCacheFactory = cacheFactory;
    }
    
    

}
