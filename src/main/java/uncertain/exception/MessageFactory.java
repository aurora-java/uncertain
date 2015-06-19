/*
 * Created on 2011-6-15 下午04:10:08
 * $Id$
 */
package uncertain.exception;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import uncertain.proc.trace.IWithProcedureStackTrace;
import uncertain.proc.trace.TraceElement;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

public class MessageFactory {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String RESOURCES_UNCERTAIN_BUILTIN_EXCEPTIONS = "resources.UncertainBuiltinExceptions";
    public static final String DEFAULT_EXCEPTION_CONFIG_FILE_NAME = "exception_config";
    private static Locale locale = Locale.getDefault();
	private static Map messages = new HashMap();
	private static Map loaded_paths = new HashMap();
	

	public synchronized static void loadResource(String path) {
		loadResource(path, locale);
	}

    public synchronized static void loadResourceByPackage(String pkg_name ) {
        String path = pkg_name + '.' + DEFAULT_EXCEPTION_CONFIG_FILE_NAME;
        loadResource(path);
    }
    
    public static void loadResource(String path, Locale locale){
        loadResource(path, locale, false);
    }
    
    public static void loadResource( String path, boolean overwrite ){
        loadResource(path, locale, overwrite);
    }
	
    /**
     * Load language resource from class path
     * @param path resource path in java CLASSPATH convention "dir.file_name" -> "dir/file_name.properties"
     * @param locale Locale of resource file
     * @param overwrite whether reload (thus overwrite previous loaded file ) if this resource is already loaded
     */
	public static void loadResource(String path, Locale locale, boolean overwrite) {
		ResourceBundle resourceBundle = null;
        resourceBundle = ResourceBundle.getBundle(path, locale);
/*
		try{
		    resourceBundle = ResourceBundle.getBundle(path, locale);
		}catch(MissingResourceException ex){
		    resourceBundle = ResourceBundle.getBundle(path);
		}
*/		
        if(loaded_paths.containsKey(path))
            if(!overwrite){
                return;
            }
        loaded_paths.put(path, path);
		if(resourceBundle==null)
		    throw new RuntimeException("Can't load resource "+path);
		Enumeration keys = resourceBundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String fullKey = generateFullKey(key,locale);
			if(messages.containsKey(fullKey) && ! overwrite ){
				 throw new RuntimeException("The key:"+key+" from "+path+" has been defined before ! Please try another.");
			}
			messages.put(fullKey, resourceBundle.getString(key));
		}
		return;
	}

	public static GeneralException createException(String message_code,
			Throwable cause) {
		return createException(message_code,cause,null);
	}

	public static GeneralException createException(String message_code,
			Throwable cause, Object[] args) {
		return new GeneralException(message_code,args,cause);
	}
	
	public static GeneralException createException(String message_code,
            Throwable cause, Object[] args, ILocatable source) {
	    return new GeneralException(message_code, args, cause, source);
	}

	public static String getMessage(String msg_code, Locale locale,
			Object[] args) {
		String fullKey = generateFullKey(msg_code,locale);
		Object message_obj = messages.get(fullKey);
		if(message_obj == null)
			return msg_code;
		return MessageFormat.format((String)message_obj, args);
	}

	public static String getMessage(String msg_code, Object[] args) {
		return getMessage(msg_code,locale,args);
	}
	public static String generateFullKey(String msg_code, Locale locale){
		if(msg_code == null || locale == null)
			return null;
		return msg_code+locale.getLanguage();
	}
	public static Locale getLocale() {
		return locale;
	}
	public static void setLocale(Locale locale) {
		MessageFactory.locale = locale;
	}

    public static String getLocationMessage( String source, int line, int row ){
        return getMessage(MessageFactory.KEY_UNCERTAIN_EXCEPTION_SOURCE_FILE, new Object[]{source, new Integer(line), new Integer(row)} );
    }
    
    public static String getLocationMessage( ILocatable locate ){
        Location l = locate.getOriginLocation();
        if(l!=null && locate.getOriginSource()!=null)
            return getLocationMessage(locate.getOriginSource(), l.getStartLine(), l.getStartColumn());
        else if(locate.getOriginSource()!=null)
            return getLocationMessage(locate.getOriginSource(), 0, 0);
        else
            return "";
    }
	
    /**
     * Get rich exception message, add source file, code info. to origin exception message
     * @param exp Exception to format
     * @param origin_message Origin exception message. Since exception class itself may invoke
     * MessageFactory.getExceptionMessage(), to avoid dead loop, origin exception must be passed
     * as parameter.
     * @return
     */
	public static String   getExceptionMessage( Throwable exp, String origin_message ){
	    StringBuffer result = new StringBuffer();
	    if(exp instanceof ICodedException){
	        ICodedException cexp = (ICodedException)exp;
	        String code = cexp.getCode();
	        if(code!=null)
	            result.append( getMessage("uncertain.exception.code", new Object[]{code}));
	    }
	    if(exp instanceof ILocatable ){
	        ILocatable lcb = (ILocatable)exp;
	        if(lcb!=null)
	            //if(lcb.getOriginLocation()!=null)
	                result.append(getLocationMessage(lcb));
	    }
	    result.append(origin_message);
	    if(exp instanceof IWithProcedureStackTrace){
	        TraceElement element = ((IWithProcedureStackTrace)exp).getTraceElement();
	        if(element!=null){
	            String trace = element.toStackTrace();
	            result.append(LINE_SEPARATOR).append(trace);
	        }
	    }
	    return result.toString();
	}
	
	static
	{
	    loadResource(RESOURCES_UNCERTAIN_BUILTIN_EXCEPTIONS);
	}

    public static final String KEY_UNCERTAIN_EXCEPTION_SOURCE_FILE = "uncertain.exception.source_file";


}
