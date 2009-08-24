/*
 * Created on 2009-6-23
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;


public class Attribute extends AbstractQualifiedNamed implements IHasReference {
    
    boolean         mIsRef = false;
    String          mRef;    
    String          mType;    
    String          mUsage;
    String          mDefault;
    
    String          mCategoryName;
    QualifiedName   mCategoryQName;
    Category        mCategory;
    
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
        if( mRefAttribute!=null)
            return mRefAttribute.toString();
        if(mQname==null)
            return null;
        else
            return mQname.toString();
    }
    
    public boolean isRef(){
        return mIsRef;
    }
    
    public QualifiedName getRefQName(){
        return mQname;
    }

    public ISchemaObject getRefObject(){
        return getRefAttribute();
    }
    
    public Attribute getRefAttribute(){
        if(mIsRef){ 
            return mRefAttribute;
        }
        else
            return null;
    }
    
    public boolean equals( Object another ){
        return this == another;
    }

    public String getDefault() {
        return mDefault;
    }

    public void setDefault(String default1) {
        mDefault = default1;
    }

    
    public void resolveReference( ISchemaManager manager ){        
        if(mIsRef){
            Schema schema = getSchema();
            QualifiedName qname = schema.getQualifiedName(mRef);
            if(qname==null) 
                throw new SchemaError("Can't resolve qualified name:"+mRef);
            this.mQname = qname;
            mRefAttribute = manager.getAttribute(qname);
            if(mRefAttribute==null)
                throw new SchemaError("Unresolvable attribute ref:"+mRef);
        }         
        if(mCategoryQName!=null){
            mCategory = manager.getCategory(mCategoryQName);
            if(mCategory==null)
                throw new SchemaError("Unknown category:" + mCategoryQName);
            System.out.println(mCategory);
        }
    }    
    
    public void resolveQName(IQualifiedNameResolver resolver) {   
        if( mIsRef ){
            mQname = resolver.getQualifiedName(mRef);
            if(mQname==null)
                throw new SchemaError("Can't resolve ref qualified name:"+mRef);
        }else
            super.resolveQName(resolver);
        if( mCategoryName!=null ){
            mCategoryQName = resolver.getQualifiedName(mCategoryName);
        }        
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String category) {
        mCategoryName = category;
    }
    
    public Category getCategory(){
        return mCategory;
    }
    
   
}
