/**
 * Created on: 2004-6-9 14:15:39
 * Author:     zhoufan
 */
package uncertain.util;

/**
 *  Handles runtime error
 */
public class ExceptionHandle {

	/** handles runtime error */
	public static void onRuntimeError1( Throwable thr){
		thr.printStackTrace(System.err);
	}
	
	/** handles all exception that can't be handled by the framework */
	public static void onException1( Exception exp){
		exp.printStackTrace(System.err);
	}
	
}
