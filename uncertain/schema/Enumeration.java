/*
 * Created on 2009-7-17
 */
package uncertain.schema;


public class Enumeration extends AbstractQualifiedNamed implements IType {
    
    String          mValue;

    public String getValue() {
		return mValue;
	}
    
	public void setValue(String value) {
//		System.out.println("value:"+value);
		this.mValue = value;
	}
	public boolean isComplex() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isExtensionOf(IType another) {
		// TODO Auto-generated method stub
		return false;
	}

}
