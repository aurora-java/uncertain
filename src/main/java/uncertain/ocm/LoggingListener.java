/**
 * Created on: 2004-9-9 22:02:27
 * Author:     zhoufan
 */
package uncertain.ocm;

import java.io.PrintStream;
/**
 * 
 */
public class LoggingListener implements IOCMEventListener {
	
	PrintStream out;

	/**
	 * Constructor for LoggingListener.
	 */
	public LoggingListener() {
		out = System.out;
	}
	
	/**
	 * @see uncertain.ocm.IOCMEventListener#onEvent(OCMEvent)
	 */
	public void onEvent(OCMEvent evt) {
		out.println(evt.toString());
	}

}
