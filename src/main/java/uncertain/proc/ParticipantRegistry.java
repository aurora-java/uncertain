/*
 * Created on 2005-6-1
 */
package uncertain.proc;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;

import uncertain.event.ReflectionMethodHandle;

/**
 * Holds cached handle methods for participant class 
 * @author Zhou Fan
 * 
 */
public class ParticipantRegistry {
    
    // class->List<HandleMethod>
    HashMap  handle_method_map;    
    
    static final ParticipantRegistry default_instance = new ParticipantRegistry();
    static final HandleMethod[] empty_methods = new HandleMethod[0];
    static final String[] method_prefix = {"pre", "on","post"};
    static final int[]	  method_prefix_len = {3,2,4};
    
    static boolean equalTypes(Class[] a1, Class[]a2){
        if(a1==null) return a2==null;
        if(a2==null) return false;
        if(a1.length!=a2.length) return false;
        for(int i=0; i<a1.length; i++)
            if(!a1[i].equals(a2[i])) return false;
        return true;
    }
        
    /**
     * get a default global HandleMethodManager instance
     * @return HandleMethodManager
     */
    public static ParticipantRegistry defaultInstance(){
        return default_instance;
    }
    
    public class HandleMethod {
        
        public int 	sequence;
        public Method	method;
        public String  event_name;
        public int		arg_type;
        

        public HandleMethod(int sequence, Method method, int arg_type, String event_name) {
            if(method ==null) throw new IllegalArgumentException("method must not be null");
            this.sequence = sequence;
            this.method = method;
            this.event_name = event_name;
            this.arg_type = arg_type;
        }
        
        public boolean equals(Object another){
            if(another==null) return false;
            if(another instanceof HandleMethod){
                HandleMethod m = (HandleMethod)another;
                return sequence==m.sequence && method.equals(m.method) && event_name.equalsIgnoreCase(m.event_name)&& arg_type == m.arg_type;
            }else return false;
        }
        
        public String toString(){
            return method_prefix[sequence]+'.'+event_name+" "+method.toString()+" "+arg_type;
        }
    };
    
    /**
     * A handle method must follow these rules:
     * 1. return type is void
     * --2. parameter is a single ProcedureRunner, or none
     * 3. name starts with "pre" or "on" or "post"
     * 4. first character follows prefix in 3 must be capitalized
     * 5. must be public
     * for example: public void onCheckUserLogin(ProcedureRunner runner);
     * @param m
     * @return
     */
    private HandleMethod getHandleMethod(Method m){
        Class return_type = m.getReturnType();
        if(! (void.class.equals(return_type) || int.class.equals(return_type)) ) return null;
        
        int arg_type = 0;
        Class[] params = m.getParameterTypes(); 
        if(params.length==0)
            arg_type = ReflectionMethodHandle.ARG_TYPE_NONE;
        else if(params.length==1){        
            if(ProcedureRunner.class.equals(params[0]))
                arg_type = ReflectionMethodHandle.ARG_TYPE_SINGLE;
            else
                arg_type = ReflectionMethodHandle.ARG_TYPE_MULTIPLE;
        }
        else arg_type = ReflectionMethodHandle.ARG_TYPE_MULTIPLE;

        String name = m.getName();
        for(int i=0; i<method_prefix.length; i++){
            if(name.length()<=method_prefix_len[i]) continue;
            if(name.indexOf(method_prefix[i]) != 0) continue;
            if( Character.isLowerCase(name.charAt(method_prefix_len[i]))) continue;
            return new HandleMethod(i,m,arg_type,name.substring(method_prefix_len[i]).toLowerCase());
        }
        return null;
    }
    
    HandleMethod[] createHandleMethods(Class cls){
        LinkedList lst = new LinkedList();
        Method[] methods = cls.getMethods();
        for(int i=0; i<methods.length; i++){
            HandleMethod h = getHandleMethod(methods[i]);
            if(h!=null) lst.add(h);
        }
        if(lst.size()==0) return empty_methods;
        Object[] handles = lst.toArray();
        HandleMethod[] hmarray = new HandleMethod[handles.length];
        System.arraycopy(handles,0,hmarray,0,handles.length);
        handle_method_map.put(cls,hmarray);
        return hmarray;
    }
    
    public HandleMethod[] getHandleMethods(Class cls){
        HandleMethod[] m = (HandleMethod[])handle_method_map.get(cls);
        if(m==null) m = createHandleMethods(cls);
        return m;
    }
    
    public boolean isParticipant(Class cls){
        HandleMethod[] m = getHandleMethods(cls);
        return m!=empty_methods;
    }

    /**
     * Default constructor
     */
    public ParticipantRegistry() {
        handle_method_map = new HashMap();
    }
    
    public int getEntrySize(){
        return handle_method_map.size();
    }

}
