/*
 * Created on 2005-5-19
 */
package uncertain.proc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IChildContainerAcceptable;

/**
 * EntryContainer
 * @author Zhou Fan
 * 
 */
public abstract class EntryList extends AbstractEntry implements IChildContainerAcceptable {
    
    // List of child entry
    LinkedList		entry_list;
    // name -> IEntry
    Map				entry_map;
    
    private void checkMap(){
        if(entry_map==null) entry_map = new HashMap();
    }
    
    /** Add a node entry to container
     * @param e entry to add
     */
    public void addEntry(IEntry e){
        if(entry_list==null) entry_list = new LinkedList();
        entry_list.add(e);
        // put entry to name map
        String n = e.getName();
        if(n!=null){
            checkMap();
            entry_map.put(n,e);
        }
        // if it is a entry container, put its entry map to self's entry map
        if(e instanceof EntryList){
            EntryList l = (EntryList)e;
            Map m = l.getEntryMap();
            if(m!=null){
                checkMap();
                entry_map.putAll(m);
                m.clear();
            }
            l.setEntryMap(entry_map);
        }
        e.setOwner(this);        
    }
    
    public IEntry getNamedEntry(String name){
        if(entry_map==null) return null;
        return (IEntry)entry_map.get(name);
    }
    
    public ListIterator locateNamedEntry(String name){
        return locateEntry(getNamedEntry(name));
    }
    
    public ListIterator locateEntry(IEntry e){
        if(e==null) return null;
        int idx =entry_list.indexOf(e);
        if(idx<0) return null;
        return entry_list.listIterator(idx);
    }
    
    public List getEntryList(){
        return entry_list;
    }
    
    //public ListIterator get
    
    protected Map getEntryMap(){
        return entry_map;
    }
    
    protected void setEntryMap(Map m){
        entry_map = m;
    }
    
    public void addAction(Action a){
        addEntry(a);
    }
    
    public void addProcedure(Procedure p){
        addEntry(p);
    }
    
    public void addSwitch(Switch ch){
        addEntry(ch);
    }
    
    public void addSet(Set s ){
        addEntry(s);
    }
    
    public void addLoop(Loop l){
        addEntry(l);
    }
    
    public void addAssert(Assert a){
        addEntry(a);
    }
    
    /**
     * Sub instance must override this method if can accept unknown config
     */
    public void addChild(CompositeMap child){
        throw BuiltinExceptionFactory.createUnknownChild(child);
    }
    
    public void clear(){
        if(entry_list!=null)
            entry_list.clear();
        if(entry_map!=null)
            entry_map.clear();
    }

    /**
     * @see uncertain.proc.IEntry#run()
     */
    public abstract void run(ProcedureRunner runner) throws Exception ;

}
