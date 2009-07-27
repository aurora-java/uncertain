/*
 * Created on 2009-6-23
 */
package uncertain.schema;

public class Element extends ComplexType {
    
    String          mRef;    
    String          mUsage;
    String          mType;    
    
    boolean         mIsRef = false;
    ComplexType     mRefType;

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
        if(mIsRef){
            Schema schema = getSchema();
            QualifiedName qname = schema.getQualifiedName(mRef);
            if(qname!=null) 
                throw new SchemaError("Unknown element:"+mRef);
            mRefType = getSchemaManager().getComplexType(qname);
            if(mRefType==null)
                throw new SchemaError("Unresolvable element ref:"+mRef);
        }        
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
