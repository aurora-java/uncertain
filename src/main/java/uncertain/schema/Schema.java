/*
 * Created on 2009-6-26
 */
package uncertain.schema;

import java.util.HashMap;
import java.util.Map;

import uncertain.composite.QualifiedName;

public class Schema extends AbstractSchemaObject implements IQualifiedNameResolver {
    


    /* fields for getter/setter */
    Namespace[]         mNameSpaces;
    IType[]             mTypes;
    Attribute[]         mAttributes;
    Element[]           mElements;
    Category[]          mCategories;
    Editor[]          	mEditors;
    Wizard[]          	mWizards;
    TypeExtend[]        mTypeExtends;
    String              mTargetNamespace;
    ISchemaManager      mSchemaManager;

 /*   
    // QName -> ISchemaObject
    Map[]               mChildMapArray = new Map[10];
  */
    // Holds all schema objects declared in this schema
    NamedObjectManager  mNamedObjectManager;
    
    // prefix -> Namespace instance
    Map                 mNameSpaceMap = new HashMap();
    
    public Schema(){
        super();
        mNamedObjectManager = new NamedObjectManager();
    }

    protected void addChildArray( IQualifiedNamed[] childs, int map_index ){
        for( int i=0; i<childs.length; i++ ){
            addChild(childs[i]);
            mNamedObjectManager.addNamedObject(map_index, childs[i]);            
        }        
    }

    public Namespace[] getNameSpaces() {
        return mNameSpaces;
    }
    
    public void addNameSpaces(Namespace[] nameSpaces) {
        mNameSpaces = nameSpaces;
        for( int i=0; i<mNameSpaces.length; i++ ){
            mNameSpaceMap.put(mNameSpaces[i].getPrefix(), mNameSpaces[i] );
            addChild(mNameSpaces[i]);
        }
    }

    public IType[] getTypes() {
        return mTypes;
    }

    public void addTypes(IType[] types) {
        mTypes = types;
        addChildArray( types, SchemaConstant.TYPE_ITYPE );
    }

    public Attribute[] getAttributes() {
        return mAttributes;
    }

    public void addAttributes(Attribute[] attributes) {
        mAttributes = attributes;
        addChildArray( attributes, SchemaConstant.TYPE_ATTRIBUTE );
    }

    public Element[] getElements() {
        return mElements;
    }

    public void addElements(Element[] elements) {
        mElements = elements;
        addChildArray( elements, SchemaConstant.TYPE_ITYPE );
    }

    public Category[] getCategories() {
        return mCategories;
    }

    public void addCategories(Category[] categories) {
        mCategories = categories;
        addChildArray( categories, SchemaConstant.TYPE_CATEGORIE );
    }
    
    public Editor[] geEditors() {
        return mEditors;
    }

    public void addEditors(Editor[] Editors) {
    	mEditors = Editors;
        addChildArray( Editors, SchemaConstant.TYPE_EDITOR );
    }
    
    public Wizard[] getWizards() {
        return mWizards;
    }

    public void addWizards(Wizard[] Wizards) {
    	mWizards = Wizards;
        addChildArray( Wizards, SchemaConstant.TYPE_WIZARD );
    }
    public TypeExtend[] getTypeExtends() {
        return mTypeExtends;
    }

    public void addTypeExtends(TypeExtend[] typeExtends) {
    	mTypeExtends = typeExtends;
        addChildArray( mTypeExtends, SchemaConstant.TYPE_ITYPE );
    }

    public String getTargetNamespace() {
        return mTargetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.mTargetNamespace = targetNamespace;
    }
    
    /**
     * Get namespace by prefix
     * @param prefix
     * @return Namespace instance corresponding to this prefix
     */
    public Namespace getNamespace( String prefix ){
        Namespace ns = (Namespace)mNameSpaceMap.get(prefix);
        return ns;
    }
    
    public QualifiedName    getQualifiedName( String prefix, String name ){
        assert name!=null;
        if( prefix == null){
            return new QualifiedName( null, null, name );
        }else{
            Namespace ns = (Namespace)mNameSpaceMap.get(prefix);
            if(ns==null)
                return null;
            else
                return new QualifiedName( ns.getPrefix(), ns.getUrl(), name );
        }
    }
    
    public QualifiedName    getQualifiedName( String name ){
        assert name!=null;
        int index = name.indexOf(':');
        if( index<0 )
            return getQualifiedName( null, name );
        String prefix = name.substring(0,index);
        String element_name = name.substring(index+1, name.length());
        return getQualifiedName( prefix, element_name );
    }

    public void setSchemaManager(ISchemaManager schemaManager) {
        mSchemaManager = schemaManager;
    }
    
    public ISchemaManager getSchemaManager(){
        return mSchemaManager;
    }
    
    public Schema getSchema(){
        return this;
    }

    
    public Attribute    getAttribute( QualifiedName qname ){
        Attribute attrib = mNamedObjectManager.getAttribute(qname);
        return attrib;
    }
    
    public Element      getElement( QualifiedName qname ){
        return mNamedObjectManager.getElement(qname);
    }
    
    public ComplexType  getComplexType( QualifiedName qname ){
        return mNamedObjectManager.getComplexType(qname);
    }
    
    public SimpleType  getSimpleType( QualifiedName qname ){
        return mNamedObjectManager.getSimpleType(qname);
    }
    
    public IType    getType( QualifiedName qname ){
        return mNamedObjectManager.getType(qname);
    }   
    
    public Map getTypeMap(){
        return mNamedObjectManager.getObjectMap(SchemaConstant.TYPE_ITYPE);
    }

    public void addChild(ISchemaObject child) {
        super.addChild(child);
        if(child instanceof IQualifiedNamed){
            ((IQualifiedNamed)child).resolveQName(this);
        }
    }
    
    public void resolveReference(){
        mNamedObjectManager.resolveReference( mSchemaManager );
    }
}
