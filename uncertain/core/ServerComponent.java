/*
 * Created on 2007-11-9
 */
package uncertain.core;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;

/**
 * A wrapper class to manage server component life cycle
 * UComponent
 * @author Zhou Fan
 *
 */
public class ServerComponent {
    
    public static final String STATUS_INITIALIZING = "initializing";
    public static final String STATUS_SHUTTING_DOWN = "shutting_down";    
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    public static final String STATUS_ERROR = "error";
    
    String              required_class_names;
    Class[]             requiredClassArray;    
    String              exported_class_names;
    Class[]             exportedClassArray;
    
    // status of this component
    String              status = STATUS_INACTIVE;
    // name of this component
    String              name;
    //  CompositeMap to hold actual configuration 
    CompositeMap        config_map;
    // Configuration instance
    Configuration       configuration;
    // Owner of this component
    ServerComponentManager owner;
    
    // Exception caught when operating component
    Throwable           exception;
    
    // Arguments pass to event
    Object[]            event_args;
    
    /** Constructor should not be called by other package
     * @param owner
     */
    ServerComponent(ServerComponentManager owner) {
        this.owner = owner;
        configuration = owner.uncertainEngine.createConfig();
        configuration.createHandleManager();
        event_args = new Object[1];
        event_args[0] = owner.uncertainEngine;
    }

    public static Class[] getClasses( String class_with_comma)
        throws ClassNotFoundException
    {
        String[] classes = class_with_comma.split(",");
        Class[] cls_array = new Class[classes.length];
        for(int i=0; i<classes.length; i++){
            String cls = classes[i].trim();
            cls_array[i] = Class.forName(cls);
        }
        return cls_array;
    }

    /** Set java classes that this component depends on, class names are separated by comma
     * @param required_class_names the dependantClasses to set
     */
    public void setRequiredClasses(String required_class_names) 
        throws ClassNotFoundException
    {
        this.required_class_names = required_class_names;
        requiredClassArray = getClasses(required_class_names);
    }    
    
    /**
     * @return name of required classes, seprarated by comma
     */
    public String getRequiredClasses() {
        return required_class_names;
    }
    
    /**
     * @return required classes in array of Class
     */
    public Class[] getRequiredClassArray(){
        return this.requiredClassArray;
    }
    
    /**
     * @param set classes that this component exports
     */
    public void setExportedClasses(String exported_class_names) 
        throws ClassNotFoundException
    {
        this.exported_class_names = exported_class_names;
        exportedClassArray = getClasses(exported_class_names);
    }
    
    /**
     * @return name of exported classes, seprarated by comma
     */
    public String getExportedClasses() {
        return exported_class_names;
    }
    
    /**
     * @return expored classes in array of Class
     */
    public Class[] getExportedClassArray(){
        return exportedClassArray;
    }    
    public void addConfig( CompositeMap config ){
        this.config_map = config;
    }
    
    public boolean fireComponentEvent(String event_name, Object[] args)
    {
        try{
            exception = null;        
            configuration.fireEvent(event_name, args);
            return true;
        }catch(Throwable thr){
            exception = thr;
            return false;
        }
    }
    
    public boolean isInitializing(){
        return STATUS_INITIALIZING.equals(status);
    }
    
    public boolean isShuttingDown(){
        return STATUS_SHUTTING_DOWN.equals(status);
    }
    
    void load(){
        if(configuration!=null)
            configuration.clear();
        configuration = owner.uncertainEngine.createConfig();
        configuration.loadConfig(config_map);        
    }

    public boolean initialize(){
        if(isInitializing()) 
            return false;        
        status = STATUS_INITIALIZING;
        load();
        boolean success = fireComponentEvent("Initialize", event_args);
        status = success ? STATUS_ACTIVE: STATUS_ERROR;
        return success;
    }
    
    public boolean shutdown(){
        if(isShuttingDown()) 
            return false;        
        status = STATUS_SHUTTING_DOWN;
        boolean success = fireComponentEvent("Shutdown", event_args);
        status = success ? STATUS_INACTIVE: STATUS_ERROR;
        return success;
    }
    
    public boolean redeploy(){
        boolean success = shutdown();
        if(success) return initialize();
        else return false;
    }
    
    public void validate(){
        if(!fireComponentEvent("Validate", event_args))
            status = STATUS_ERROR;
    }
    
    public String getStatus(){
        return status;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get exception that thrown during component init or shutdown
     * @return
     */
    public Throwable getException(){
        return exception;
    }

}
