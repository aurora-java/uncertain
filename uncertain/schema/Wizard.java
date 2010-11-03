package uncertain.schema;


public class Wizard extends AbstractQualifiedNamed implements IType {

	String mInstanceClass;

	public String getInstanceClass() {
		return mInstanceClass;
	}

	public void setInstanceClass(String Class) {
		this.mInstanceClass = Class;
	}

	public boolean isComplex() {
		return false;
	}

	public boolean isExtensionOf(IType another) {
		return false;
	}

	public void resolveQName(IQualifiedNameResolver resolver) {
		super.resolveQName(resolver);
	}

}