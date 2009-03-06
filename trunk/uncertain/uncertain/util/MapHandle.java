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
	
	public MapHandle(Map params){
		this.params = params;
	}
	
	/**
	 * @see uncertain.util.TagParseHandle#ProcessTag(int, String)
	 */
	public String ProcessTag(int index, String tag) {
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
