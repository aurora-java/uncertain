package uncertain.schema;

import uncertain.composite.QualifiedName;

public class TypeExtend extends ComplexType{
    String          mBase;
    QualifiedName   mBaseType;
    
    public TypeExtend(){
        
    }

    public String getBase() {
        return mBase;
    }

    public void setBase(String base) {
        this.mBase = base;
    }
    
    public void resolveQName( IQualifiedNameResolver resolver ){        
        mBaseType = resolver.getQualifiedName(mBase);
		if (mBaseType == null)
			throw new SchemaError("Can't resolve base qualified name:"
					+ mBase);
		ComplexType t = getSchemaManager().getComplexType(mBaseType);
		if(t == null)
			throw new SchemaError("Can't resolve base type:"+ mBase);
		t.addTypeExtend(this);
		super.resolveQName(resolver);
    }
    public QualifiedName getBaseType(){
        return mBaseType;
    }
}
