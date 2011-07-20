/*
 * Created on 2011-7-19 下午09:40:06
 * $Id$
 */
package uncertain.proc;

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
 *  <p:method-invoke instanceType="my.pkg.IRegisteredInterface" methodName="anyMethod">
 *      <p:arguments>
 *          <p:argument type="int" value="value containing ${@tag} />
 *          <p:argument type="long" path="/parameter/path_to_parameter" />
 *      </p:arguments>
 *  </p:method-invoke>
 *  
 */
public class MethodInvoke extends AbstractEntry {
    
    IObjectRegistry     mRegistry;
    
    String      methodName;
    Class       className;
    Class       instanceType;
    String      resultPath;

    public void run(ProcedureRunner runner) throws Exception {
      /*
       * // get method argument types 
       * Class[] type_array = get type array from arguments array;
       * 
       * // for static method invoke
       * if(className != null )
       *   instance = null;
       *   method= Class.forName(className).getMethod(methodName,type_array);
       *   
       * // for method invoke from registered instance 
       * else if( instanceType != null ){
       *   Object instance = mRegistry.getInstanceOfType(instanceType);
       *   Class cls = instance.getClass();
       *   method = cls.getMethod(methodName, type_array);
       *  }
       *  
       *  else throw exception;
       *  
       *  // get argument array
       *  Object[] arguments = get values from arguments arrays;
       *  
       *  Object return_value = method.invoke(instance, arguments);
       *  
       *  // set result to context
       *  if(resultPath!=null)
       *    context.putObject(resultPath, return_value, true);
       */
    }

}
