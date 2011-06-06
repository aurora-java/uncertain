/*
 * Created on 2011-4-10 ÏÂÎç10:27:42
 * $Id$
 */
package uncertain.mbean;

import java.io.File;
import java.util.logging.Level;


public interface UncertainEngineWrapperMBean {
    
    public String getName();
    
    public void setName( String name );
    
    public String getLogPath();
    
    public void setLogPath(String path);
    
   // public boolean getCacheConfigFiles();  

   // public void setCacheConfigFiles(boolean cacheConfigFiles);
    
    public String getDefaultLogLevel();

    public void setDefaultLogLevel(String defaultLogLevel);
    
    public boolean getIsRunning();
    
    //public void shutdown();
    
    public File getConfigDirectory();
    
    public String dumpInstanceMapping();


}
