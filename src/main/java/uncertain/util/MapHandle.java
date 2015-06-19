/**
 * Created on: 2004-9-5 14:30:04
 * Author:     zhoufan
 */
package uncertain.util;

import java.util.Map;

/**
 * 
 */
public class MapHandle implements TagParseHandle {
	
	Map		params;
	byte    attribCase = 0;
	
	public MapHandle(Map params){
		this.params = params;
	}
	/**
	 * 
	 * @param params
	 * @param attrib_case 0: nochange 1:upper 2:lower
	 */
	public MapHandle(Map params, byte attrib_case){
        this.params = params;
        this.attribCase = attrib_case;
    }
	
	/**
	 * @see uncertain.util.TagParseHandle#ProcessTag(int, String)
	 */
	public String ProcessTag(int index, String tag) {
	    switch(attribCase){
    	    case Character.UPPERCASE_LETTER:
    	        tag = tag.toUpperCase();
    	        break;
    	    case Character.LOWERCASE_LETTER:
    	        tag = tag.toLowerCase();
    	        break;
	    }
		Object obj = params.get(tag);
		if(obj == null) return "";
		else return obj.toString();
	}

	/**
	 * @see uncertain.util.TagParseHandle#ProcessCharacter(int, char)
	 */
	public int ProcessCharacter(int index, char ch) {
		return ch;
	}

}
