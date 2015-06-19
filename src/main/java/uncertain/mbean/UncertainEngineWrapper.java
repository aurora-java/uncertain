/*
 * Created on 2011-4-13 ����08:51:16
 * $Id$
 */
package uncertain.mbean;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.core.DirectoryConfig;
import uncertain.core.UncertainEngine;
import uncertain.logging.ILoggingTopicRegistry;
import uncertain.ocm.ClassRegistryMBean;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.proc.ParticipantRegistry;

public class UncertainEngineWrapper implements UncertainEngineWrapperMBean {

    UncertainEngine     mEngine;

    /**
     * @param mEngine
     */
    public UncertainEngineWrapper(UncertainEngine mEngine) {
        this.mEngine = mEngine;
    }

    
    public void setConfigDirectory(File dir) {
        mEngine.setConfigDirectory(dir);
    }

    public ClassRegistryMBean getClassRegistry() {
        return mEngine.getClassRegistry();
    }

    public OCManager getOcManager() {
        return mEngine.getOcManager();
    }

    public IObjectCreator getObjectCreator() {
        return mEngine.getObjectCreator();
    }

    public IObjectRegistry getObjectRegistry() {
        return mEngine.getObjectRegistry();
    }

    public ParticipantRegistry getParticipantRegistry() {
        return mEngine.getParticipantRegistry();
    }

    public CompositeMap getGlobalContext() {
        return mEngine.getGlobalContext();
    }

    public File getConfigDirectory() {
        return mEngine.getConfigDirectory();
    }

    public boolean getIsRunning() {
        return mEngine.getIsRunning();
    }

    public DirectoryConfig getDirectoryConfig() {
        return mEngine.getDirectoryConfig();
    }

    public String getName() {
        return mEngine.getName();
    }

    public void setName(String name) {
        mEngine.setName(name);
    }

    public String getLogPath() {
        return mEngine.getDirectoryConfig().getLogDirectory();
    }

    public void setLogPath(String logPath) {
        mEngine.getDirectoryConfig().setLogDirectory(logPath);
    }

    public String getDefaultLogLevel() {
        return mEngine.getDefaultLogLevel();
    }

    public void setDefaultLogLevel(String defaultLogLevel) {
        mEngine.setDefaultLogLevel(defaultLogLevel);
    }

/*
    public boolean getCacheConfigFiles() {
        return mEngine.getCacheConfigFiles();
    }

    public void setCacheConfigFiles(boolean mCacheConfigFiles) {
        mEngine.setCacheConfigFiles(mCacheConfigFiles);
    }
    */
    public String dumpInstanceMapping(){
        ObjectRegistryImpl or =  (ObjectRegistryImpl)mEngine.getObjectRegistry();
        Map mapping = or.getInstanceMapping();
        return MBeanUtil.dumpMap(mapping);
    }


}
