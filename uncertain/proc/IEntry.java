/*
 * Created on 2005-3-23
 */
package uncertain.proc;

/**
 * Defines general interface for an entry in procedure
 * @author Zhou Fan
 * 
 */
public interface IEntry {
    
    public void run(ProcedureRunner runner) throws Exception ;
    
    public String getName();
    
    public void setOwner(IEntry owner);
    
    public IEntry getOwner();
    
    public IEntry getRootOwner();

}