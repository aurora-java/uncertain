/*
 * Created on 2005-7-31
 */
package uncertain.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import uncertain.logging.ILogger;

/**
 * LoggingUtil
 * @author Zhou Fan
 * 
 */
public class LoggingUtil {
    
    static Level[] level_array = new Level[20];
    
    static void putLevel(Level l){
        level_array[l.intValue()/100]=l;
    }
    
    static{
        putLevel(Level.SEVERE);
        putLevel(Level.WARNING);
        putLevel(Level.SEVERE);        
        putLevel(Level.INFO);
        putLevel(Level.CONFIG);
        putLevel(Level.FINE);
        putLevel(Level.FINER);
        putLevel(Level.FINEST);
    }
    
    public static Level getLevel(int value){
        if(value==Integer.MIN_VALUE) return Level.ALL;
        else if(value==Integer.MAX_VALUE) return Level.OFF;
        else{
            Level l = level_array[value/100];
            //System.err.println(l);
            if(l==null) throw new IllegalArgumentException("Unknown level value:"+value);
            return l;
        }
    }
    
    public static void setHandleLevels(Logger logger, Level l){
        logger.setLevel(l);
        Handler[] handlers = logger.getHandlers();
        if(handlers!=null)
        for(int i=0; i<handlers.length; i++)
            handlers[i].setLevel(l);
    }
    
    public static void logException( Throwable thr, ILogger logger, Level level ){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thr.printStackTrace(new PrintStream(baos));
        String str = baos.toString();
        logger.log(level, str);
    }
    
    public static void logException( Throwable thr, ILogger logger ){
        logException(thr, logger, Level.SEVERE );
    }    
}
