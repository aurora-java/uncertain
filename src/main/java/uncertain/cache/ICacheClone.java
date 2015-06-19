package uncertain.cache;

import java.util.Map;


public interface ICacheClone extends Cloneable,Iterable<Map.Entry<String, Object>>{

	public Object cacheClone();
	
	public void cacheCopy(ICacheClone cache);
}
