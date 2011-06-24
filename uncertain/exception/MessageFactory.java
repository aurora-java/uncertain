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

public class MessageFactory {

	private static Locale locale = Locale.getDefault();
	private static Map messages = new HashMap();

	public synchronized static void loadResource(String path) {
		loadResource(path, locale);
	}

	public static void loadResource(String path, Locale locale) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle(path, locale);
		Enumeration keys = resourceBundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String fullKey = generateFullKey(key,locale);
			if(messages.containsKey(fullKey)){
				 throw new RuntimeException("The key:"+key+" from "+path+" has been defined before ! Please try another.");
			}
			messages.put(fullKey, resourceBundle.getString(key));
		}
	}

	public static GeneralException createException(String message_code,
			Throwable cause) {
		return createException(message_code,cause,null);
	}

	public static GeneralException createException(String message_code,
			Throwable cause, Object[] args) {
		return new GeneralException(message_code,getMessage(message_code,args),cause);
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
	/*
	 * try{ load(path); } catch(SAXException e){ "AURORA-00001" ->
	 * "Error code: AURORA-00001 Source:a.screen Line:10 Source file {1} format...."
	 * GeneralException ex = ExceptionFactory.createExceptionFromCode(
	 * Exceptions.SCREEN_SYNTAX_ERROR ,new Object[]{path} ); }
	 */

}
