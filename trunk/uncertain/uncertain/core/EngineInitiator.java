/*
 * Created on 2008-7-31
 */
package uncertain.core;

import java.io.File;

public class EngineInitiator {

    /**
     * @param homeDir
     * @param configPath
     */
    public EngineInitiator(File homeDir, File configPath) {
        mHomeDir = homeDir;
        mConfigPath = configPath;
    }

    File mHomeDir;
    File mConfigPath;
    UncertainEngine uncertainEngine;

    public void init() throws Exception {
        String pattern = ".*\\.config";
        uncertainEngine = new UncertainEngine(mConfigPath, "uncertain.xml");
        uncertainEngine.scanConfigFiles(pattern);
    }

    /**
     * @return the mHomeDir
     */
    public File getHomeDir() {
        return mHomeDir;
    }

    /**
     * @return the uncertainEngine
     */
    public UncertainEngine getUncertainEngine() {
        return uncertainEngine;
    }

}
