/*
 * Created on 2009-7-16
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;


public class SimpleType extends AbstractQualifiedNamed implements IType {
  
    Restriction     mRestriction;
	String mEditor;
	QualifiedName mEditorQName;
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
    
    public void doAssemble(){
        
    }
    public void resolveQName(IQualifiedNameResolver resolver) {
        super.resolveQName(resolver);
		if (mEditor != null && !mEditor.equals("")) {
			mEditorQName = resolver.getQualifiedName(mEditor);
			if (mEditorQName == null)
				throw new SchemaError("Can't resolve editor qualified name:"
						+ mEditor);
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
    public boolean isExtensionOf( IType another ){
        return false;    
    }    

}
