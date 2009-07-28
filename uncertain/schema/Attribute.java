/*
 * Created on 2009-6-23
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;


public class Attribute extends AbstractQualifiedNamed {
    
    boolean         mIsRef = false;
    String          mRef;    
    String          mType;    
    String          mUsage;
    IValidator[]    mValidators;
    Attribute       mRefAttribute;
    
    public static Attribute createInstance( String name ){
        QualifiedName qname = new QualifiedName(null, null, name);
        Attribute attrib = new Attribute();
        attrib.setQName(qname);
        return attrib;
    }
    
    public Attribute(){
        super();
    }

    public String getRef() {
        return mRef;
    }

    public void setRef(String ref) {
        this.mRef = ref;
        mIsRef = mRef!=null;
    }

    public IValidator[] getValidators() {
        if( mIsRef)
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

    public String getUsage() {
        return mUsage;
    }

    public void setUsage(String usage) {
        this.mUsage = usage;
    }
    
    public String toString(){
        if(mQname==null)
            return null;
        else
            return mQname.toString();
    }

    public void doAssemble() {
        super.doAssemble();
        if(mIsRef){
            Schema schema = getSchema();
            QualifiedName qname = schema.getQualifiedName(mRef);
            if(qname!=null) 
                throw new SchemaError("Unknown attribute:"+mRef);
            mRefAttribute = getSchemaManager().getAttribute(qname);
            if(mRefAttribute==null)
                throw new SchemaError("Unresolvable attribute ref:"+mRef);
        }        
    }
    
    public boolean isRef(){
        return mIsRef;
    }
    
    public Attribute getRefAttribute(){
        return mIsRef? mRefAttribute : this ;
    }
    
    public boolean equals( Object another ){
        return this == another;
    }
   
}
