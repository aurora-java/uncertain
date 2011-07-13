/*
 * Created on 2011-7-13 上午10:56:26
 * $Id$
 */
package uncertain.ocm;

import uncertain.composite.CompositeMap;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

/**
 * This class can be used as base class to save source & location info in O/C mapping process.
 * *** DO REMEMBER *** to call super.beginConfigure() if sub class wants to override this method!
 */
public abstract class AbstractLocatableObject implements ILocatable, IConfigurable {
    
    protected   String mSource;
    protected   Location mLocation;

    public void endConfigure() {

    }

    public void beginConfigure(CompositeMap config) {
        mSource = config.getSourceFile()==null?null:config.getSourceFile().getAbsolutePath();
        mLocation = config.getLocation();
    }

    public Location getOriginLocation() {
        return mLocation;
    }

    public String getOriginSource() {
        return mSource;
    }

}
