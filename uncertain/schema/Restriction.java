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
//		System.out.println("base:"+base);
		this.mBase = base;
	}
	public boolean isComplex() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isExtensionOf(IType another) {
		// TODO Auto-generated method stub
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
//    	System.out.println("restir ");
        mBaseType = resolver.getQualifiedName(mBase);
//        System.out.println(mBaseType);
    }
    public QualifiedName getBaseType(){
        return mBaseType;
    }

}
