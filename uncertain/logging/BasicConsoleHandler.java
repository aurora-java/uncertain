/*
 * Created on 2009-4-20
 */
package uncertain.logging;

import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class BasicConsoleHandler extends Handler {
    

    PrintStream out = System.err;
    
    public void flush() {
       out.flush();
    }

    public synchronized void publish(LogRecord record) 
    {
       if( !super.isLoggable(record)) return;
       Formatter f = getFormatter();
       if(f==null) return;
       String content = f.format(record);
       out.print(content);
    }
    
    public void close(){
        flush();
    }

}
