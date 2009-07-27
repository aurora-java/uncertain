/*
 * Created on 2009-6-26
 */
package uncertain.schema;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ComplexType extends AbstractQualifiedNamed implements IType {
    
    Attribute[]         mAttributes;
    Element[]           mElements;
    Extension[]         mExtensions;
    CollectionRef[]     mCollections;
    IValidator[]        mValidators;

    Schema          mSchema;

    public boolean isComplex() {       
        return true;
    }
    
    public IValidator[] getValidators() {
        return mValidators;
    }

    public void setValidators(IValidator[] validators) {
        this.mValidators = validators;
    }

    public Attribute[] getAttributes() {
        return mAttributes;
    }

    public void setAttributes(Attribute[] attributes) {
        this.mAttributes = attributes;
        addChilds(attributes);
    }

    public Element[] getElements() {
        return mElements;
    }

    public void setElements(Element[] elements) {
        this.mElements = elements;
        addChilds(elements);
    }

    public Extension[] getExtensions() {
        return mExtensions;
    }

    public void setExtensions(Extension[] extensions) {
        this.mExtensions = extensions;
        addChilds(extensions);
    }

    public CollectionRef[] getCollections() {
        return mCollections;
    }

    public void setCollections( CollectionRef[] collections) {
        this.mCollections = collections;
    }
    
    public boolean isExtensionOf( ComplexType another ){
        return false;    
    }
    
    /**
     * Get all extensions, till root type
     * @return Collection<ComplexType> A list containing all types 
     */
    public Collection getExtendedTypes(){
        ComplexType[] super_types = loadSuperTypes();
        if(super_types==null)
            return null;
        HashMap map = new HashMap();
        for( int i=0; i<super_types.length; i++){
            ComplexType t = super_types[i];
            map.put(t.getQName(), t);
            Collection super_type = t.getExtendedTypes();
            if( super_type!=null)
                for( Iterator it = super_type.iterator(); it.hasNext(); ){
                    ComplexType st = (ComplexType)it.next();
                    map.put( st.getQName(), st);
                }                
        }
        return map.values();
    }
    
    private void addAttributes( List set, Attribute[] array){
        if(array!=null)
            for( int i=0; i<array.length; i++){
                set.add(array[i]);
            }
    }
    
    public List getAllAttributes(){
        List result = new LinkedList();
        addAttributes(result, mAttributes);
        Collection types = getExtendedTypes();
        if(types!=null)
            for(Iterator it = types.iterator(); it.hasNext(); ){
                ComplexType t = (ComplexType)it.next();
                addAttributes(result, t.mAttributes);
            }
        return result;
    }
    
    protected ComplexType[] loadSuperTypes(){
        if(mExtensions==null) return null;
        if(mExtensions.length==0) return null;
        ComplexType[] super_types = new ComplexType[mExtensions.length];
        for(int i=0; i<mExtensions.length; i++){
            Extension e = mExtensions[i];
            QualifiedName n = e.getBaseType();
            if(n==null)
                throw new SchemaError("Unknown namespace in qualified name:'"+e.getBase()+"'");
            ComplexType t = mSchema.getSchemaManager().getComplexType(n);
            if( t == null ){
                throw new SchemaError("Unknown type:" + e.getBase());
            }
            super_types[i] = t;
        }
        return super_types;
    }
    
    public void doAssemble(){
        super.doAssemble();
        mSchema = getSchema();
    }    

}
