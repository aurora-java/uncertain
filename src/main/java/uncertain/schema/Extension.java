/*
 * Created on 2009-6-26
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;

public class Extension  implements ISchemaObject, IQualifiedNameAware {
    
    String          mBase;
    ISchemaObject   mParent;
    QualifiedName   mBaseType;
    
    public Extension(){
        
    }

    public String getBase() {
        return mBase;
    }

    public void setBase(String base) {
        this.mBase = base;
    }
    
    public void resolveQName( IQualifiedNameResolver resolver ){        
        mBaseType = resolver.getQualifiedName(mBase);
    }
    
   public void doAssemble(){
       // nothing to do
   }
    
    /**
     * Set parent of this schema object
     * @param parent
     */
    public void setParent( ISchemaObject parent ){
        mParent = parent;
    }
    
    public ISchemaObject getParent(){
        return mParent;    
    }
    
    public QualifiedName getBaseType(){
        return mBaseType;
    }

}
