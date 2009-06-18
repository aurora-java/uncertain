/**Define general interface for file based log handle
 * Created on 2009-4-20
 */
package uncertain.logging;

import java.io.IOException;

/**
 * Implement this interface so that the framework can set base log path parameter
 * ILogPathSettable
 * @author Zhou Fan
 *
 */
public interface ILogPathSettable {
    
    public String getLogPath();

    /**
     * @param logPath the default log path
     */
    public void setLogPath(String logPath);

}
