package uncertain.util.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationFilter {
	
	/**
	 * Find method with specified annotation
	 * @param cls Class to search
	 * @param annotaion
	 * @param acceptor
	 */
	public static void findMethod(Class cls, Class annotation, IMethodAcceptor acceptor){
        for(Method m:cls.getMethods()){
            Annotation at = m.getAnnotation(annotation);
            if(at!=null && annotation.equals(at.annotationType())){
            	acceptor.accept(at, m);
            }
        }
		
	}

}
