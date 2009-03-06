/*
 * Created on 2005-9-19
 */
package uncertain.proc;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;

/**
 * Implement this interface to get notified when attached to configuration 
 * @author Zhou Fan
 * 
 */
public interface IFeature {
    
    public static final int NORMAL = 0;
    public static final int NO_FEATURE_INSTANCE = -1;
    public static final int NO_CONFIG = -2;
    
    public int attachTo(CompositeMap config, Configuration procConfig );

}
