/**
 * Created on: 2004-6-8 11:24:19
 * Author:     zhoufan
 */
package uncertain.ocm;

/**
 * 
 */
public class NamingUtil {

	/** convert from object field name to key name
	 *  "Field_name" -> "field_name"
	 */
	public static String toAttribName(String name){
		return name.toLowerCase();
	}
	
	/** convert a string to valid java identifier
	 *  "class-name" -> "classname"
	 */
	public static String toIdentifier(String name){
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<name.length(); i++){
			char ch = name.charAt(i);
			if( Character.isJavaIdentifierPart(ch))
				buf.append(ch);
/*			
			else
				buf.append('_');
*/
				}
		return buf.toString().toLowerCase();
	}
	
	/** convert from element name to java class name
	 * 1. First character capitalized
	 * 2. Non-java identifier part indicates next character be capitalized
	 * "schema-config" -> "SchemaConfig"
	 * @param source source String for input
	 * @return converted class name
	 */
	public static String toClassName(String source){
	    int	length = source.length(), id=0;
	    StringBuffer buf = new StringBuffer();
	    boolean is_capital = true;
	    while(id<length){	        
	        char ch = source.charAt(id);
	        if(Character.isJavaIdentifierPart(ch)){
	            buf.append(is_capital?Character.toUpperCase(ch):ch);
	        	is_capital = false;
	        }else{
	            is_capital = true;
	        }
	        id++;
	    }
	    return buf.toString();
	}
	
}
