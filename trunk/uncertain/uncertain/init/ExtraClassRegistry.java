/*
 * Created on 2005-10-12
 */
package uncertain.init;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IConfigurable;
/**
 * ExtraClassRegistry
 * @author Zhou Fan
 * 
 */
public class ExtraClassRegistry extends ClassRegistry implements IConfigurable {

    ClassRegistry   parent;
    
    public ExtraClassRegistry(ClassRegistry parent) {
        this.parent = parent;
    }

    public void beginConfigure(CompositeMap config){

    }
    
    public void endConfigure(){
        parent.addAll(this);
    }
}
