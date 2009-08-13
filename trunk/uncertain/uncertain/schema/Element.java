/*
 * Created on 2009-6-23
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;

public class Element extends ComplexType {
    
    String          mRef;    
    String          mUsage;
    String          mType;
    
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
    
    public boolean isRef(){
        return mIsRef;
    }    
    
    public ComplexType getRefType(){
        return mRefType;
    }
    
    public void doAssemble() {
        super.doAssemble();
   /*
        Schema schema = getSchema();
        if(mIsRef){
            QualifiedName qname = schema.getQualifiedName(mRef);
            if(qname!=null) 
                throw new SchemaError("Unknown element:"+mRef);
            mRefType = getSchemaManager().getComplexType(qname);
            if(mRefType==null)
                throw new SchemaError("Unresolvable element ref:"+mRef);
        }
        if(mType!=null){
            QualifiedName typename = schema.getQualifiedName(mType); 
            mElementType = getSchemaManager().getType(typename);
            // @todo check type      
            if(mElementType==null)
                throw new SchemaError("Unknown type:"+typename);
        }
    */
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

/*    
    String      editor;

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }
*/    
    
    

}
