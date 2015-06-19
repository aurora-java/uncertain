/*
 * ClassMapHandle.java
 *
 * Created on 2002年1月8日, 下午1:28
 */

package uncertain.util.reflect;

import java.lang.reflect.*;
import java.util.*;
import uncertain.composite.*;

/**
 *
 * @author  Administrator
 * @version 
 */
public class ClassMapHandle extends BasicClassIterateHandle {
    
    CompositeMap cls;
    CompositeMap constructors;
    CompositeMap methods;
    CompositeMap interfaces;
    CompositeMap fields;    

    /** Creates new ClassMapHandle */
    public ClassMapHandle() {
    }
    
    public CompositeMap getRoot(){
        return cls;
    }
    
    public void onMethod(Class owner, Method m) {
       CompositeMap map = new CompositeMap("method");
       map.put("name", m.getName());
       map.put("return-type", m.getReturnType().getName());
       map.put("declaring-class",m.getDeclaringClass().getName());
       Class[] params = m.getParameterTypes();
       for(int i=0; i<params.length; i++){
           CompositeMap param = new CompositeMap("parameter");
           setupClassMap( params[i], param);
           map.addChild(param);
       }
       this.methods.addChild(map);
    }    

    public void endIterate(Class cls) {
    }
    
    public void onConstructor(Class owner, Constructor constructor) {
       CompositeMap map = new CompositeMap("constructor");
       map.put("name", constructor.getName());
       Class[] params = constructor.getParameterTypes();
       for(int i=0; i<params.length; i++){
           CompositeMap param = new CompositeMap("parameter");
           setupClassMap( params[i], param);
           map.addChild(param);
       }
       this.constructors.addChild(map);
    }
    
    void setupClassMap( Class _cls, Map cls){
        cls.put("name", _cls.getName() );
        cls.put("super-class", _cls.getSuperclass());
        cls.put("package", _cls.getPackage());        
    }
    
    public void beginIterate(Class _cls) {
        
        cls =   new CompositeMap("class");
        setupClassMap( _cls, cls);
        
        interfaces = cls.createChild(null, null, "interfaces");
        fields = cls.createChild( null, null, "fields");
        constructors = cls.createChild(null,null,"constructors");
        methods = cls.createChild(null,null,"methods");
    }
    
    public void onInterface(Class owner, Class itf) {
        CompositeMap map = new CompositeMap("interface");
        setupClassMap(itf, map);
        interfaces.addChild( map);
    }    
    
    
    public void onField(Class owner, Field fld) {
        CompositeMap map = new CompositeMap("field");        
        map.put("name", fld.getName());
        map.put("type", fld.getType().getName());
        fields.addChild(map);
    }
    
    public static void main(String[] args) throws Exception {
        ClassIterator ci = new ClassIterator();
        ClassMapHandle hd = new ClassMapHandle();
        ci.iterate(BasicClassIterateHandle.class,hd);
        System.out.println(hd.getRoot().toXML());
    }

}
