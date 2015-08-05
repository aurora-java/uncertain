package uncertain.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uncertain.ocm.IObjectCreationListener;
import uncertain.util.reflect.AnnotatedMethodMap;
import uncertain.util.reflect.IAnnotationExtractor;

public class DataDistributor implements IDataDistributor, IObjectCreationListener {
	
	public static class WatchExtractor implements IAnnotationExtractor{

		public String getValue( Annotation annotation ){
			Watch watch = (Watch)annotation;
			String key = watch.key();
			return key;
		};
		
	}
	
	static final WatchExtractor EXTRACTOR = new WatchExtractor();
	
	/**
	 * property name ->  Collection<SetterInvoker>
	 */
	Map<String,Collection<SetterInvoker>>	setters;
	AnnotatedMethodMap							methodMap;
	
	public DataDistributor(){
		setters = new ConcurrentHashMap<String,Collection<SetterInvoker>>();
		methodMap = new AnnotatedMethodMap(EXTRACTOR);
	}

	@Override
	public void setData(String key, Object data){
		Collection<SetterInvoker> slist = setters.get(key);
		if(slist!=null)
			for(SetterInvoker si:slist){
				si.invoke(data);
			}
	};
	
	protected void addMethod(Object instance, String key, Method method){
		Collection<SetterInvoker> silist = setters.get(key);
		if(silist==null){
			silist = new LinkedList<SetterInvoker>();
			setters.put(key, silist);
		}
		silist.add( new SetterInvoker(instance, method));
	}

	@Override
	public void registerInstance( Object instance ){
		Map<String,Method> map = methodMap.getSetterMethods(instance);
		if(map==null)
			return;
		for(Map.Entry<String,Method> entry : map.entrySet()){
			addMethod(instance, entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void onInstanceCreate(Object instance) {
		registerInstance( instance );
	}
	
	public AnnotatedMethodMap getMethodMap(){
		return this.methodMap;
	}


}
