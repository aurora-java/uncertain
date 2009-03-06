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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Create object by constructor reflection, using instances associated with specific class
 *  as parameter
 *  @author Zhou Fan
 * 
 */
public class ObjectSpace implements IObjectCreator {
    
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
    
    
    ObjectSpace	parent = null;
    
    /**
     * Default constructor
     */
    public ObjectSpace() {
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
    public ObjectSpace(ObjectSpace p){
        instance_map = new HashMap();
        constructor_map = new HashMap();
        parameter_map = new HashMap();
        setParent(p);
        logger = p.logger;
    }
    
    /**
     * @return Returns the parent.
     */
    public ObjectSpace getParent() {
        return parent;
    }
    /**
     * @param parent The parent ObjectSpace to set.
     */
    public void setParent(ObjectSpace parent) {
        this.parent = parent;
        //this.constructor_list_map = parent.constructor_list_map;
    }
    
    /**
     * Get instance for specified type which is previously registered or can get from parent ObjectSpace
     * @param type class of instance to get
     * @return instance of that type, or null if not registered
     */
    public Object getParameterOfType(Class type){
        Object o = instance_map.get(type);
        if(o!=null) return o;
        if(parent!=null) return parent.getParameterOfType(type);
        return null;
    }
    
    /** Associate a instance with specified type, without further association with super class
     *  or implemented interface
     * @param type The type to associate with
     * @param instance of specified type
     */
    public void registerParamOnce( Class type, Object instance){
        instance_map.put(type,instance);
    } 

    
    /** Associate a instance with a certain type, also associate all super types and interfaces
     *  of the type with this instance, if super type/interface has not been associated yet.     * 
     * @param type The type to associate with
     * @param instance of specified type
     */
    public void registerParameter( Class type, Object instance){
        registerParamOnce(type,instance);
        if(type.isInterface())   return;        
        // associate instance with all super type
        for( Class cls = type.getSuperclass(); cls !=null && !Object.class.equals(cls); cls = cls.getSuperclass()){
            if(!instance_map.containsKey(cls)) registerParamOnce(cls,instance);
        }
        // associate instance with all implemented type
        Class[] interfaces = type.getInterfaces();
        for(int i=0; i<interfaces.length; i++){
            if(!instance_map.containsKey(interfaces[i])) registerParamOnce(interfaces[i],instance);
        }
    }   
    
    /**
     * Equals to registerParameter(instance.getClass(), instance)
     * @param instance parameter instance to register
     */
    public void registerParameter(Object instance){
        registerParameter(instance.getClass(), instance);
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
                   return false;
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
                if(getParameterOfType(types[n])==null) 
                    proper = false;
            }
            if(proper) return c;
        }
        return null;
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
                for(int i=0; i<types.length; i++) params[i] = getParameterOfType(types[i]);
                constructor_map.put(type, c);
                parameter_map.put(type,params);
                logger.fine("Constructor for "+type+" set to "+c);
            }
            else{
                logger.warning("Can't get proper constructor for "+type);
                //logger.info(parameter_map.toString());
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
        throws Exception
    {
        Constructor c = getConstructor(type);
        if(c==null) return null;
        Object[] params = (Object[])parameter_map.get(type);
        return c.newInstance(params);
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
    
    public Map getParameters(){
        return instance_map;
    }
    
    public Logger getLogger(){
        return logger;
    }
    
    public void setLogger(Logger l){
        logger = l;
    }

}
