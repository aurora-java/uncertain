/*
 * Created on 2009-11-19 ÏÂÎç01:50:03
 * Author: Zhou Fan
 */
package uncertain.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class BasicStreamHandler extends Handler {
    
    OutputStream        mStream;
    PrintStream         mWriter;

    public BasicStreamHandler( OutputStream stream ) {
        mStream = stream;
        mWriter = new PrintStream(mStream);
    }

    public void close() throws SecurityException {
       if(mStream!=null)
           try{
               mStream.close();
           }catch(IOException ex){
               handleException(ex);
           }
    }

    public void flush() {
        try{
            mStream.flush();
        }catch(IOException ex){
            handleException(ex);
        }
    }

    public synchronized void publish(LogRecord record) 
    {
       if( !super.isLoggable(record)) return;
       Formatter f = getFormatter();
       String content = f.format(record);
       try{
           mWriter.write(content.getBytes());
           flush();
       }catch(IOException ex){
           handleException(ex);
       }
    }
    
    void handleException( Exception thrown ){
        thrown.printStackTrace();
        ErrorManager em = getErrorManager();
        if(em!=null)
            em.error("Error when writting log file", thrown, -1);
        else
            thrown.printStackTrace();
    }    

}
