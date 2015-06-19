/*
 * Created on 2009-9-2
 */
package uncertain.proc;

import java.io.IOException;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;

/**
 * Manages Procedure instance creation
 */

public interface IProcedureManager {
    
    /**
     * Load procedure from class path
     * @param name name of procedure, in java class format
     * @return A new Procedure instance according to specified name
     */
    public Procedure loadProcedure( String name );
    
    /**
     * Create a new empty Procedure instance
     */
    public Procedure createProcedure();
    
    /**
     * Create a new Procedure instance from config
     * @param proc_config A CompositeMap instance in procedure config format
     * @return Created Procedure instance
     */
    public Procedure createProcedure( CompositeMap proc_config );
    
    /**
     * Create a new ProcedureRunner instance
     */
    public ProcedureRunner createProcedureRunner();
    
    /**
     * Do initialize work with CompositeMap instance
     */
    public void initContext( CompositeMap context_map );
    
    /**
     * Make clean up works on context map
     * @param context_map An CompositeMap instance that is returned by createContextMap()
     */
    public void destroyContext( CompositeMap context_map );
    
    
    /**
     * Create a new Configuration instance for use with ProcedureRunner
     * @return
     */
    public Configuration createConfig();

}
