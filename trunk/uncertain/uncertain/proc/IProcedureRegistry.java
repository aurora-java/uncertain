/*
 * Created on 2010-4-28 обнГ12:36:05
 * $Id$
 */
package uncertain.proc;

/**
 * Get procedure instance by pre-defined code
 */
public interface IProcedureRegistry {
    
    public Procedure getProcedure( String name );

}
