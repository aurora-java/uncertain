/*
 * Created on 2007-12-25
 */
package uncertain.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Helper method to convert stack trace to string
 * StackTraceUtil
 * @author Zhou Fan
 *
 */
public class StackTraceUtil {

    public static String toString(Throwable thr){
        Throwable t = thr.getCause();
        if(t==null) t = thr;
        StringWriter writer = new StringWriter();
        try{
            t.printStackTrace(new PrintWriter(writer));        
            return writer.toString();
        }finally{
            try{
                writer.close();
            }catch(IOException ex ){
                
            }
        }
    }
    
    public static String toString(StackTraceElement[] trace){
        StringBuffer buf = new StringBuffer();
        for(int i=0; i<trace.length; i++){
            StackTraceElement elm = trace[i];
            buf.append(elm.toString()).append("\n");
        }
        return buf.toString();
    }
    
}
