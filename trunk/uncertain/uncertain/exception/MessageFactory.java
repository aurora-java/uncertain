/*
 * Created on 2011-6-15 下午04:10:08
 * $Id$
 */
package uncertain.exception;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public class MessageFactory {

    static Locale   locale=Locale.getDefault();

    public synchronized static void loadResource( String path ){

    }
    
    public static void loadResource( String path, Locale locale ){

    }
    
    public static GeneralException createException( String message_code, Throwable cause ){
        return null;
    }
    
    public static GeneralException createException( String message_code, Throwable cause, Object[] args){
        return null;
    }   
    
    public static String getMessage( String msg_code, Locale locale, Object[] args){
        //MessageFormat.format(msg, args);
        return null;
    }
    
    public static String getMessage( String msg_code, Object[] args){
        return null;
    }
    
    
    
    /*
     * try{
     *     load(path);
     * } catch(SAXException e){
     *     "AURORA-00001" -> "Error code: AURORA-00001 Source:a.screen Line:10 Source file {1} format...."
     *     GeneralException ex = ExceptionFactory.createExceptionFromCode( Exceptions.SCREEN_SYNTAX_ERROR ,new Object[]{path} );
     * }
     */

}
