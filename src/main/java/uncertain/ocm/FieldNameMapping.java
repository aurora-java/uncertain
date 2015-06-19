/**
 * Created on: 2004-6-7 15:39:04
 * Author:     zhoufan
 */
package uncertain.ocm;

/**
 *  Simple class to encapsulate a field name mapping.
 *  When perform OC mapping, the name of key in container
 *  can be different in actual field name in object
 */
public class FieldNameMapping {
	
	String origin_name;
	String mapped_name;
	
	/**
	 * Default constructor
	 */
	public FieldNameMapping(){
	}

	/**
	 * Constructor with full parameter.
	 */
	public FieldNameMapping(String _origin_name, String _mapped_name) {
		this.origin_name = _origin_name;
		this.mapped_name = _mapped_name;
	}
	
	public String getOriginName(){
		return origin_name;
	}
	
	public String getMappedName(){
		return mapped_name;
	}

}
