/*
 * Created on 2011-4-28 ����04:56:56
 * $Id$
 */
package uncertain.cache;

import java.util.Collection;

public class MultiSegmentKeyCacheImpl implements IMultiSegmentKeyCache {
    
    String      mName;
    ICache      mSegmentIndex;
    ICache      mActualCache;
    
    public static String getFullKey( Object[] segments ){
        StringBuffer full_key = new StringBuffer();
        for(int i=0; i<segments.length; i++){
            full_key.append('[').append(segments[i]).append(']');
        }
        return full_key.toString();
    }
    /*
    private void associateKey( Object segment, Object full_key ){
        Collection cl = (Collection)mSegmentIndex.getValue(segment);
        if(cl==null){
            //synchronized()
        }
    }
    */
    
    public void setValueMSK(Object[] segments, Object value) {
        // TODO Auto-generated method stub
        
    }
    
    public void expireValueMSK(Object segment) {
        // TODO Auto-generated method stub
        
    }
    
    public void expireValueMSKinBatch(Object[] segments) {
        // TODO Auto-generated method stub
        
    }

}
