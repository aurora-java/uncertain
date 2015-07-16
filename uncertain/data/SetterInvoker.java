package uncertain.data;

import java.lang.reflect.Method;

public class SetterInvoker {
	
	Object instance;
	Method setterMethod;

	public SetterInvoker(Object instance, Method setterMethod) {
		super();
		this.instance = instance;
		this.setterMethod = setterMethod;
	}

	public Object getInstance() {
		return instance;
	}
	
	public void setInstance(Object instance) {
		this.instance = instance;
	}
	
	public Method getSetterMethod() {
		return setterMethod;
	}
	
	public void setSetterMethod(Method setterMethod) {
		this.setterMethod = setterMethod;
	}
	
	public void invoke(Object value){
		try{
			setterMethod.invoke(instance, value);
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}

}
