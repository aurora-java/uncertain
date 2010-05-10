/*
 * Created on 2008-7-31
 */
package uncertain.core;

import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IObjectRegistry;
import uncertain.util.LoggingUtil;

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
