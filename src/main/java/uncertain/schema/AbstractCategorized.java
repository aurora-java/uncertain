/*
 * Created on 2009-8-25
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;

/**
 * Abstract schema object that is sub class of AbstractQualifiedNamed, and has 'Category' property
 * @author Zhou Fan
 *
 */
public class AbstractCategorized extends AbstractQualifiedNamed implements IHasReference {
    
    String          mCategoryName;
    QualifiedName   mCategoryQName;
    Category        mCategory;
    

    public String getCategory() {
        return mCategoryName;
    }

    public void setCategory(String category) {
        mCategoryName = category;
    }
    
    public Category getCategoryInstance(){
        return mCategory;
    }
    
    public void resolveQName(IQualifiedNameResolver resolver) { 
        super.resolveQName(resolver);
        if( mCategoryName!=null ){
            mCategoryQName = resolver.getQualifiedName(mCategoryName);
            if(mCategoryQName==null)
                throw new InvalidQNameError(mCategoryName);
        }
    }
    
    
    public void resolveReference( ISchemaManager manager ){    
        if(mCategoryQName!=null){
            mCategory = manager.getCategory(mCategoryQName);
            if(mCategory==null)
                throw new SchemaError("Unknown category:" + mCategoryQName);
        }
    }

}
