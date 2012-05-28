/*
 * Created on 2011-7-19 下午09:40:06
 * $Id$
 */
package uncertain.proc;

import java.lang.reflect.Method;

import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;

/**
 *  <p:method-invoke className="my.pkg.ClassWithStaticMethod" methodName="staticMethod">
 *      <p:arguments>
 *          <p:argument type="int" value="value containing ${@tag} />
 *          <p:argument type="long" path="/parameter/path_to_parameter" />
 *      </p:arguments>
 *  </p:method-invoke>
 *  
 *  or
 *  
 *  <p:method-invoke instanceType="my.pkg.IRegisteredInterface" methodName="anyMethod" resultPath="/model/result/@status" >
 *      <p:arguments>
 *          <p:argument type="int" value="value containing ${@tag}" />
 *          <p:argument type="long" path="/parameter/path_to_parameter" />
 *      </p:arguments>
 *  </p:method-invoke>
 *  
 */
public class MethodInvoke extends AbstractEntry {

	IObjectRegistry mRegistry;

	String methodName;
	String className;

	String instanceType;
	String resultPath;
	private Argument[] arguments;
	private Class[] argumentClasses;
	private Object[] argumentObjects;
	public MethodInvoke(IObjectRegistry registry){
		this.mRegistry = registry;
	}
	public void run(ProcedureRunner runner) throws Exception {
		if (methodName == null) {
			throw BuiltinExceptionFactory.createAttributeMissing(this, "methodName");
		}
		if (className == null && instanceType == null) {
			throw BuiltinExceptionFactory.createOneAttributeMissing(this, "className,instanceType");
		}
		if (className != null && instanceType != null) {
			throw BuiltinExceptionFactory.createConflictAttributesExcepiton(this, "className,instanceType");
		}
		initArguments(runner.getContext());
		Object instance = null;
		Method method = null;
		if (className != null) {
			method = Class.forName(className).getMethod(methodName, argumentClasses);
		} else if (instanceType != null) {
			instance =getInstanceOfType(mRegistry, Class.forName(instanceType));
			if(instance == null){
				throw BuiltinExceptionFactory.createInstanceNotFoundException(this, Class.forName(instanceType));
			}
			Class cls = instance.getClass();
			method = cls.getMethod(methodName, argumentClasses);
		}
		if(method == null)
			throw new RuntimeException("Can not find method:"+methodName+" in "+className);
		Object return_value = method.invoke(instance, argumentObjects);
		if (resultPath != null)
			runner.getContext().putObject(resultPath, return_value, true);
	}

	private void initArguments(CompositeMap context) {
		if (arguments == null)
			return;
		argumentClasses = new Class[arguments.length];
		argumentObjects = new Object[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			arguments[i].onInitialize(context,mRegistry);
			argumentClasses[i] = arguments[i].getClassType();
			argumentObjects[i] = arguments[i].getObjectValue();
		}
	}
	private Object getInstanceOfType(IObjectRegistry registry,Class type){
		Object instance = null;
		if(registry == null || type == null)
			return null;
		instance = registry.getInstanceOfType(type);
		if(instance != null)
			return instance;
		Class superClass = type.getSuperclass();
		if(superClass != null){
			instance = registry.getInstanceOfType(superClass);
			if(instance != null)
				return instance;
		}
		Class[] interfaces = type.getInterfaces();
		if(interfaces != null){
			for(int i=0;i<interfaces.length;i++){
				instance = registry.getInstanceOfType(interfaces[i]);
				if(instance != null)
					return instance;
			}
		}
		return instance;
	}

	public IObjectRegistry getRegistry() {
		return mRegistry;
	}

	public void setRegistry(IObjectRegistry mRegistry) {
		this.mRegistry = mRegistry;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public String getResultPath() {
		return resultPath;
	}

	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}

	public Argument[] getArguments() {
		return arguments;
	}

	public void setArguments(Argument[] arguments) {
		this.arguments = arguments;
	}

}
