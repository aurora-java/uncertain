/*
 * Created on 2015年7月6日 下午2:19:16
 * zhoufan
 */
package uncertain.pipe.base;

import java.util.HashMap;
import java.util.Map;

public class DispatchData implements IDispatchData {
    
    String      dispatchResult;
    Map         properties;
    Object      data;

    public DispatchData() {
    }
    
    public DispatchData(Object data) {
        this.data = data;
    }    

    /* (non-Javadoc)
     * @see uncertain.pipe.base.IDispatchData#getDispatchResult()
     */
    public String getDispatchResult() {
        return dispatchResult;
    }

    /* (non-Javadoc)
     * @see uncertain.pipe.base.IDispatchData#getProperties()
     */
    public Map getProperties() {
        return properties;
    }

    /* (non-Javadoc)
     * @see uncertain.pipe.base.IDispatchData#getData()
     */
    public Object getData() {
        return data;
    }

    /* (non-Javadoc)
     * @see uncertain.pipe.base.IDispatchData#setDispatchResult(java.lang.String)
     */
    public void setDispatchResult(String dispatchResult) {
        this.dispatchResult = dispatchResult;
    }

    /* (non-Javadoc)
     * @see uncertain.pipe.base.IDispatchData#setProperties(java.util.Map)
     */
    public void setProperties(Map properties) {
        this.properties = properties;
    }

    /* (non-Javadoc)
     * @see uncertain.pipe.base.IDispatchData#setData(java.lang.Object)
     */
    public void setData(Object data) {
        this.data = data;
    }
    
    /* (non-Javadoc)
     * @see uncertain.pipe.base.IDispatchData#getProperty(java.lang.Object)
     */
    public Object getProperty( Object key ){
        return properties==null?null:properties.get(key);
    }
    
    /* (non-Javadoc)
     * @see uncertain.pipe.base.IDispatchData#setProperty(java.lang.Object, java.lang.Object)
     */
    public void setProperty( Object key, Object value ){
        if(properties==null)
            properties = new HashMap();
        properties.put(key, value);
    }
    
    

}
