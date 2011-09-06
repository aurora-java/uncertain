/*
 * Created on 2009-6-23
 */
package uncertain.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.QualifiedName;

public class Element extends ComplexType implements IReference {

	String mRef;
	String mUsage;
	String mType;
	String mDefault;
	String mMaxOccurs;
	String mMinOccurs;

	String mEditor;

	boolean mIsRef = false;
	ComplexType mRefType;
	IType mElementType;

	String mDisplayMask;
	String mDocument;
	String mWizard;
	QualifiedName mWizardQName;
	public String getDocument() {
		return mDocument;
	}

	public void setDocument(String document) {
		this.mDocument = document;
	}

	public String getDisplayMask() {
		return mDisplayMask;
	}

	public void setDisplayMask(String displayMask) {
		this.mDisplayMask = displayMask;
	}

	public String getRef() {
		return mRef;
	}

	public void setRef(String ref) {
		this.mRef = ref;
		mIsRef = mRef != null;
	}

	public ComplexType getRefType() {
		return mRefType;
	}

	public void doAssemble() {
		super.doAssemble();
	}

	public boolean isArray() {
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

	public boolean isRef() {
		return mIsRef;
	}

	/**
	 * @return Qualified name for referenced object
	 */
	public QualifiedName getRefQName() {
		return mQname;
	}

	/**
	 * @return Referenced object instance
	 */
	public ISchemaObject getRefObject() {
		if (mIsRef) {
			return mRefType;
		} else
			return null;
	}

	public void resolveReference(ISchemaManager manager) {
		if (mIsRef) {
			mRefType = manager.getComplexType(mQname);
			if (mRefType == null)
				throw new SchemaError("Unresolvable element ref:" + mRef);
		}
		if (mType != null) {
			QualifiedName type_qname = getSchema().getQualifiedName(mType);
			if (type_qname == null)
				throw new InvalidQNameError(mType);
			mElementType = manager.getType(type_qname);
		}
		super.resolveReference(manager);
	}

	public void resolveQName(IQualifiedNameResolver resolver) {
		if (mIsRef) {
			mQname = resolver.getQualifiedName(mRef);
			if (mQname == null)
				throw new SchemaError("Can't resolve ref qualified name:"
						+ mRef);
		} else
			super.resolveQName(resolver);
		if (mWizard != null && !mWizard.equals("")) {
			mWizardQName = resolver.getQualifiedName(mWizard);
			if (mWizardQName == null)
				throw new SchemaError("Can't resolve wizard qualified name:"
						+ mWizard);
		}
	}

	public String getEditor() {
		return mEditor;
	}

	public void setEditor(String editor) {
		mEditor = editor;
	}

	public String getDefault() {
		return mDefault;
	}

	public void setDefault(String default1) {
		mDefault = default1;
	}

	public String getMaxOccurs() {
		return mMaxOccurs;
	}

	public void setMaxOccurs(String maxOccurs) {
		mMaxOccurs = maxOccurs;
	}

	public String getMinOccurs() {
		return mMinOccurs;
	}

	public void setMinOccurs(String minOccurs) {
		mMinOccurs = minOccurs;
	}

	public List getChildElements(ISchemaManager manager) {
		List childElements = new ArrayList();
		List childList = getAllElements();
		Iterator childIterator = childList.iterator();
		while (childIterator.hasNext()) {
			Object ele = childIterator.next();
			ComplexType ele_ct = (ComplexType) ele;
			if (ele_ct instanceof Element) {
				childElements.add(ele_ct);
			} else
				childElements.addAll(manager.getElementsOfType(ele_ct));
		}
		return childElements;
	}
	public String getWizard() {
		return mWizard;
	}

	public void setWizard(String wizard) {
		mWizard = wizard;
	}
	public QualifiedName getWizardQName() {
		return mWizardQName;
	}

	public void setWizardQName(QualifiedName wizardQName) {
		this.mWizardQName = wizardQName;
	}
}
