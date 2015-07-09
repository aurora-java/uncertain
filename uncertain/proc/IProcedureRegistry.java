/*
 * Created on 2010-4-28 обнГ12:36:05
 * $Id: IProcedureRegistry.java 739 2010-04-28 08:07:18Z seacat.zhou@gmail.com $
 */
package uncertain.proc;

/**
 * Get procedure instance by pre-defined code
 */
public interface IProcedureRegistry {
    
    public Procedure getProcedure( String name );

}
