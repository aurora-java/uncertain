/*
 * Created on 2009-7-17
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;

public class Category extends AbstractQualifiedNamed implements IHasReference {
    
    String          mParentName;
    String          mTitle;
    String          mDescription;
    QualifiedName   mParentQName;
    Category        mParentCategory;

    public String getParentName() {
        return mParentName;
    }

    /**
     * QName of parent category
     */
    public void setParentName(String parent) {
        this.mParentName = parent;
    }
    
    public Category getParentCategory(){
        return mParentCategory;
    }

    /**
     * Title of category
     */
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void resolveQName(IQualifiedNameResolver resolver) {
        super.resolveQName(resolver);
        if(mParentName!=null){
            mParentQName = resolver.getQualifiedName(mParentName);
            if(mParentQName==null)
                throw new InvalidQNameError(mParentName);
        }
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
    
    public void resolveReference( ISchemaManager manager ){
        if(mParentQName!=null){
            mParentCategory = manager.getCategory(mParentQName);
            if(mParentCategory==null)
                throw new SchemaError("Unknown category:"+mParentQName);
        }
    }

}
