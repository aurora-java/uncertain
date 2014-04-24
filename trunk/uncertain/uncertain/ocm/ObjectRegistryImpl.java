/*
 * Created on 2005-6-14
 */
package uncertain.ocm;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import uncertain.mbean.IMBeanNameProvider;
import uncertain.mbean.IMBeanRegister;
import uncertain.mbean.IMBeanRegistrable;
import uncertain.ocm.mbean.ObjectRegistryImplWrapper;

/** Create object by constructor reflection, using instances associated with specific class
 *  as parameter
 *  @author Zhou Fan
 * 
 */
public class ObjectRegistryImpl implements IObjectCreator, IObjectRegistry, IMBeanRegistrable {
    
    public static final String LOGGING_SPACE = "uncertain.objectspace";
    
    // Class -> instance
    Map	instance_map;
    
    // Class -> Constructor;
    Map constructor_map;
    
    // Class -> Object[] for constructor parameters
    Map parameter_map;
    
    // Class -> ArrayList<Constructor>
    Map constructor_list_map;
    
    // Class -> ISingleton instance
    Map singleton_instance_map;
    
    // Logger instance
    Logger	logger;    
    
    
    ObjectRegistryImpl	parent = null;
    
    /**
     * Default constructor
     */
    public ObjectRegistryImpl() {
        instance_map = new HashMap();
        constructor_map = new HashMap();
        parameter_map = new HashMap();
        constructor_list_map = new HashMap();
        singleton_instance_map = new HashMap();
        logger = Logger.getLogger(LOGGING_SPACE);
    }
    
    /**
     * Creates new ObjectSpace using existing instance as parent 
     * @param p parent ObjectSpace
     */
    public ObjectRegistryImpl(ObjectRegistryImpl p){
        instance_map = new HashMap();
        constructor_map = new HashMap();
        parameter_map = new HashMap();
        setParent(p);
        logger = p.logger;
    }
    
    /**
     * @return Returns the parent.
     */
    public ObjectRegistryImpl getParent() {
        return parent;
    }
    /**
     * @param parent The parent ObjectSpace to set.
     */
    public void setParent(ObjectRegistryImpl parent) {
        this.parent = parent;
        //this.constructor_list_map = parent.constructor_list_map;
    }
    
    /**
     * Get instance for specified type which is previously registered or can get from parent ObjectSpace
     * @param type class of instance to get
     * @return instance of that type, or null if not registered
     */
    public Object getInstanceOfType(Class type){
        Object o = instance_map.get(type);
        if(o!=null) return o;
        if(parent!=null) return parent.getInstanceOfType(type);
        return null;
    }
    
    /** Associate a instance with specified type, without further association with super class
     *  or implemented interface
     * @param type The type to associate with
     * @param instance of specified type
     */
    public void registerInstanceOnce( Class type, Object instance){
        if(instance!=null)
            if(!type.isAssignableFrom(instance.getClass()) && !type.isPrimitive() )
                throw new IllegalArgumentException("type "+type.getName() +" isn't compatible with "+instance.getClass().getName());
        instance_map.put(type,instance);
    } 

    
    /** Associate a instance with a certain type, also associate all super types and interfaces
     *  of the type with this instance, if super type/interface has not been associated yet.     * 
     * @param type The type to associate with
     * @param instance of specified type
     */
    public void registerInstance( Class type, Object instance){
        registerInstanceOnce(type,instance);
        if(type.isInterface())   return;        
        // associate instance with all super type
        for( Class cls = type.getSuperclass(); cls !=null && !Object.class.equals(cls); cls = cls.getSuperclass()){
            if(!instance_map.containsKey(cls)) registerInstanceOnce(cls,instance);
        }
        // associate instance with all implemented type
        Class[] interfaces = type.getInterfaces();
        for(int i=0; i<interfaces.length; i++){
            if(!instance_map.containsKey(interfaces[i])) registerInstanceOnce(interfaces[i],instance);
        }
    }   
    
    /**
     * Equals to registerParameter(instance.getClass(), instance)
     * @param instance parameter instance to register
     */
    public void registerInstance(Object instance){
        registerInstance(instance.getClass(), instance);
    }
    
    ArrayList getConstructorList(Class type){
       if(parent!=null) return parent.getConstructorList(type);
       ArrayList cList = (ArrayList)constructor_list_map.get(type);
       if(cList==null){
           // get all constructors and sort  
           Constructor[] cscts = type.getConstructors();
           int length = cscts.length;
           if(length==0) throw new IllegalArgumentException("No public constructor available for "+type);
           cList = new ArrayList(length);
           for(int i=0; i<length; i++) cList.add(cscts[i]);
           Collections.sort(cList, new Comparator(){
               
               public int compare(Object o1, Object o2){
                   Constructor c1 = (Constructor) o1, c2 = (Constructor)o2;
                   return c1.getParameterTypes().length - c2.getParameterTypes().length;
               }
               
               public boolean equals(Object obj){
                   return obj!=null && obj.getClass().equals(this.getClass());
               }
               
           });
           constructor_list_map.put(type,cList);           
       }
       return cList;
    }
    
    /**
     * Analyze all constructors in specified class and get the most proper one, that is,
     * the constructor that has max number of parameters that can be found in current associated
     * parameters
     */
    Constructor getProperConstructor(Class type){
        // try all possible constructors, parameter number from max to min
        ArrayList cList = getConstructorList(type);
        for(int i=cList.size()-1; i>=0; i--){            
            Constructor c = (Constructor)cList.get(i);
            Class[] types=c.getParameterTypes();
            boolean proper = true;
            for(int n=0; n<types.length; n++){
                if(getInstanceOfType(types[n])==null) 
                    proper = false;
            }
            if(proper) return c;
        }
        return null;
    }
    
    public void analysisConstructor( ILogger logger, Class type){
            ArrayList cList = getConstructorList(type);
            logger.log(type.getName()+" has "+cList.size()+" constructors");
            int count=1;
            for(int i=cList.size()-1; i>=0; i--, count++){
                Constructor c = (Constructor)cList.get(i);
                Class[] types=c.getParameterTypes();
                logger.log("No."+count+" types:");
                for(int types_count=0; types_count<types.length; types_count++)
                    logger.log(types[types_count].getName());
                boolean proper = true;
                for(int n=0; n<types.length; n++){
                    if(getInstanceOfType(types[n])==null){ 
                        proper = false;
                        logger.log("type "+types[n].getName()+" is missing, so discard");
                    }else{
                        logger.log(types[n].getName()+" -> "+getInstanceOfType(types[n]));
                    }
                }
                if(proper) {
                    logger.log("This constructor is OK:");
                    return;
                }
            }
            logger.log("Can't find proper constructor");
        }        
    
    /**
     * Get a proper constructor for specified class
     * @param type which Class to find constructor for
     * @return the constructor, or null if none proper constructor found
     */
    public Constructor getConstructor(Class type){
        Constructor c = (Constructor)constructor_map.get(type);
        if( c==null){
            c=getProperConstructor(type);
            if(c!=null) {
                Class[] types = c.getParameterTypes();
                Object[] params = new Object[types.length];
                for(int i=0; i<types.length; i++) params[i] = getInstanceOfType(types[i]);
                constructor_map.put(type, c);
                parameter_map.put(type,params);
                logger.fine("Constructor for "+type+" set to "+c);
            }
            else{
                logger.warning("Can't get proper constructor for "+type);
                //analysisConstructor( , type);
            }
        }
        return c;
    }
    
    /**
     * Wether there are enough parameter to create a instance of specified type
     */
    public boolean canCreateInstance(Class type){
        return constructor_map.containsKey(type);
        /*
        boolean rtn = constructor_map.containsKey(type);
        if(rtn) return rtn;
        */
    }
    
    private Object createInstanceInternal(Class type)
    {
        Constructor c = getConstructor(type);
        if(c==null) return null;
        Object[] params = (Object[])parameter_map.get(type);
        try {
			return c.newInstance(params);
		} catch (Exception e) {
			StringBuffer msg=new StringBuffer("Constructor:");
			msg.append(c);
			boolean isFirst=true;
			for(Object obj:params){
				if(!isFirst)
					msg.append(",");
				else
					msg.append(",params:");
				if(obj!=null)
					msg.append(obj.getClass());
				else
					msg.append("null");
				isFirst=false;
			}
			throw new RuntimeException(msg.toString(), e);
		} 
    }
    
    /**
     * Creates an instance of specified type, 
     * @param type class to create instance
     * @return created instance, or null if can't create instance
     */
    public Object createInstance(Class type) 
        throws Exception
    {
        if(ISingleton.class.isAssignableFrom(type)){
            Object instance = singleton_instance_map.get(type);
            if(instance==null){
                instance = createInstanceInternal(type);
                singleton_instance_map.put(type, instance);
                //System.out.println("adding "+type.getName()+" -> "+instance);
            }
            /*
            else
                System.out.println("reusing "+instance);
            */    
            return instance;
        }
        else
            return createInstanceInternal(type);
    }
    
    /**
     * Creates an instance of specified type. If any exception caught, just return null
     * instead of throw exception out
     * @param type
     * @return created instance, or null if can't create instance
     */
    public Object createInstanceSilently(Class type){
        try{
            return createInstance(type);
        } catch(Throwable ex){            
            if(ex.getCause()!=null) ex = ex.getCause();
            logger.log(Level.SEVERE,"error occur when create instance of "+type,ex);
            return null;
        }
    }
    
    public Map getInstanceMapping(){
        return instance_map;
    }
    
    public Logger getLogger(){
        return logger;
    }
    
    public void setLogger(Logger l){
        logger = l;
    }
    
    public CompositeMap dumpInstanceList( CompositeMap list ){
        Iterator it = instance_map.entrySet().iterator();
        while( it.hasNext() ){
            Map.Entry entry = (Map.Entry)it.next();
            Object obj = entry.getValue();
            Class type = (Class)entry.getKey();
            CompositeMap instance = new CompositeMap("instance");
            instance.put("type", type.getName());
            instance.put("instance", obj.toString());
            instance.put("instance_type", obj.getClass().getName());
            list.addChild(instance);
        }
        return list;
    }
    
    public void registerMBean(IMBeanRegister register,
            IMBeanNameProvider name_provider)
            throws MalformedObjectNameException,
            InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException {
        new ObjectRegistryImplWrapper(this).registerMBean(register, name_provider);
    }

}
