/*
 * Created on 2008-5-9
 */
package uncertain.event;

import uncertain.composite.CompositeMap;

/**
 * Interface to mark a class to be "RuntimeContext"
 * A "RuntimeContext" is a sub class of uncertain.composite.DynamicObject, and
 * can be casted from a ProcedureRunner's context CompositeMap
 * @author Zhou Fan
 *
 */
public interface IRuntimeContext {
    
    public CompositeMap getObjectContext();
    
//    public void initialize( CompositeMap context );

}
