/**
 * Created on: 2004-9-14 18:01:23
 * Author:     zhoufan
 */
package uncertain.composite;

/**
 * Used by CompositeMapParser to perform extra processing for
 * attribute name or elment name. For example, make all attribute
 * name in low case.
 */
public interface NameProcessor {
	
	public String	getAttributeName(String attrib_name);
	
	public String  getElementName(String element_name);

}
