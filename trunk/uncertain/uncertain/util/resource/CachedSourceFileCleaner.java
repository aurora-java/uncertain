/*
 * Created on 2011-6-2 ÏÂÎç02:51:49
 * $Id$
 */
package uncertain.util.resource;

import uncertain.cache.ICache;

public class CachedSourceFileCleaner implements ISourceFileUpdateListener {
    
    ICache      cache;
    Object      key;
    
    /**
     * @param cache An ICache instance that contains cached content associated with source file
     * @param key Key used to save cached content
     */
    public CachedSourceFileCleaner(ICache cache, Object key) {
        super();
        this.cache = cache;
        this.key = key;
    }

    public void onUpdate(ISourceFile source) {
        //System.out.println("Removing "+key);
        cache.remove(key);
    }

}
