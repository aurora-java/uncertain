/*
 * Created on 2011-7-11 下午09:52:23
 * $Id$
 */
package uncertain.core;

import uncertain.composite.CompositeMap;
import uncertain.event.IEventDispatcher;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;

/**
 * Defines abstract behavior for an "uncertain style" container, that can provide these fundamental functions:
 * 1. To create object instance;
 * 2. To get instance by type;
 * 3. To fire event;
 * 4. To get global container for shared data.
 */
public interface IContainer {
    
    public IObjectCreator getObjectCreator();

    public IObjectRegistry getObjectRegistry();
    
    public IEventDispatcher getEventDispatcher();
    
    public CompositeMap getGlobalContext();

}
