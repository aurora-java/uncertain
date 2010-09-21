package uncertain.schema;

import uncertain.composite.QualifiedName;

public class Restriction extends AbstractQualifiedNamed implements IType {
    
    String          mBase;
    Enumeration[]   mEnumerations;
    QualifiedName   mBaseType;
    public String getBase() {
		return mBase;
	}
    
	public void setBase(String base) {
		this.mBase = base;
	}
	public boolean isComplex() {
		return false;
	}
	
	public boolean isExtensionOf(IType another) {
		return false;
	}
    public Enumeration[] getEnumerations() {
        return mEnumerations;
    }

    public void addEnumerations(Enumeration[] enumerations) {
    	mEnumerations = enumerations;
        addChilds( mEnumerations);
    }
    public void resolveQName( IQualifiedNameResolver resolver ){    
        mBaseType = resolver.getQualifiedName(mBase);
    }
    public QualifiedName getBaseType(){
        return mBaseType;
    }

}
