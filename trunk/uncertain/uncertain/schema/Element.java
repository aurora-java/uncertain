/*
 * Created on 2009-6-23
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;

public class Element extends ComplexType implements IReference {
    
    String          mRef;    
    String          mUsage;
    String          mType;
    String          mDefault;
    String          mMaxOccurs;
    String          mMinOccurs;
    
    String          mEditor;
    
    boolean         mIsRef = false;
    ComplexType     mRefType;
    IType           mElementType;

    public String getRef() {
        return mRef;
    }

    public void setRef(String ref) {
        this.mRef = ref;
        mIsRef = mRef!=null;
    }
    
    public ComplexType getRefType(){
        return mRefType;
    }
    
    public void doAssemble() {
        super.doAssemble();
    }    
    
    public boolean isArray(){
        return false;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }
    
    public IType getElementType(){
        Schema schema = getSchema();
        QualifiedName qname = schema.getQualifiedName(mType);
        return getSchemaManager().getType(qname);
    }
    
    public boolean isRef() {
        return mIsRef;
    }
    
    /**
     * @return  Qualified name for referenced object
     */
    public QualifiedName getRefQName(){
        return mQname;
    }
    
    /**
     * @return Referenced object instance
     */
    public ISchemaObject getRefObject(){
        return null;
    }
    
    public void resolveReference( ISchemaManager manager ){
        if(mIsRef){
            mRefType = manager.getComplexType(mQname);
            if(mRefType==null)
                throw new SchemaError("Unresolvable element ref:"+mRef);
        }
        if(mType!=null){
            QualifiedName type_qname = getSchema().getQualifiedName(mType);
            if(type_qname==null) 
                throw new InvalidQNameError(mType);
            mElementType = manager.getType(type_qname);
        }
        super.resolveReference(manager);
    }

    public void resolveQName(IQualifiedNameResolver resolver) {
        if( mIsRef ){
            mQname = resolver.getQualifiedName(mRef);
            if(mQname==null)
                throw new SchemaError("Can't resolve ref qualified name:"+mRef);
        }else
            super.resolveQName(resolver);
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

}
