/*
 * Created on 2005-5-31
 */
package uncertain.event;
import java.lang.reflect.Method;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.proc.ProcedureRunner;

/**
 * ReflectionEventHandle
 * @author Zhou Fan
 * 
 */
public class ReflectionMethodHandle implements IEventHandle {
    
    /** No arguments */
    public static final int ARG_TYPE_NONE = 0;
    
    /** One single argument of type ProcedureRunner */
    public static final int ARG_TYPE_SINGLE = 1;
    
    /** Multiple arguments */
    public static final int ARG_TYPE_MULTIPLE = 2;
    
    Method	handle_method;
    Class[] param_types;
    Object	handle_instance;
    int		arg_type = ARG_TYPE_NONE;

    /**
     * Construct from a method, attached with an object instance
     */
    public ReflectionMethodHandle(Object instance, Method method, int arg_type) {
        this.handle_instance = instance;
        this.handle_method = method;
        this.arg_type = arg_type;
        if(arg_type==ARG_TYPE_MULTIPLE)
            param_types = handle_method.getParameterTypes();
    }
    
    public int handleEvent(int sequence, CompositeMap context, Object[] parameters) throws Exception {
        return handleEvent(sequence, null, context, parameters);
    }
    
    public int handleEvent(int sequence, ProcedureRunner runner, Object[] parameters) throws Exception {
        CompositeMap context =runner==null?null:runner.getContext();
        return handleEvent(sequence, runner, context, parameters);
    }
    

    /**
     * Invoke target method
     */
    public int handleEvent(int sequence, ProcedureRunner runner, CompositeMap context, Object[] parameters) throws Exception {
        //System.out.println(this.handle_instance.getClass().getName()+'.'+this.handle_method.getName());
        Object[] args = null;
        switch(arg_type){
            case ARG_TYPE_SINGLE:
                args = new Object[1];
                args[0]=runner;
                break;
            case ARG_TYPE_NONE:
                args = null;
                break;
            case ARG_TYPE_MULTIPLE:
                args = new Object[param_types.length];
                if(param_types!=null )                        
                        for(int i=0, n=0; i<param_types.length; i++){
                            args[i] = null;
                            // Direct assign
                            if(i==0 && ProcedureRunner.class.equals(param_types[i])){
                                args[0] = runner;
                            }else if( context!=null && IRuntimeContext.class.isAssignableFrom(param_types[i])){
                                args[i] = DynamicObject.cast(context, param_types[i]);
                                //System.out.println("Assigning dynamic object "+args[i]);
                            } else if(parameters!=null){                                
                                if(n<parameters.length){
                                    Object arg = parameters[n];
                                    if(arg!=null)
                                        if(param_types[i].isAssignableFrom(arg.getClass()))
                                            args[i] = arg;
                                }
                                n++;
                            }
                            if(args[i]==null && param_types[i].isPrimitive())
                                throw new IllegalArgumentException("Target handle method "+this.handle_method.getDeclaringClass().getName()+"." + this.handle_method.getName()+", parameter No."+(i+1)+" is of primitive type "+param_types[i].getName()+", but value got from context is null, this method can't be invoked");                              
                        }
                break;
        }
        Object value = handle_method.invoke(handle_instance, args);
        if(value!=null)
            return ((Integer)value).intValue();
        else
            return EventModel.HANDLE_NORMAL;
    }
    
    public String toString(){
        return handle_instance.getClass().getName() + '.' + handle_method.getName();
        // + ":"+handle_instance.toString()
    }

}
