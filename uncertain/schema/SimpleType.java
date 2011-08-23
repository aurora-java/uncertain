/*
 * Created on 2009-7-16
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;

public class SimpleType extends AbstractQualifiedNamed implements IType {

	Restriction mRestriction;
	String mEditor;
	QualifiedName mEditorQName;
	String mReferenceType;
	QualifiedName mReferenceTypeQName;

	public Restriction getRestriction() {
		return mRestriction;
	}

	public void addRestriction(Restriction restriction) {
		this.mRestriction = restriction;
		addChild(this.mRestriction);
	}

	public boolean isComplex() {
		return false;
	}

	public void doAssemble() {

	}

	public void resolveQName(IQualifiedNameResolver resolver) {
		super.resolveQName(resolver);
		if (mEditor != null && !mEditor.equals("")) {
			mEditorQName = resolver.getQualifiedName(mEditor);
			if (mEditorQName == null)
				throw new SchemaError("Can't resolve editor qualified name:"
						+ mEditor);
		}
		if (mReferenceType != null && !mReferenceType.equals("")) {
			mReferenceTypeQName = resolver.getQualifiedName(mReferenceType);
			if (mReferenceTypeQName == null)
				throw new SchemaError(
						"Can't resolve referenceType qualified name:"
								+ mReferenceType);
		}

	}

	public String getEditor() {
		return mEditor;
	}

	public void setEditor(String editor) {
		mEditor = editor;
	}

	public QualifiedName getEditorQName() {
		return mEditorQName;
	}

	public void setEditorQName(QualifiedName editorQName) {
		this.mEditorQName = editorQName;
	}

	public String getReferenceType() {
		return mReferenceType;
	}

	public void setReferenceType(String mReferenceType) {
		this.mReferenceType = mReferenceType;
	}

	public QualifiedName getReferenceTypeQName() {
		return mReferenceTypeQName;
	}

	public void setReferenceTypeQName(QualifiedName mReferenceTypeQName) {
		this.mReferenceTypeQName = mReferenceTypeQName;
	}

	public boolean isExtensionOf(IType another) {
		return false;
	}

}
