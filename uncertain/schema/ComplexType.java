/*
 * Created on 2009-6-26
 */
package uncertain.schema;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import uncertain.composite.QualifiedName;

public class ComplexType extends AbstractCategorized implements IType {
    
    Attribute[]         mAttributes;
    Element[]           mElements;
    Extension[]         mExtensions;
    Array[]             mArrays;
    IValidator[]        mValidators;
    FeatureClass[]      mClasses;
    
    NamedObjectManager  mObjectManager = new NamedObjectManager();
    
    Schema              mSchema;
    List<ComplexType>   mTypeExtends;
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

    public boolean isExtensionOf( IType another ){
    	List<ComplexType> allSuperTypes = getAllSuperTypes();
    	if(allSuperTypes!= null&&allSuperTypes.contains(another))
    		return true;
        return false;    
    }
    public List getAllExtendedTypes(){
    	List<ComplexType> result = getAllSuperTypes();
    	if(result == null)
    		result = new LinkedList();
    	if(mTypeExtends != null)
    		result.addAll(mTypeExtends);
    	return result;
    }
    
    /**
     * Get all extensions, till root type. This method guarantees that types appear in bottom to top
     * order(directly extended type appear first), and each type occurs only once.
     * @return List<ComplexType> A list containing all types 
     */
    public List<ComplexType> getAllSuperTypes(){
        /** @todo use cache */
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
                addElements(t);
            }
        }
        // Add super types extended by parent types
        for( int i=0; i<extended_types.length; i++){
            final Collection super_type = extended_types[i].getAllSuperTypes();
            if( super_type!=null)
                for( Iterator it = super_type.iterator(); it.hasNext(); ){
                    ComplexType st = (ComplexType)it.next();
                    if(!map.contains(st.getQName())){
                        map.add(st.getQName());
                        result.add(st);
                        addElements(st);
                    }
                }
        }
        return result;
    }
    private void addElements(ComplexType ct){
    	Element[] elements = ct.getElements();
    	if(elements != null){
	    	for(int i=0;i<elements.length;i++){
	    		Element ele = elements[i];
	    		if(mObjectManager.getElement(ele.getQName()) == null){
	    			mObjectManager.addElements(new Element[]{ele});
	    		}
	    	}
    	}
    	Array[] arrays = ct.getArrays();
    	if(arrays != null){
	    	for(int i=0;i<arrays.length;i++){
	    		Array arr = arrays[i];
	    		if(mObjectManager.getElement(arr.getQName()) == null){
	    			mObjectManager.addElements(new Array[]{arr});
	    		}
	    	}
    	}
    }
    private ISchemaObject[] getChildArray( String type ){
        if(SchemaConstant.NAME_ATTRIBUTE.equals(type))
            return mAttributes;
        else if(SchemaConstant.NAME_ELEMENT.equals(type))
            return mElements;
        else if(SchemaConstant.NAME_ARRAY.equals(type))
            return mArrays;
        else
            throw new IllegalArgumentException("Unknown type "+type);
    }
    
    private void addObjectToList( List list, ISchemaObject[] array){
        if(array!=null)
            for( int i=0; i<array.length; i++){
                ISchemaObject obj = array[i];
                if(obj instanceof IReference){
                    IReference ref = (IReference)obj;
                    if(ref.isRef())
                        list.add(ref.getRefObject());
                    else
                        list.add(obj);
                }else
                    list.add(obj);
            }
    }

    protected List getAllChilds( String type ){
        List result = new LinkedList();
        addObjectToList(result, getChildArray(type));
        Collection types = getAllExtendedTypes();
        if(types!=null)
            for(Iterator it = types.iterator(); it.hasNext(); ){
                ComplexType t = (ComplexType)it.next();
                addObjectToList(result, t.getChildArray(type));
            }
        return result;   
        /**@todo use cache */
        // return mObjectManager.getObjectMap(SchemaConstant.TYPE_ATTRIBUTE).values();
    }
    
    /** Get all attributes, including explicitly declared and extended types */
    public List getAllAttributes(){
        /**@todo use cache */
        return getAllChilds(SchemaConstant.NAME_ATTRIBUTE);
    }

    /** Get all elements, including explicitly declared and extended types 
     * @return A List of ComplexType, not including Array
     */
    public List getAllElements(){
        /**@todo use cache */
        return getAllChilds(SchemaConstant.NAME_ELEMENT);
    }
    
    /** Get all arrays, including explicitly declared and extended types */    
    public List getAllArrays(){
        /**@todo use cache */
        return getAllChilds(SchemaConstant.NAME_ARRAY);
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
        mObjectManager.addAttributes(mAttributes);
        mObjectManager.addElements(mElements);
        mObjectManager.addElements(mArrays);
        /** @todo add all super type's mObjectManager */
    }
    
    /** @todo TBI */
    public void clearCache(){
        //mObjectManager.clear();
        doAssemble();
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
    
    private static void addList( Set set, List list, Object o ){
        if(set.contains(o))
            return;
        set.add(o);
        list.add(o);
    }
    
    private static void addListAll( Set set, List list, Collection data ){
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

    public void resolveQName(IQualifiedNameResolver resolver) {
        super.resolveQName(resolver);

    }
    
    public Element getElement( QualifiedName qname ){
        Element elm =  mObjectManager.getElement(qname);
        return elm;
    }
    
    public Attribute getAttribute( QualifiedName qname ){
        return mObjectManager.getAttribute(qname);
    }

    public Array[] getArrays() {
        return mArrays;
    }

    public void setArrays(Array[] arrays) {
        mArrays = arrays;
        addChilds(arrays);
    }
    
    public Array getArray( QualifiedName qname ){
        return mObjectManager.getArray(qname);
    }
    
    public void resolveReference( ISchemaManager manager ){
        super.resolveReference(manager);
        mObjectManager.resolveReference(manager);
    }
    public void addTypeExtend(ComplexType ct){
    	if(mTypeExtends == null)
    		mTypeExtends = new LinkedList<ComplexType>();
    	mTypeExtends.add(ct);
    	addElements(ct);
    }

}