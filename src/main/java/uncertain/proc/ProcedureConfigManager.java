/*
 * Created on 2009-9-7 обнГ01:55:06
 * Author: Zhou Fan
 */
package uncertain.proc;

import uncertain.composite.CompositeMap;

/**
 * Creates procedure configuration in CompositeMap
 */

public class ProcedureConfigManager {
    
    static final String NAMESPACE = ProcedureRunner.class.getPackage().getName();
    
    public static CompositeMap createConfigNode( String name ){
        CompositeMap config = new CompositeMap("p", NAMESPACE, name);
        return config;
    }

}
