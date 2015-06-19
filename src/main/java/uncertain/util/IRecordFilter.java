/*
 * Created on 2007-12-7
 */
package uncertain.util;

import uncertain.composite.CompositeMap;

public interface IRecordFilter {
    
    /**
     * @param record A CompositeMap instance to test
     * @return whether passed parameter can be accepted
     */
    public boolean accepts( CompositeMap record );

}
