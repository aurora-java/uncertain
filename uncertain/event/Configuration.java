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
import java.util.logging.Logger;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.IterationHandle;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.OCManager;
import uncertain.proc.IEventHandle;
import uncertain.proc.IEventListener;
import uncertain.proc.IExceptionHandle;
import uncertain.proc.IFeature;
import uncertain.proc.ParticipantRegistry;
import uncertain.proc.ProcedureRunner;

/**
 * Holds configuration to run a Procedure, and create participant instance 
 * from configuration data
 * @author Zhou Fan
 */
public class Configuration  implements Cloneable
//implements IMappingHandle
{
    
    /**
     * implement this interface to get & filter participant on its creation
     * @author Zhou Fan
     *
     */
    public interface IParticipantListener {
        /**
         * @param pInst created participant instance
         * @return true if this instance can be added
         */
        public boolean addParticipant(Object pInst);
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
    
    IParticipantListener	listener;
    // temporary result
    int						attach_result=0;
    // current event handle
    IEventHandle            current_handle = null;
    // Handle flag
    int                     handle_flag = EventModel.HANDLE_NORMAL;
    
    // Parent Configuration
    Configuration           parent;
    
    public Configuration(){
        this(ParticipantRegistry.defaultInstance(), OCManager.getInstance());
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

    public void setListener(IParticipantListener listener){
        this.listener = listener;
    }
    
    public IEventHandle getCurrentHandle(){
        return current_handle;
    }
    
    /**
     * Add a object as participant. 
     * @param obj
     */
    public void addParticipant(Object obj){
        Logger logger = ocManager.getLogger();
        if(obj!=null){
            Class cls = obj.getClass();
	        if(registry.isParticipant(cls)||obj instanceof IEventListener){
	            if(listener!=null)
	                if(!listener.addParticipant(obj)) return;
	            participant_list.add(obj);
	            logger.info("Added participant instance "+cls.getName());
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
            ocManager.getLogger().info("Can't create instance from "+container.toXML());
        return obj;
    }
    
    /** get a participant instance created from a container
     * @param container
     * @return
     */
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
        feature_map.put(config,fList);
        return fList;
    }
    
    /**
     * Create a feature instance of specified type from config
     * @param fClass
     * @param config
     * @return
     */
    public Object createFeatureInstance(Class fClass, CompositeMap config){
        try{
            Object fInst = ocManager.getObjectCreator().createInstance(fClass);
            ocManager.populateObject(config,fInst);
            if( fInst instanceof IFeature){
                attach_result = ((IFeature)fInst).attachTo(config, this);
                if(attach_result != IFeature.NORMAL) return null;
            }
            addParticipant(fInst);
            return fInst;
        }catch(Exception ex){
            ocManager.handleException("Can't create feature instance of "+fClass.getName(),ex);
            return null;
        }
    }
    
    public void loadConfig(CompositeMap config, IParticipantListener listener){
        setListener(listener);
        loadConfig(config);
    }
    
    /**
     * Load a CompositeMap and add create participants described by this CompositeMap
     * @param config A CompositeMap that contains participant description
     */
    public void loadConfig(CompositeMap config){
        config_list.add(config);
        IterationHandle handle = new IterationHandle(){
          
            public int process( CompositeMap map){

                List fClassList = reg.getFeatures(map.getNamespaceURI(), map.getName());
                if(fClassList != null){
                    List	fInstList = createFeatureList(map);
                    Iterator it = fClassList.iterator();
                    while(it.hasNext()){
                        Class fClass = (Class)it.next();
                        attach_result = IFeature.NORMAL;
                        Object fInst = createFeatureInstance(fClass,map);
                        if(fInst != null) fInstList.add(fInst);
                        if(attach_result==IFeature.NO_CONFIG)
                            return IterationHandle.IT_REMOVE;
                    }
                }               

                if(reg.getClassName(map)!=null){
                    
                    loadInternal(map);
                    return IterationHandle.IT_NOCHILD;
                }

                return IterationHandle.IT_CONTINUE;

            }
        };
        
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
        return (List)feature_map.get(config);
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
    
    public void addFirstExceptionHandle(IExceptionHandle handle){
        exception_handle_list.add(0, handle);
    }
    
    public int fireEvent(String event_name, Object[] args)
        throws Exception
    {
        return fireEvent(event_name, args, null, handleManager);
    }    
    
    public int fireEvent(String event_name, CompositeMap context, Object[] args)
        throws Exception
    {
        return fireEventInternal(event_name, args, (ProcedureRunner)null, context, this.handleManager);
    }    

    public int fireEvent(String event_name, Object[] args, HandleManager handle_manager)
        throws Exception
    {
        return fireEvent(event_name, args, null, handle_manager);
    }
    
    public int fireEvent(String event_name, Object[] args, ProcedureRunner runner, HandleManager handle_manager)
        throws Exception
    {
        return fireEventInternal( event_name, args, runner, null, handle_manager );
    }
    
    protected int fireEventInternal(String event_name, Object[] args, ProcedureRunner runner, CompositeMap context, HandleManager handle_manager)
        throws Exception 
    {
        boolean create_trace = false;
        if(runner!=null)
            create_trace = runner.isTraceOn();
        
        current_handle = null;
        if(handle_manager==null) return EventModel.HANDLE_NORMAL;
        handle_flag = 0;
        
        Configuration parent_config = this;
        for(int i=EventModel.PRE_EVENT; i<=EventModel.POST_EVENT; i++){            
            // invoke event listeners
            ListIterator    lsnr_it = handle_manager.getEventListenerIterator();            
            while(lsnr_it!=null){
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
            }
            // invoke event handles hooked with this event
            Iterator it = handle_manager.getEventHandleIterator(event_name, i);
            parent_config = this;
            while(it!=null){
                while(it.hasNext()){
                    handle_flag = EventModel.HANDLE_NORMAL;
                    IEventHandle handle = (IEventHandle)it.next();
                    current_handle = handle;
                    if(create_trace) System.out.println(handle.toString());
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
            }
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
        if(features==null){
            loadConfig(config);
            features = (List)feature_map.get(config);
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
        handleManager = null;
        
        if(handleManager!=null)
            handleManager.clear();
            
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
    
    public Object clone(){
        Configuration conf = new Configuration( registry, ocManager);
        conf.handleManager = (HandleManager)handleManager.clone();
        conf.config_list.addAll(config_list);
        conf.listener = listener;
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

}
