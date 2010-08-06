package uncertain.schema;

import uncertain.composite.QualifiedName;

public class Editor extends AbstractQualifiedNamed implements IType {

	String mInstanceClass;
	String mType;
	IType mElementType;

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

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		mType = type;
	}

	public IType getElementType() {
		Schema schema = getSchema();
		QualifiedName qname = schema.getQualifiedName(mType);
		return getSchemaManager().getType(qname);
	}

	public void resolveQName(IQualifiedNameResolver resolver) {
		if (mType != null) {
			QualifiedName type_qname = getSchema().getQualifiedName(mType);
			if (type_qname == null)
				throw new InvalidQNameError(mType);
			mElementType = getSchemaManager().getType(type_qname);
		}
		super.resolveQName(resolver);
	}

}