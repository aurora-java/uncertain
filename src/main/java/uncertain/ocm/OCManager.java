/**
 * Created on: 2004-6-9 15:44:58
 * Author:     zhoufan
 */
package uncertain.ocm;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;

import uncertain.composite.CharCaseProcessor;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import uncertain.datatype.DataTypeRegistry;
import uncertain.logging.DefaultLogger;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;

/**
 * The entry class to perform container/object mapping
 */
public class OCManager implements IMappingHandle {
    
    public static final String LOGGING_TOPIC = "uncertain.ocm";
    
    // Class name -> Class mapping
    HashMap				classMap;
    
    // get class name from CompositeMap
    ClassRegistry		classRegistry;
    // create instance from Class
    IObjectCreator		objectCreator;
    // populate object by reflection from CompositeMap
	ReflectionMapper	default_mapper;
	
	// List<IOCMEventListener>
	LinkedList			listener_list;
	
	// logger
	ILogger				logger;
	ILoggerProvider     mLoggerProvider;
    
    // DataTypeRegistry to get type information
    DataTypeRegistry        datatype_home;
	
	boolean			event_enable = true;
	
	static OCManager default_instance = new OCManager();
	
	static CompositeLoader default_loader = CompositeLoader.createInstanceForOCM();
	
	public static CompositeLoader getDefaultCompositeLoader(){
	    return default_loader;
	}
/*
	static CompositeMapParser default_parser = CompositeMapParser.createInstance(
		null, 
		new CharCaseProcessor(CharCaseProcessor.CASE_LOWER, CharCaseProcessor.CASE_UNCHANGED)
	);
	
    public static CompositeMapParser defaultParser(){
        return default_parser;
    }
*/
    public static OCManager getInstance(){
		return default_instance;
	}
	
	
	void createDefaultLogger(){
	    logger = new DefaultLogger(LOGGING_TOPIC);
	    logger.setLevel(Level.WARNING);
	    //logger.info("Logger created: "+LOGGING_SPACE);
	}

    private void _init(){
        classMap = new HashMap(1000);
        default_mapper = new ReflectionMapper(this);
        classRegistry   = new ClassRegistry();
        datatype_home = DataTypeRegistry.getInstance();
        createDefaultLogger();
        
    }
	/**
	 * Constructor for OCManager.
	 */
	public OCManager() {
        _init();
		objectCreator  = new ObjectRegistryImpl();
	}
	
	public OCManager(IObjectCreator obj_creator)	{
	    _init();
		objectCreator = obj_creator;
	}
/*
	public OCManager(OCManager parent, IObjectCreator obj_creator){
	    classMap = parent.classMap;
	    default_mapper = parent.default_mapper;
	    classRegistry = parent.classRegistry;
	    setObjectCreator(obj_creator);
	}
*/

    /**
     * @return Returns the objectCreator.
     */
    public IObjectCreator getObjectCreator() {
        return objectCreator;
    }
    /**
     * @param objectCreator The objectCreator to set.
     */
    public void setObjectCreator(IObjectCreator objectCreator) {
        this.objectCreator = objectCreator;
    }
	public void addListener(IOCMEventListener listener){
		if( listener_list == null) listener_list = new LinkedList();
		listener_list.add(listener);
		setEventEnable(true);
	}
	
	public boolean isEventEnable(){
/*	    
		if( listener_list == null) return false;
		return event_enable;
*/
	    return event_enable;
	}
	
	public void setEventEnable(boolean enable){
		event_enable = enable;
	}
	
	void fireEvent(OCMEvent evt){
	    //logger.info(evt.toString());
	    //getLogger().log(evt.getLevel(),evt.toString());
		if(isEventEnable()&&listener_list!=null){
			Iterator it = listener_list.iterator();
			while(it.hasNext()){
				IOCMEventListener listener = (IOCMEventListener)it.next();
				listener.onEvent(evt);
			}
		}
	}
	
	/**
	 * Determine mapped java class from CompositeMap
	 * @param container the CompositeMap to map
	 * @return mapped java class, or null if can't find correct class
	 */
	public Class getMappedClass(CompositeMap container){
	    if(container==null) throw new NullPointerException();
		String cls_name = classRegistry.getClassName(container);
		if(cls_name==null) {
		    //logger.warning("Unknown element:"+container.getName()+" namespace:"+container.getNamespaceURI());
            //logger.warning();
		    return null;
		}
		if(classMap.containsKey(cls_name)) 
		    return (Class)classMap.get(cls_name);
		Class cls = null;
		try{
		    cls = Class.forName(cls_name);
		}catch(ClassNotFoundException ex){
		    fireEvent(OCMEventFactory.newClassNotFoundEvent(this,cls_name));
		}
		classMap.put(cls_name, cls);
		return cls;
	}
	
    public Object createNewInstance(CompositeMap container){
        Class cls = getMappedClass(container);
	    if(cls==null) {
	        return null;
	    }
		Object obj = null;
		try{
		    obj = objectCreator.createInstance(cls);
		    //logger.info("Instance created: "+cls.getName());
		} catch(Exception ex){
		    /*
			fireEvent(OCMEventFactory.newObjectCreationFailEvent(this,container));
			return null;
			*/
		    throw new RuntimeException(ex);
		}        
		return obj;
    }
    
    void populateObjectInternal(CompositeMap container, Object obj, IMappingHandle handle){
	
		if( obj instanceof IConfigurable){
			((IConfigurable)obj).beginConfigure(container);
		}
        default_mapper.toObject(container, obj, handle);
        if( obj instanceof IConfigureListener){
            ((IConfigureListener)obj).endConfigure();
        }
		if( isEventEnable())
			fireEvent(OCMEventFactory.newObjectCreatedEvent(this,obj));
    }
    
    /**
     * Decides whether given CompositeMap can be mapped to a class
     */
    public boolean canCreateInstance(CompositeMap container){
        return getMappedClass(container) != null;
    }
	
	/**
	 * Create an new object instance from CompositeMap
	 * @param container
	 * @return
	 */
	public Object createObject( CompositeMap container){
	    assert container!=null;
	    Object obj = createNewInstance(container);
	    if(obj!=null) populateObjectInternal(container, obj, this);
	    return obj;
	}
	
	public Object createObject( CompositeMap container, IMappingHandle handle){
	    Object obj = handle.createNewInstance(container);
	    if(obj!=null) populateObjectInternal(container, obj, handle);
	    return obj;
	}

	
    public void getUnknownContainer(CompositeMap container){
        return;
    }
    
    public boolean acceptUnknownContainer(){
        return false;
    }
	
	public CompositeMap createContainer( Object obj){
	    return null;
	    /*
		if( obj == null) return null;
		String pkg_name = obj.getClass().getPackage().getName();
		IObjectFactory fact = (IObjectFactory)package_map.get(pkg_name);
		if(fact == null) 
			return null;
		else{
			CompositeMap	container = fact.createContainer(obj);
			IObjectMapper mapper = fact.getMapper(obj);
			if( mapper == null) mapper = default_mapper;
			mapper.toContainer(obj, container);
			return container;			
		}	
		*/
				
	}	
	
	
	public void populateObject( CompositeMap container, Object obj){
	    /*
	    IObjectFactory fact = (IObjectFactory)namespace_map.get(container.getNamespaceURI());
		if( fact == null)
			throw new IllegalArgumentException("Can't get IObjectFactory from container's namespace:"+container.getNamespaceURI());
		else{
			
		    IObjectMapper mapper = fact.getMapper(obj);
			if( mapper == null) mapper = default_mapper;
			mapper.toObject(container,obj);
		}
		*/
        if( obj instanceof IConfigurable){
            ((IConfigurable)obj).beginConfigure(container);
        }
                
		default_mapper.toObject(container,obj);
        
        if( obj instanceof IConfigurable){
            ((IConfigurable)obj).endConfigure();
        }        
	}
	
	public void populateObject( CompositeMap container, Object obj, IMappingHandle handle){
        
        if( obj instanceof IConfigurable){
            ((IConfigurable)obj).beginConfigure(container);
        }        
	    
        default_mapper.toObject(container,obj,handle);
        
        if( obj instanceof IConfigurable){
            ((IConfigurable)obj).endConfigure();
        }         
	}
	
	/*	
	public void populateContainer( Object obj, CompositeMap container){
	  
		if( obj == null) return;
		String pkg_name = obj.getClass().getPackage().getName();
		IObjectFactory fact = (IObjectFactory)package_map.get(pkg_name);
		if(fact == null) 
			return ;
		else{
			IObjectMapper mapper = fact.getMapper(obj);
			if( mapper == null) mapper = default_mapper;
			mapper.toContainer(obj, container);
		}	
		
	}
	*/
	
	ReflectionMapper getDefaultMapper(){
		return default_mapper;		
	}

    /**
     * @return Returns the classRegistry.
     */
    public ClassRegistry getClassRegistry() {
        return classRegistry;
    }
    /**
     * @param classRegistry The classRegistry to set.
     */
    public void setClassRegistry(ClassRegistry classRegistry) {
        this.classRegistry = classRegistry;
    }
    
    
    /**
     * @return Returns the logger.
     */
    public ILogger getLogger() {
        return logger;
    }
    /**
     * @param logger The logger to set.
     */
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }
    
    /** provide a single entry to handle exception in o/c mapping process */
    public void handleException(String message, Throwable thr){
        if(thr instanceof Error) throw (Error)thr;
        Throwable t;
        for(t = thr; t.getCause()!=null; t=t.getCause());
        throw new RuntimeException(t);
        //logger.log(Level.SEVERE, message, t);
    }
    
    /**
     * get ReflectionMapper that used by OCManager to perform reflection operations
     * @return ReflectionMapper instance
     */
    public ReflectionMapper getReflectionMapper(){
        return default_mapper;
    }
    
    /**
     * get ObjectAccessor of a class for specified field
     * @param cls Class that the field is from
     * @param name name of the field
     * @return ObjectAccessor instance
     */
    public ObjectAccessor getAccessor(Class cls, String field_name){
        MappingRule rule = default_mapper.getMappingRule(cls);
        if(rule==null) return null;
        ObjectAccessor oac = (ObjectAccessor)rule.getAttributeMapping().get(field_name.toLowerCase());
        return oac;        
    }
    
    public void setAttribute(Object obj, String field_name, Object value) throws Exception {
        ObjectAccessor oac = getAccessor(obj.getClass(), field_name);
        if(oac!=null) oac.writeToObject(obj,value);
    }
    
    public Object getAttribute(Object obj, String field_name) throws Exception {
        ObjectAccessor oac = getAccessor(obj.getClass(), field_name);
        if(oac==null) return null; 
        return oac.readFromObject(obj);
    }

    /**
     * @return the datatype_home
     */
    public DataTypeRegistry getDataTypeHome() {
        return datatype_home;
    }

    /**
     * @param datatype_home the datatype_home to set
     */
    public void setDataTypeHome(DataTypeRegistry datatype_home) {
        this.datatype_home = datatype_home;
    }

    /**
     * @return the mLoggerProvider
     */
    public ILoggerProvider getLoggerProvider() {
        return mLoggerProvider;
    }

    /**
     * @param loggerProvider the mLoggerProvider to set
     */
    public void setLoggerProvider(ILoggerProvider loggerProvider) {
        mLoggerProvider = loggerProvider;
        logger = loggerProvider.getLogger(LOGGING_TOPIC) ;
    }
}
