/*
 * Created on 2005-7-22
 */
package uncertain.event;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.IterationHandle;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.DummyLogger;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.IExceptionHandle;
import uncertain.proc.IFeature;
import uncertain.proc.ParticipantRegistry;
import uncertain.proc.ProcedureRunner;

/**
 * Holds configuration to run a Procedure, and create participant instance 
 * from configuration data
 * @author Zhou Fan
 */
public class Configuration  implements Cloneable, IEventDispatcher
//implements IMappingHandle
{
    
    public static final String LOGGING_TOPIC = "uncertain.event";
    
    private class FeatureInstance {
        int    attach_result;
        Object feature_instance;
        /*
        public FeatureInstance( int result, Object inst ){
            this.attach_result = result;
            this.feature_instance = inst;
        }
        */
    }
    
    private class ConfigurationIterator  implements IterationHandle {
        
        IterationHandle dataFilter;
        
        public ConfigurationIterator(IterationHandle filter){
            dataFilter = filter;
        }
        
        public int process( CompositeMap map){
            int result = IterationHandle.IT_CONTINUE;
            if(isMapLoaded(map))
                return IterationHandle.IT_CONTINUE;
            if(dataFilter!=null){
                result = dataFilter.process(map);
                if(result==IterationHandle.IT_BREAK)
                    return result;
            }
            List fClassList = reg.getFeatures(map.getNamespaceURI(), map.getName());
            if(fClassList != null){
                List    fInstList = createFeatureList(map);
                Iterator it = fClassList.iterator();
                while(it.hasNext()){
                    Class fClass = (Class)it.next();
                    //attach_result = IFeature.NORMAL;
                    //Object fInst = createFeatureInstance(fClass,map);
                    FeatureInstance inst = createFeatureInstance(fClass,map);
                    if(inst.attach_result==IFeature.NO_CONFIG)
                        return IterationHandle.IT_REMOVE;
                    else if (inst.attach_result==IFeature.NO_CHILD_CONFIG)
                        result = IterationHandle.IT_NOCHILD;
                    if(inst.feature_instance != null) 
                        fInstList.add(inst.feature_instance);
                }
            }               

            if(reg.getClassName(map)!=null){
                
                loadInternal(map);
                return IterationHandle.IT_NOCHILD;
            }

            return result;

        }
    };
    
    ParticipantRegistry		registry;
    OCManager				ocManager;
    HandleManager           handleManager;
    ClassRegistry           reg;
    
    LinkedList				config_list = new LinkedList();
    LinkedList				participant_list = new LinkedList();
    LinkedList              exception_handle_list = new LinkedList();
    // CompositeMap -> List<Feature>
    HashMap					feature_map;
    // CompositeMap -> ParticipantInstance
    HashMap                 instance_map;
    
    //IParticipantListener	listener;
    // temporary result
    int						attach_result=0;
    // current event handle
    IEventHandle            current_handle = null;
    // Handle flag
    int                     handle_flag = EventModel.HANDLE_NORMAL;
    
    // Parent Configuration
    Configuration           parent;
    
    // logger
    ILogger                 mLogger;
    
    public Configuration(){
        this(ParticipantRegistry.defaultInstance(), OCManager.getInstance());
    }
    
    public Configuration( OCManager m){
        this(ParticipantRegistry.defaultInstance(), m);
    }

    /**
     * Constructor using ParticipantRegistry to lookup handle method info by class,
     * and OCManager to populate handle instance
     * @param r
     * @param m
     */
    public Configuration(ParticipantRegistry r, OCManager m) {
        registry = r;
        ocManager = m;
        reg = m.getClassRegistry();
        createHandleManager();
    }

    /**
     * @param handleManager the handleManager to set
     */    
    public void setHandleManager(HandleManager handleManager) {
        this.handleManager = handleManager;
    }
/*
    public void setListener(IParticipantListener listener){
        this.listener = listener;
    }
  */  
    public IEventHandle getCurrentHandle(){
        return current_handle;
    }
    
    /**
     * Whether specified CompositeMap is already loaded. If a clone of original loaded CompositeMap
     * is passed, return result will be false. 
     * @param data
     * @return true if is loaded
     */
    public boolean isMapLoaded( CompositeMap data ){
        Integer value = CompositeUtil.uniqueHashCode(data);
        if(instance_map!=null)
            if(instance_map.containsKey(value))
                return true;
        if(feature_map!=null)
            if(feature_map.containsKey(value))
                return true;
        return false;
    }
    
    /**
     * Add a object as participant. 
     * @param obj
     */
    public void addParticipant(Object obj){
        ILogger logger = ocManager.getLogger();
        if(obj!=null){
            Class cls = obj.getClass();
	        if(registry.isParticipant(cls)||obj instanceof IEventListener){
	            /*
	             if(listener!=null)
	                if(!listener.addParticipant(obj)) return;
	                */
	            participant_list.add(obj);
	            logger.log(Level.CONFIG, "Added participant instance "+cls.getName());
	        }else{
	            // logger.warning("Instance of "+cls.getName()+" created, but no handle method found");
                // still add instance even if it has no handle method
                participant_list.add(obj);
	        }
            if( obj instanceof IExceptionHandle)
                addExceptionHandle((IExceptionHandle)obj);
            if(handleManager!=null)
                handleManager.addParticipant(obj);
        }
    }

    public Object createParticipant(CompositeMap container){
        //Object obj = ocManager.createNewInstance(container);        
        Object obj = ocManager.createObject(container);
        if(obj!=null){ 
            //ocManager.populateObject(container, obj);
            addParticipant(obj);
            if(instance_map==null) instance_map = new HashMap();
            instance_map.put(new Integer(CompositeUtil.uniqueHashCode(container)), obj);
        }
        else
            ocManager.getLogger().log("Can't create instance from "+container.toXML());
        return obj;
    }
    
    
    public Object getInstance(CompositeMap container){
        if(instance_map==null)
            return null;
        return container==null?instance_map.get(null):instance_map.get(new Integer(CompositeUtil.uniqueHashCode(container)));
    }
    
    
    private void loadInternal(CompositeMap container){
       
        Class cls = ocManager.getMappedClass(container);
        if(cls!=null){
            createParticipant(container);
        }else{
            Iterator it = container.getChildIterator();
            if(it!=null)
                while(it.hasNext()){
                    CompositeMap child = (CompositeMap) it.next();
                    loadInternal(child);
                }
        }
    }


    private List createFeatureList( CompositeMap config ){
        if(feature_map==null) feature_map = new HashMap();
        LinkedList fList = new LinkedList();
        feature_map.put(CompositeUtil.uniqueHashCode(config),fList);
        return fList;
    }
    
    /**
     * Create a feature instance of specified type from config
     * @param fClass
     * @param config
     * @return
     */
    private FeatureInstance createFeatureInstance(Class fClass, CompositeMap config){
        if(fClass==null)
            throw new NullPointerException("Class parameter is null");
        FeatureInstance inst = new FeatureInstance();
        inst.attach_result = IFeature.NORMAL;
        Object fInst = null;
        try{
            fInst = ocManager.getObjectCreator().createInstance(fClass);
        }catch(Exception ex){
            throw new RuntimeException("Can't create instance of "+fClass.getName(), ex);
        }
        if( fInst ==null )
            throw new RuntimeException("Can't create instance of "+fClass.getName());
        ocManager.populateObject(config,fInst);
        inst.feature_instance = fInst;
        if( fInst instanceof IFeature){
            inst.attach_result = ((IFeature)fInst).attachTo(config, this);
            if(inst.attach_result == IFeature.NO_FEATURE_INSTANCE || inst.attach_result== IFeature.NO_CONFIG) 
                inst.feature_instance = null;
        }
        if(inst.feature_instance!=null)
            addParticipant(inst.feature_instance);
        return inst;
    }
    /*
    public void loadConfig(CompositeMap config, IParticipantListener listener){
        setListener(listener);
        loadConfig(config);
    }
    */
    
    public void loadConfig(CompositeMap config ){
        loadConfig(config,null);
    }
    
    /**
     * Load a CompositeMap and add create participants described by this CompositeMap
     * @param config A CompositeMap that contains participant description
     * @param filter A IterationHandle to determine whether a child node will be processed.
     * If the process() method returns NO_CHILD, then the child node will be ignored.
     */
    private void loadConfig(CompositeMap config, IterationHandle filter ){
        config_list.add(config);
        IterationHandle handle = new ConfigurationIterator(filter);
        config.iterate(handle, true);
    }

    
    public void loadConfigList(Collection list){
        for(Iterator it = list.iterator(); it.hasNext();){
            CompositeMap config = (CompositeMap)it.next();
            loadConfig(config);
        }
    }
    
    /**
     * Get all participant instances represented by this config
     * @return
     */
    public List getParticipantList(){
        return participant_list;
    }
    
    public List getConfigList(){
        return config_list;
    }
    
    /**
     * Get all feature instances associated with this config
     * @param config
     * @return
     */
    
    public List getAttachedFeatures( CompositeMap config ){
        return feature_map==null?null:(List)feature_map.get(CompositeUtil.uniqueHashCode(config));
    }
    
    /**
     * Get feature instance associated with specified config for certain type
     * @param config part of Configuration
     * @param cls type of feature instance to get
     * @return
     */
    public Object getFeatureInstance( CompositeMap config, Class cls) {
        List l = getAttachedFeatures(config);
        if(l==null) return null;
        Iterator it = l.iterator();
        while(it.hasNext()){
            Object o = it.next();
            if(cls.isInstance(o)) return o;
        }
        return null;
    }
    
    /**
     * Add a exception handle that will get called when an exception is thrown
     * during procedure execution
     * @param handle Instance of IExceptionHandle
     */
    public void addExceptionHandle(IExceptionHandle handle){
        exception_handle_list.add(handle);
    }
/*    
    public void addFirstExceptionHandle(IExceptionHandle handle){
        exception_handle_list.add(0, handle);
    }
*/    
    
    public int fireEvent(String event_name, Object[] args)
        throws Exception
    {
        return fireEventInternal(event_name, args, null, null, this.handleManager);
    }    
    
    public int fireEvent(String event_name, CompositeMap context, Object[] args)
        throws Exception
    {
        return fireEventInternal(event_name, args, (ProcedureRunner)null, context, this.handleManager);
    }    

    public int fireEvent(String event_name, Object[] args, HandleManager handle_manager)
        throws Exception
    {
        return fireEventInternal(event_name, args, null, null, handle_manager);
    }
    
    public int fireEvent(String event_name, Object[] args, CompositeMap context, HandleManager handle_manager)
    throws Exception
    {
        return fireEventInternal(event_name, args, null, context, handle_manager);
    }    
    
    public int fireEvent(String event_name, Object[] args, ProcedureRunner runner, HandleManager handle_manager)
        throws Exception
    {
        return fireEventInternal( event_name, args, runner, null, handle_manager );
    }
    
    protected ILogger getLogger( ProcedureRunner runner, CompositeMap context ){
        if( mLogger!=null)
            return mLogger;
        ILogger logger = null;
        if( parent!=null )
            logger = parent.getLogger();
        if( logger ==null ){            
            if( runner!=null )
                logger = runner.getLogger();
            else
                logger = LoggingContext.getLogger(context, LOGGING_TOPIC);
        }
        if( logger == null)
            logger = DummyLogger.getInstance();
        return logger;
    }
    
    protected int fireEventInternal(String event_name, Object[] args, ProcedureRunner runner, CompositeMap context, HandleManager handle_manager)
        throws Exception 
    {
        ILogger logger = getLogger(runner, context);
        //logger.info("Using logger:"+logger);
        current_handle = null;
        if(handle_manager==null) return EventModel.HANDLE_NORMAL;
        handle_flag = 0;
        
        Configuration parent_config = this;
        for(int i=EventModel.PRE_EVENT; i<=EventModel.POST_EVENT; i++){            
            // invoke event listeners
            ListIterator    lsnr_it = handle_manager.getEventListenerIterator();            
            do{
                if(lsnr_it!=null)
                    while(lsnr_it.hasNext()){
                        //handle_flag = EventModel.HANDLE_NORMAL;
                        IEventListener lnr = (IEventListener)lsnr_it.next();
                        handle_flag = lnr.onEvent(runner, i,event_name);
                        if(handle_flag==EventModel.HANDLE_NO_SAME_SEQUENCE)
                            break;
                        else if(handle_flag==EventModel.HANDLE_STOP)
                            return handle_flag;
                    }
                if(parent_config!=null)
                    parent_config = parent_config.getParent();
                lsnr_it = null;
                if(parent_config!=null)
                    if(parent_config.handleManager!=null)
                        lsnr_it =parent_config.handleManager.getEventListenerIterator();
            }while(lsnr_it!=null);
            // invoke event handles hooked with this event
            Iterator it = handle_manager.getEventHandleIterator(event_name, i);
            parent_config = this;
            do{
                if(it!=null)
                    while(it.hasNext()){
                        handle_flag = EventModel.HANDLE_NORMAL;
                        IEventHandle handle = (IEventHandle)it.next();
                        current_handle = handle;
                        logger.log(Level.FINE, handle.toString());
                        if(runner!=null)
                            handle_flag = handle.handleEvent(i, runner, args);
                        else
                            handle_flag = handle.handleEvent(i, context, args);
                        if(handle_flag==EventModel.HANDLE_NO_SAME_SEQUENCE)
                            break;
                        else if(handle_flag==EventModel.HANDLE_STOP)
                            return handle_flag;                
                    }
                it = null;
                if(parent_config!=null)
                    parent_config = parent_config.getParent();
                if(parent_config!=null)
                    if(parent_config.handleManager!=null)
                        it =parent_config.handleManager.getEventHandleIterator(event_name, i);             
            }while(it!=null);
        }
        
        return EventModel.HANDLE_NORMAL;    
    }
    
    public List getExceptionHandles(){
        return exception_handle_list;
    }
    
    /**
     * Create a new HandleManager instance from a subset of config
     * @param config
     * @return
     */
    public HandleManager createHandleManager( CompositeMap config ){
        List features = null;
        if(feature_map==null) 
            return null;
        //features = (List)feature_map.get(config);
        features = getAttachedFeatures(config);
        if(features==null){
            loadConfig(config);
            //features = (List)feature_map.get(config);
            features = getAttachedFeatures(config);
            if(features==null) return null;
        }
        HandleManager manager = new HandleManager(registry);
        Iterator it = features.iterator();
        while(it.hasNext()){
            manager.addParticipant(it.next());
        }
        return manager;
    }
    
    
    public RuntimeContext createEventContext( CompositeMap container ) {
        RuntimeContext context = new RuntimeContext();
        context.initialize(container);
        context.setConfig(this);
        return context;
    }
    
    public RuntimeContext createEventContext(){
        return createEventContext(new CompositeMap("context"));
    }
    
    
    /**
     * @return Current set or created HandleManager instance
     */
    public HandleManager getHandleManager() {
        return handleManager;
    }
    
    /**
     * create new HandleManager from scratch
     * @return
     */
    public HandleManager createHandleManager(){
        handleManager = new HandleManager(registry);
        return handleManager; 
    }
    
    public void addConfiguration(Configuration other){
        if(handleManager!=null){
            Iterator it = other.getParticipantList().iterator();
            while(it.hasNext()){
                handleManager.addParticipant(it.next());
            }
        }
        if(other.getExceptionHandles()!=null)
            addExceptionHandles(other.getExceptionHandles());
    }
    
    public void addExceptionHandles(Collection handle_list){
        if(exception_handle_list==null) exception_handle_list = new LinkedList();
        exception_handle_list.addAll(handle_list);
    }
    
    public void clear(){
        config_list.clear();
        participant_list.clear();
        exception_handle_list.clear();
        if(feature_map!=null)
            feature_map.clear();
        if(instance_map!=null)
            instance_map.clear();
      
        if(handleManager!=null){
            handleManager.clear();
            handleManager = null;
        }
    }

    /**
     * @return the parent
     */
    public Configuration getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Configuration parent) {
        if(parent==this) throw new IllegalArgumentException("Can't set parent to be self");
        this.parent = parent;
    }    
    
    public void setLogger( ILogger logger ){
        this.mLogger = logger;
    }
    
    public ILogger getLogger(){
        if( mLogger!=null) 
            return mLogger;
        if( parent!=null)
            return parent.getLogger();
        return DummyLogger.getInstance();
    }
    
    /*
    public Object clone(){
        Configuration conf = new Configuration( registry, ocManager);
        conf.handleManager = (HandleManager)handleManager.clone();
        conf.config_list.addAll(config_list);
        //conf.listener = listener;
        conf.parent = parent;
        conf.participant_list.addAll(participant_list);
        conf.exception_handle_list.addAll(exception_handle_list);
        if(feature_map!=null){
            conf.feature_map = new HashMap(feature_map);            
        }
        if(instance_map!=null){
            conf.instance_map = new HashMap(instance_map);
        }        
        return conf;
    }
    */

}
