/*
 * Created on 2009-6-8
 */
package uncertain.core;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class DirectoryConfig extends DynamicObject {
    
    public static final String KEY_LOG_DIRECTORY = "logdirectory";
    
    public static final String KEY_CONFIG_DIRECTORY = "configdirectory";
    
    public static final String KEY_BASE_DIRECTORY = "basedirectory";
    
    public static DirectoryConfig createDirectoryConfig(){
        CompositeMap dir_config = new CompositeMap("dir-config");
        DirectoryConfig dir = new DirectoryConfig();
        dir.initialize(dir_config);
        return dir;
    }
    
    public String getLogDirectory(){
        return getString(KEY_LOG_DIRECTORY);
    }
    
    public void setLogDirectory(String dir){
        putString(KEY_LOG_DIRECTORY, dir);
    }
    
    public String getBaseDirectory(){
        return getString(KEY_BASE_DIRECTORY);
    }
    
    public void setBaseDirectory(String base_dir){
        putString(KEY_BASE_DIRECTORY, base_dir);
    }
    
    public String getConfigDirectory(){
        return getString(KEY_CONFIG_DIRECTORY);
    }
    
    public void setConfigDirectory( String config_dir ){
        putString(KEY_CONFIG_DIRECTORY, config_dir);
    }

}
