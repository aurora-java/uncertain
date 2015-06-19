/*
 * Created on 2005-5-18
 */
package uncertain.proc;

import uncertain.composite.CompositeMap;
import uncertain.exception.ConfigurationFileException;
import uncertain.ocm.IConfigurable;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

/**
 * Provides a basic implementation of IEntry, leave run() method abstract
 * @author Zhou Fan
 * 
 */
public abstract class AbstractEntry implements IEntry, IConfigurable, ILocatable {
    
    protected String  name;
    protected IEntry  owner;
    
    
    protected String    source;
    protected Location  location;

    /**
     * @see uncertain.proc.IEntry#run(uncertain.proc.ProcedureRunner)
     */
    public abstract void run(ProcedureRunner runner)  throws Exception ;
    
    /**
     * @see uncertain.proc.IEntry#getName()
     */
    public String getName() {        
        return name;
    }
    
    /**
     * @see uncertain.proc.IEntry#setName()
     */    
    public void setName(String n){
        name = n;
    }

    /**
     * @see uncertain.proc.IEntry#setOwner(uncertain.proc.IEntry)
     */
    public void setOwner(IEntry owner) {
       this.owner = owner;
    }

    /**
     * @see uncertain.proc.IEntry#getOwner()
     */
    public IEntry getOwner(){
        return owner;
    }

    /**
     * @see uncertain.proc.IEntry#getRootOwner()
     */    
    public IEntry getRootOwner(){
        if(owner!=null) return owner.getRootOwner();
        else return null;
    }
    
    public void beginConfigure(CompositeMap config){
        source = config.getSourceFile()==null?null:config.getSourceFile().getAbsolutePath();
        location = config.getLocation();
        name = config.getName();
    }
    
    public void endConfigure(){
        
    }
    
    public Location getOriginLocation(){
        return location;
    }
    
    public String getOriginSource(){
        return source;
    }
    /*
    public ConfigurationFileException createException( String code, Object[] args, Throwable cause ){
        ConfigurationFileException exp = new ConfigurationFileException(code, args, cause, this);
        return exp;
    }
    
    public ConfigurationFileException createException( String code, Object[] args ){
        return createException( code, args, null);
    }
    */

}
