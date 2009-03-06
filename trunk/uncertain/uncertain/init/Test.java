/*
 * Created on 2005-7-31
 */
package uncertain.init;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test
 * @author Zhou Fan
 * 
 */
public class Test {


    public static void main(String[] args) {
        Logger logger = Logger.getAnonymousLogger();
        logger.setLevel(Level.FINE);
        Handler[] handles = logger.getHandlers();        
        for(int i=0; i<handles.length; i++) {
            System.out.println(handles[i]);
            handles[i].setLevel(Level.FINE);
        }
		logger.getParent().setLevel(Level.FINE);
		logger.info("setUp called");
		logger.fine("setUp called");
		
/*
        Logger child = Logger.getLogger("uncertain.ocm");
        child.setLevel(Level.INFO);
        Logger parent = Logger.getLogger("uncertain");
        parent.setLevel(Level.WARNING);
        child.info("This is a info");
        child.warning("This is a warning");
*/
        
/*
        Handler  handle = new ConsoleHandler();
        handle.setErrorManager(new ErrorManager());
        child.addHandler( handle);
        child.throwing("uncertain.test","main",new IllegalArgumentException("testException"));
**/

        
    }
}
