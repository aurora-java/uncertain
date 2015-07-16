package uncertain.data;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataDistributor implements IDataDistributor {
	
	/**
	 * property name ->  Collection<SetterInvoker>
	 */
	Map<String,Collection<SetterInvoker>>	setters;
	SetterMethodMap							methodMap;
	
	public DataDistributor(){
		setters = new ConcurrentHashMap<String,Collection<SetterInvoker>>();
		methodMap = new SetterMethodMap();
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
		for(Map.Entry<String,Method> entry : map.entrySet()){
			addMethod(instance, entry.getKey(), entry.getValue());
		}
	}


}
