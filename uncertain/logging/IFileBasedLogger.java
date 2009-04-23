/**Define general interface for file based log handle
 * Created on 2009-4-20
 */
package uncertain.logging;

import java.io.IOException;

public interface IFileBasedLogger {
    
    public void setBasePath( String path );
    
    public String[] getFileList();
    
    public void prepareLogFile() throws IOException;

}
