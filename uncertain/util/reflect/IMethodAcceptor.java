package uncertain.util.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface IMethodAcceptor {
	
	public void accept( Annotation annotation, Method method);

}
