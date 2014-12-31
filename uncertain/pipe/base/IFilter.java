/*
 * Created on 2014年12月15日 下午11:41:23
 * $Id$
 */
package uncertain.pipe.base;

import java.util.Collection;

public interface IFilter {
    
    public Object filt(Object data);
    
    public Collection filtBatch(Collection list);

}
