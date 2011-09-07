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
    public static final int NO_CHILD_CONFIG = -3;
    
    /**
     * @param config The CompositeMap that the feature instance is associated with
     * @param procConfig The Configuration instance that loads config data 
     * @return A int constant to decide whether this feature instance can be attached
     * NORMAL: this feature can be attached
     * NO_FEATURE_INSTANCE: the feature instance should be discarded from Configuration
     * NO_CONFIG: both feature instance and the whole CompositeMap that the feature is associated
     * NO_CHILD_CONFIG: this element can be added, but child nodes should not be processed
     * with should be discarded
     */
    public int attachTo(CompositeMap config_data, Configuration config );

}
