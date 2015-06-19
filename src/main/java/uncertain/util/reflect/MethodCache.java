/*
 * MethodCache.java
 *
 * Created on 2002年1月8日, 下午2:20
 */

package uncertain.util.reflect;

import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author  Administrator
 * @version 
 */
public class MethodCache extends BasicClassIterateHandle {
    
    HashMap          cache;
    ClassIterator    iterator = new ClassIterator();
    LinkedList         filters = new LinkedList();
    
    /** used in loadClass to store methods */
    LinkedList         method_list;

    /** Creates new MethodCache */
    public MethodCache() {
        cache = new HashMap();
    }
    
    public MethodCache(int size){
        cache = new HashMap(size);
    }
    
    public void addFilter( MethodFilter flt){
        filters.add(flt);
    }
    
    public List getFilters(){
        return filters;
    }
    
    public void setFilters( Collection flts){
        filters.clear();
        addFilters(flts);
    }
    
    public void addFilters( Collection flts){
        filters.addAll(flts);
    }
    
    public Collection loadClass( Class cls ) throws SecurityException {
        iterator.iterate(cls, this);
        cache.put(cls,method_list);
        return method_list;
    }
    
    public synchronized Collection getMethods( Class cls){
        Object obj = cache.get(cls);
        if( obj == null){
            try{
                return loadClass(cls);                
            } catch(Exception ex){
                ex.printStackTrace();
                return null;
            }
        }else return (Collection)obj;
    }
    
    /** call back methods*/

    public void beginIterate(Class cls) {
        method_list = new LinkedList();
    }
    
    public void onMethod(Class owner, Method m) {
        Iterator it = filters.iterator();
        while(it.hasNext()){
            MethodFilter filter = (MethodFilter)it.next();
            if( !filter.accepts(owner, m)) return;                    
        }
        method_list.add(m);
    }     

}
