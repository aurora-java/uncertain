/*
 * Created on 2005-5-18
 */
package uncertain.proc;

/**
 * Provides a basic implementation of IEntry, leave run() method abstract
 * @author Zhou Fan
 * 
 */
public abstract class AbstractEntry implements IEntry {
    
    String name;
    IEntry owner;

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
        else return this;
    }
    

}
