/*
 * Created on 2015年7月6日 下午3:42:53
 * zhoufan
 */
package uncertain.pipe.base;

import java.util.Map;

public interface IDispatchData {

    String getDispatchResult();

    Map getProperties();

    Object getData();

    void setDispatchResult(String dispatchResult);

    void setProperties(Map properties);

    void setData(Object data);

    Object getProperty(Object key);

    void setProperty(Object key, Object value);

}