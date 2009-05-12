/*
 * Created on 2007-11-22
 */
package uncertain.event;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

/**
 * A container that can hold event parameters, and can fire events
 * EventContext
 * @author Zhou Fan
 *
 */
public class RuntimeContext extends DynamicObject implements IRuntimeContext {
    
    public static final String  KEY_CONFIGURATION = "__configuration__" ;
    
    public static final String  KEY_EXCEPTION = "__exception__";
    
    public static final String  KEY_IS_TRACE = "__is_trace__";
    
    public static final String  KEY_TRACE_OBJECT = "__trace_object__";
    
    public static RuntimeContext getInstance( CompositeMap map ){
        RuntimeContext context = new RuntimeContext();
        context.initialize(map);
        return context;
    }
    
    public CompositeMap getParentMap(){
        return getObjectContext().getParent();
    }

    /**
     * @return Configuration instance associated with this context
     */
    public Configuration getConfig() {
        Configuration config = (Configuration)get(KEY_CONFIGURATION);
        if(config==null){
            CompositeMap map = getParentMap();
            while(map!=null){
                config = (Configuration)(map.get(KEY_CONFIGURATION));
                if(config!=null) return config;
                map = map.getParent();
            }
            return null;
        }
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(Configuration config) {
        put(KEY_CONFIGURATION, config);
    }

    public void fireEvent(String event_name, Object[] args)
    {
        Configuration config = getConfig();
        if(config==null) return;
        //setException(null);
        try{
            config.fireEvent(event_name, args);
        }catch(Throwable thr){
            setException(thr);
        }
    }
    
    public void fireEvent(String event_name)
    {
        fireEvent(event_name, null);
    }

    private String getTypeKey( Class type ){
        return "_instance."+type.getName();
    }
    
    /**
     * Get instance of specified type in current service context 
     * @param type
     * @return
     */
    public Object getInstanceOfType( Class type){
        if( getObjectContext()==null) throw new IllegalStateException("Object context not initialized");
        Object obj = get(getTypeKey(type));
        if( obj==null && getParentMap() !=null)
            obj = getParentMap().get(getTypeKey(type));
        return obj;
    }
    
    public void setInstanceOfType( Class type, Object instance) {
        put(getTypeKey(type), instance);
    }    
    
    public Throwable getException(){
        return (Throwable)get(KEY_EXCEPTION);
    }
    
    public Exception getCatchableException(){
        Throwable thr = getException();
        if(thr!=null)
            if(thr instanceof Exception )
                return (Exception)thr;
        return null;
    }
    
    public void setException(Throwable e){
        put(KEY_EXCEPTION, e);
    }
   
    public boolean isTrace(){
        return getBoolean(KEY_IS_TRACE, false);
    }
    
    public void setTrace( boolean trace ){
        putBoolean(KEY_IS_TRACE, trace);
    }

    public Object getTraceObject() {
        return get(KEY_TRACE_OBJECT);
    }
    
    public void setTraceObject( Object obj ){
        put(KEY_TRACE_OBJECT, obj);
    }

}
