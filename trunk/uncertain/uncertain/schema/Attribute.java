/*
 * Created on 2009-6-23
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;

public class Attribute extends AbstractCategorized implements IReference {

	boolean mIsRef = false;
	String mRef;
	Attribute mRefAttribute;

	String mType;
	String mUse;
	String mDefault;
	String mDocument;


	IValidator[] mValidators;

	public static Attribute createInstance(String name) {
		QualifiedName qname = new QualifiedName(null, null, name);
		Attribute attrib = new Attribute();
		attrib.setQName(qname);
		return attrib;
	}

	public Attribute() {
		super();
	}

	public String getRef() {
		return mRef;
	}

	public void setRef(String ref) {
		this.mRef = ref;
		mIsRef = mRef != null;
	}

	public IValidator[] getValidators() {
		if (mIsRef)
			return mRefAttribute.mValidators;
		else
			return mValidators;
	}

	public void setValidators(IValidator[] validators) {
		this.mValidators = validators;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		this.mType = type;
	}

	public String getUse() {
		return mUse;
	}

	public void setUse(String use) {
		this.mUse = use;
	}

	public String getDocument() {
		return mDocument;
	}

	public void setDocument(String document) {
		this.mDocument = document;
	}

	public String toString() {
		if (mRefAttribute != null)
			return mRefAttribute.toString();
		if (mQname == null)
			return "attribute";
		else
			return mQname.toString();
	}

	public boolean isRef() {
		return mIsRef;
	}

	public QualifiedName getRefQName() {
		return mQname;
	}

	public ISchemaObject getRefObject() {
		return getRefAttribute();
	}

	public Attribute getRefAttribute() {
		if (mIsRef) {
			return mRefAttribute;
		} else
			return null;
	}

	public boolean equals(Object another) {
		return this == another;
	}

	public String getDefault() {
		return mDefault;
	}

	public void setDefault(String default1) {
		mDefault = default1;
	}

	public void resolveReference(ISchemaManager manager) {
		super.resolveReference(manager);
		if (mIsRef) {
			Schema schema = getSchema();
			QualifiedName qname = schema.getQualifiedName(mRef);
			if (qname == null)
				throw new SchemaError("Can't resolve qualified name:" + mRef);
			this.mQname = qname;
			mRefAttribute = manager.getAttribute(qname);
			if (mRefAttribute == null)
				throw new SchemaError("Unresolvable attribute ref:" + mRef);
		}
	}

	public void resolveQName(IQualifiedNameResolver resolver) {
		super.resolveQName(resolver);
		if (mIsRef) {
			mQname = resolver.getQualifiedName(mRef);
			if (mQname == null)
				throw new SchemaError("Can't resolve ref qualified name:"
						+ mRef);
		}

	}

	public IType getAttributeType() {
		Schema schema = getSchema();
		QualifiedName qname = null;
		if (mType != null)
			qname = schema.getQualifiedName(mType);
		return getSchemaManager().getType(qname);
	}

	public QualifiedName getTypeQName() {
		Schema schema = getSchema();
		QualifiedName qname = null;
		if (mType != null)
			qname = schema.getQualifiedName(mType);
		return qname;
	}
}
