/*
 * BeanInstanceLoader.java
 *
 * Created on 2002年1月8日, 下午3:03
 */

package uncertain.composite;

import java.lang.reflect.*;
import java.util.*;
import uncertain.util.reflect.*;


/**
 *
 * @author  Administrator
 * @version 
 */
public class BeanInstanceMapper {
    
    /** default static instance */
    
    static BeanInstanceMapper default_mapper = new BeanInstanceMapper("get","set",true);
    
    MethodCache get_cache = new MethodCache();
    MethodCache set_cache  = new MethodCache();
    
    String get_prefix;
    String set_prefix;

    /** Creates new BeanInstanceLoader 
     * @param get_prefix prefix string for get methods
     * @param set_prefix prefix string for set methods
     * @no_base if true, only methods declared in leaf class will be processed
     */
    public BeanInstanceMapper(String get_prefix, String set_prefix, boolean no_base) {
        
        this.get_prefix = get_prefix;
        this.set_prefix = set_prefix;
        
        MethodArgSizeFilter one = new MethodArgSizeFilter(1), zero = new MethodArgSizeFilter(0);       
        
        /* get methods should start with get_prefix and has zero arg */
        get_cache.addFilter( new MethodNameFilter(get_prefix,null,null)) ;
        get_cache.addFilter(zero);
        
        /* set methods should start with set_prefix and has one arg */
        set_cache.addFilter( new MethodNameFilter(set_prefix,null,null));
        set_cache.addFilter(one);
        
        if( no_base){
            MethodDeclareClassFilter flt = new MethodDeclareClassFilter();
            set_cache.addFilter(flt);
            get_cache.addFilter(flt);
        }
        
    }
    
    public void setSetMethodFilter(Collection filters){
         set_cache.setFilters(filters);
    }
    
    public void setGetMethodFilter( Collection filters){
        get_cache.setFilters(filters);
    }
    
    public static BeanInstanceMapper defaultInstance(){ return default_mapper; }
    
    public void BeanToMap( Object inst, Map map)
        throws Exception
    {        
        Class cls = inst.getClass();
        Collection methods = get_cache.getMethods(cls);
        Iterator it = methods.iterator();
        while(it.hasNext()){
            Method mthd = (Method)it.next();
            Object result = mthd.invoke(inst,null);
            if( result != null){
                String method_name = mthd.getName();
                String prop = method_name.substring(get_prefix.length());
                map.put(prop,result);
            }
        }
    }
    
    public void MapToBean( Map map, Object inst)
        throws Exception
    {
        Class cls = inst.getClass();
        Collection methods = set_cache.getMethods(cls);
        Iterator it = methods.iterator();
        Object[] args = new Object[1];
        while(it.hasNext()){
            Method mthd = (Method)it.next();
                String method_name = mthd.getName();
                String prop = method_name.substring(set_prefix.length());
                args[0] = map.get(prop);
                mthd.invoke(inst,args);
        }
        
    }
    
    public CompositeMap createCompositeMap( Collection objs, String root, String element )
        throws Exception
    {
        CompositeMap map = new CompositeMap(root);
        Iterator it = objs.iterator();
        while(it.hasNext()){
            Object obj = it.next();
            if(obj == null) continue;
            String elm = element;
            if(elm == null) elm = obj.getClass().getName();
            CompositeMap child = new CompositeMap(elm);
            BeanToMap( obj, child);
            map.addChild(child);
        }
        return map;
    }
    

}
