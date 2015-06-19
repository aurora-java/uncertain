/*
 * Created on 2011-4-28 обнГ04:47:17
 * $Id$
 */
package uncertain.cache;

/**
 * Defines general operation for Multi-Segment-Key feature
 * 
 */
public interface IMultiSegmentKeyCache {
    
    /** Set value via multi-segment key */
    public void setValueMSK( Object[] segments, Object value );
    
    /** remove all value whose key is related to specified segment value */
    public void expireValueMSK( Object segment );
    
    /** batch remove all value whose key is related to specified segments */
    public void expireValueMSKinBatch( Object[] segments );

}
