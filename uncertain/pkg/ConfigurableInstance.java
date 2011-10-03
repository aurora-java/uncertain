/*
 * Created on 2011-9-28 下午09:21:09
 * $Id$
 */
package uncertain.pkg;

import java.io.File;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;

public class ConfigurableInstance extends AbstractLocatableObject {
    
    Type[]  requiredInstances;
    Type[]  implementTypes;
    String  configFile;
    String  alternativeConfigFile;
    File    actualConfigFile;
    String  description;
    //String  rootElement;
    
    public int getRequiredCount(){
        return requiredInstances == null? 0: requiredInstances.length;
    }

    public Type[] getRequiredInstances() {
        return requiredInstances;
    }

    public void setRequiredInstances(Type[] requiredInstances) {
        this.requiredInstances = requiredInstances;
    }

    public Type[] getImplementTypes() {
        return implementTypes;
    }

    public void setImplementTypes(Type[] implementTypes) {
        this.implementTypes = implementTypes;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getAlternativeConfigFile() {
        return alternativeConfigFile;
    }

    public void setAlternativeConfigFile(String alternativeConfigFile) {
        this.alternativeConfigFile = alternativeConfigFile;
    }
    
    public void addDescription( CompositeMap desc ){
        this.description = desc.getText();
    }
    
    public boolean canCreateInstance( IObjectRegistry reg ){
        if(requiredInstances==null)
            return true;
        for(int i=0; i<requiredInstances.length; i++)
            if(reg.getInstanceOfType(requiredInstances[i].getType())==null){
                return false;
            }
        return true;
    }
    
    public void registerInstance( Object inst, IObjectRegistry reg ){
        if(implementTypes!=null)
            for( Type t :implementTypes){
                reg.registerInstance(t.getType(), inst);
            }
    }
    
    protected void setActualConfigFile( File file ){
        this.actualConfigFile = file;
    }
    
    protected File getActualConfigFile(){
        return this.actualConfigFile;
    }
    
    public Object createInstance( CompositeLoader composite_loader, OCManager oc_manager ){
        File config_file = getActualConfigFile();
        assert config_file!=null;
        assert config_file.exists();
        String name = null;
        CompositeMap data = null;
        try{
            name = config_file.getCanonicalPath();
            data = composite_loader.loadByFullFilePath(name);
        }catch(Exception ex){
            throw BuiltinExceptionFactory.createResourceLoadException(this, name, ex);
        }
        if(data==null)
            throw BuiltinExceptionFactory.createResourceLoadException(this, name, null);
        Object o = oc_manager.createObject(data);
        if(o==null){
            throw BuiltinExceptionFactory.createCannotCreateInstanceFromConfigException(this, name);
        }
        return o;
    }    
    

}
