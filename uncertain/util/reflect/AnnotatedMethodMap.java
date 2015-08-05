package uncertain.util.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uncertain.data.Watch;

public class AnnotatedMethodMap {
	
	/**
	 * class name -> { property name -> setter method }
	 */
	Map<String,Map<String,Method>> setterMethodMap;
	
	static final Map<String,Method> EMPTY_MAP = new HashMap<String,Method>(1);
	
	IAnnotationExtractor	extractor;
	
	public class MethodAcceptor implements IMethodAcceptor {

		Map<String,Method> methodMap;
		
		public MethodAcceptor(){
			methodMap = new ConcurrentHashMap<String,Method>();
		}
		
		public void accept( Annotation annotation, Method method){
			String key = extractor.getValue(annotation);
			if(key==null)
				return;
			if(methodMap.containsKey(key))
				throw new IllegalArgumentException("Duplicate key "+key+" for method "+method.getName());
			methodMap.put(key,method);
		};
		
		public Map<String,Method> getMap(){
			return methodMap;
		}
		
	}
	
	public AnnotatedMethodMap(IAnnotationExtractor extractor){
		this.extractor = extractor;
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
		if(ma.getMap().size()==0){
			setterMethodMap.put(name, EMPTY_MAP);
			return;
		}
		setterMethodMap.put(name, ma.getMap());
	}
	
	public Map<String,Method> getSetterMethods( Class cls ){
		analyze(cls);
		Map<String,Method> map = setterMethodMap.get(cls.getName());
		if(EMPTY_MAP==map)
			return null;
		return map;
	}
	
	public Map<String,Method> getSetterMethods( Object instance ){
		return getSetterMethods(instance.getClass());
	}
	

}
