/*
 * Created on 2009-7-16
 */
package uncertain.schema;


public class SimpleType extends AbstractQualifiedNamed implements IType {
  
    Restriction     mRestriction;
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

    }
    
    public boolean isExtensionOf( IType another ){
        return false;    
    }    

}
