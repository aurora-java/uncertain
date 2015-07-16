package uncertain.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uncertain.util.reflect.AnnotationFilter;
import uncertain.util.reflect.IMethodAcceptor;

public class SetterMethodMap {
	
	/**
	 * class name -> { property name -> setter method }
	 */
	Map<String,Map<String,Method>> setterMethodMap;
	
	public class MethodAcceptor implements IMethodAcceptor {

		Map<String,Method> methodMap;
		
		public MethodAcceptor(){
			methodMap = new ConcurrentHashMap<String,Method>();
		}
		
		public void accept( Annotation annotation, Method method){
			Watch watch = (Watch)annotation;
			String key = watch.key();
			if(methodMap.containsKey(key))
				throw new IllegalArgumentException("Duplicate key "+key+" for method "+method.getName());
			methodMap.put(key,method);
		};
		
		public Map<String,Method> getMap(){
			return methodMap;
		}
		
	}
	
	public SetterMethodMap(){
		setterMethodMap = new ConcurrentHashMap<String,Map<String,Method>>();
	}
	
	protected void addMethod( String cls, String property, Method method ){
		Map<String,Method> setter_map = setterMethodMap.get(cls);
		if(setter_map==null){
			setter_map = new ConcurrentHashMap<String,Method>();
			setterMethodMap.put( cls, setter_map );
		}
		setter_map.put(property, method);
	}
	
	public void analyze( Class cls ){
		String name = cls.getName();
		if(setterMethodMap.containsKey(name))
			return;
		MethodAcceptor ma = new MethodAcceptor();
		AnnotationFilter.findMethod(cls, Watch.class, ma);
		setterMethodMap.put(name, ma.getMap());
	}
	
	public Map<String,Method> getSetterMethods( Class cls ){
		analyze(cls);
		return setterMethodMap.get(cls.getName());
	}
	
	public Map<String,Method> getSetterMethods( Object instance ){
		return getSetterMethods(instance.getClass());
	}
	

}
