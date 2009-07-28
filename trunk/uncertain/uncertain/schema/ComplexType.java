/*
 * Created on 2009-6-26
 */
package uncertain.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import uncertain.composite.QualifiedName;

public class ComplexType extends AbstractQualifiedNamed implements IType {
    
    Attribute[]         mAttributes;
    Element[]           mElements;
    Extension[]         mExtensions;
    CollectionRef[]     mCollections;
    IValidator[]        mValidators;
    FeatureClass[]      mClasses;
    
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
     * Get all extensions, till root type. This method guarantees that types appear in bottom to top
     * order(directly extended type appear first), and each type occurs only once.
     * @return List<ComplexType> A list containing all types 
     */
    public List getAllExtendedTypes(){
        final ComplexType[] extended_types = loadSuperTypes();
        final Set map = new HashSet();
        
        final List result = new LinkedList();
        if(extended_types==null)
            return null;
        // Add directly extended types
        for( int i=0; i<extended_types.length; i++){
            ComplexType t = extended_types[i];
            QualifiedName qname = t.getQName(); 
            if(!map.contains(qname)){
                map.add(qname);
                result.add(t);
            }
        }
        // Add super types extended by parent types
        for( int i=0; i<extended_types.length; i++){
            final Collection super_type = extended_types[i].getAllExtendedTypes();
            if( super_type!=null)
                for( Iterator it = super_type.iterator(); it.hasNext(); ){
                    ComplexType st = (ComplexType)it.next();
                    if(!map.contains(st.getQName())){
                        map.add(st.getQName());
                        result.add(st);
                    }
                }
        }
        return result;
    }
    
    private void addAttributesToList( List list, Attribute[] array){
        if(array!=null)
            for( int i=0; i<array.length; i++){
                list.add(array[i]);
            }
    }
    
    /** Get all attributes, including attributes from extended types */
    public List getAllAttributes(){
        List result = new LinkedList();
        addAttributesToList(result, mAttributes);
        Collection types = getAllExtendedTypes();
        if(types!=null)
            for(Iterator it = types.iterator(); it.hasNext(); ){
                ComplexType t = (ComplexType)it.next();
                addAttributesToList(result, t.mAttributes);
            }
        return result;
    }
    
    protected ComplexType[] loadSuperTypes(){
        if(mExtensions==null) return null;
        if(mExtensions.length==0) return null;
        ComplexType[] super_types = new ComplexType[mExtensions.length];
        int c=0;
        for(int i=mExtensions.length-1; i>=0; i--){
            Extension extension = mExtensions[i];
            QualifiedName qname = extension.getBaseType();
            if(qname==null)
                throw new SchemaError("Unknown namespace in qualified name:'"+extension.getBase()+"'");
            ComplexType t = mSchema.getSchemaManager().getComplexType(qname);
            if( t == null ){
                throw new SchemaError("Unknown type:" + extension.getBase());
            }
            super_types[c++] = t;
        }
        return super_types;
    }
    
    public void doAssemble(){
        super.doAssemble();
        mSchema = getSchema();
    }    
    
    public FeatureClass[] getClasses(){
        return mClasses;
    }
    
    public void setClasses( FeatureClass[] classes ){
        this.mClasses = classes;
    }
    
    /** Get attached classes declared in this type
     * @return List<Class> containing all classes 
     */
    public List getAttachedClasses(){
        List list = new LinkedList();
        if(mClasses!=null){
            for(int i=0; i<mClasses.length; i++)
                list.add(mClasses[i].getType());
        }
        return list;
    }
    
    static void addList( Set set, List list, Object o ){
        if(set.contains(o))
            return;
        set.add(o);
        list.add(o);
    }
    
    static void addListAll( Set set, List list, Collection data ){
        for(Iterator it = data.iterator(); it.hasNext(); )
            addList( set, list, it.next());
    }
    
    /**
     * Get all attached classes, including all classes from super types.
     * Classes from root type will appear in the begin of result list.
     * @return List<Class> containing all classes 
     */
    public List getAllAttachedClasses(){
        final List result = new LinkedList();
        final List types = getAllExtendedTypes();
        final Set all_classes = new HashSet();
        if(types!=null)
            for(ListIterator it = types.listIterator(types.size()); it.hasPrevious(); ){
                ComplexType t = (ComplexType)it.previous();
                addListAll(all_classes, result, t.getAllAttachedClasses());
            }
        addListAll(all_classes, result, getAttachedClasses());
        return result;
    }
    
    public String toString(){
        QualifiedName qname = getQName();
        if(qname!=null)
            return qname.toString();
        else
            return "complexType";
    }
    

}
