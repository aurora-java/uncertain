/*
 * Created on 2007-11-9
 */
package uncertain.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.event.Configuration;
import uncertain.ocm.IObjectRegistry;

/**
 * Holds and manages server components
 * @author Zhou Fan
 *
 */
public class ServerComponentManager {
    
    //public static final String 
    
    HashMap             component_map = new HashMap();
    LinkedList          component_list = new LinkedList();
    UncertainEngine     uncertainEngine;
    IObjectRegistry         objectSpace;
    
    public ServerComponentManager(UncertainEngine     uncertainEngine){
        this.uncertainEngine = uncertainEngine;
        this.objectSpace = uncertainEngine.getObjectRegistry();
    }
    
    public ServerComponent getComponent( String name ){
        return (ServerComponent)component_map.get(name);
    }
    
    public void addComponent( ServerComponent component ){        
        component_map.put(component.getName(), component);
        component_list.add(component);
    }
    
    public List getComponents(){
        return component_list;
    }
    
    void loadInstances(ServerComponent component){
        Configuration config = component.configuration;
        Iterator it = config.getParticipantList().iterator();
        while(it.hasNext()){
            Object inst = it.next();
            if(inst instanceof IGlobalInstance) 
                objectSpace.registerInstance(inst);
        }
        
    }

}
