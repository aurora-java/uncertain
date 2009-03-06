/**
 * Created on: 2004-9-14 18:46:15
 * Author:     zhoufan
 */
package uncertain.composite;

/**
 * 
 */
public class CharCaseProcessor implements NameProcessor {
	
	public static final int	CASE_LOWER = 1;
	public static final int	CASE_UPPER = 2;
	public static final int	CASE_UNCHANGED = 0;
	
	int	attrib_case;
	int	element_case;
	
	/**
	 * Create processor with specified character case, can be 
	 * the following contant defined in CharCaseProcessor:
	 * CASE_LOWER: change to lower case
	 * CASE_UPPER: change to upper case
	 * CASE_UNCHANGED: leave origin input unchanged
	 */
	public CharCaseProcessor(int attrib_case, int element_case){
		this.attrib_case = attrib_case;
		this.element_case = element_case;
	}
	
	/**
	 * Default constructor, set all input name to lower case
	 */
	public CharCaseProcessor() {
		this(CASE_LOWER, CASE_LOWER);
	}

	/**
	 * @see uncertain.composite.NameProcessor#getAttributeName(String)
	 */
	public String getAttributeName(String attrib_name) {
		if(attrib_name == null)return null;
		switch(attrib_case){
			case CASE_LOWER:
				return attrib_name.toLowerCase();
			case CASE_UPPER:
				return attrib_name.toUpperCase();
			default:
				return attrib_name;
		}
	}

	/**
	 * @see uncertain.composite.NameProcessor#getElementName(String)
	 */
	public String getElementName(String element_name) {
		if(element_name == null)return null;
		switch(element_case){
			case CASE_LOWER:
				return element_name.toLowerCase();
			case CASE_UPPER:
				return element_name.toUpperCase();
			default:
				return element_name;
		}		
	}

}
