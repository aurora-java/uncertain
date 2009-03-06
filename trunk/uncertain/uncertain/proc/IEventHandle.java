/*
 * Created on 2005-5-24
 */
package uncertain.proc;

import uncertain.composite.CompositeMap;


/**
 * Define general behavior of how event will be handled and how parameter will be passed
 * @author Zhou Fan
 * 
 */
public interface IEventHandle {
    
    public int handleEvent(int sequence, CompositeMap context, Object[] parameters) throws Exception;
    
    public int handleEvent(int sequence, ProcedureRunner runner, Object[] parameters) throws Exception ;

}
