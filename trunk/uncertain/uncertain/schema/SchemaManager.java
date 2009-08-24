/*
 * Created on 2009-7-15
 */
package uncertain.schema;

import java.io.IOException;
import java.util.Collection;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ocm.OCManager;
import uncertain.ocm.PackageMapping;

/**
 * Holds all schema object loaded
 * @author Zhou Fan
 *
 */
public class SchemaManager implements ISchemaManager {
    
    public static final String DEFAULT_EXTENSION = "sxsd";
    
    //DocumentFactory     mDocumentFactory;
    CompositeLoader     mCompositeLoader;
    NamedObjectManager  mNamedObjectManager;  
    OCManager           mOcManager;
    
    static final PackageMapping SCHEMA_NS_PACKAGE_MAPPING 
        = new PackageMapping(ISchemaManager.SCHEMA_NAMESPACE, SchemaManager.class.getPackage().getName());
    
    private void _init(){
        mCompositeLoader = CompositeLoader.createInstanceForOCM(DEFAULT_EXTENSION);
        mNamedObjectManager = new NamedObjectManager();
    }
    
    public SchemaManager(){
        _init();
        mOcManager = OCManager.getInstance();
        initOCManager();
    }
    
    public SchemaManager( OCManager oc_manager ){
        _init();
        mOcManager = oc_manager;
        initOCManager();
    }
    
    private void initOCManager(){
        mOcManager.getClassRegistry().addPackageMapping( SCHEMA_NS_PACKAGE_MAPPING );
    }
    
    
    public Attribute    getAttribute( QualifiedName qname ){
        return mNamedObjectManager.getAttribute(qname);
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
    
    public Category getCategory( QualifiedName qname ){
        return mNamedObjectManager.getCategory(qname);
    }
    
    public Schema loadSchema( CompositeMap schema_config ){
        Schema schema = (Schema)mOcManager.createObject(schema_config);
        schema.setSchemaManager(this);
        schema.doAssemble();
        addSchema(schema);
        return schema;
    }
    
    public Schema loadSchemaByFile( String source_file ) 
        throws IOException, SAXException
    {
        CompositeMap map = null;
         map = mCompositeLoader.loadByFullFilePath( source_file );
         return loadSchema(map);
    }
    
    public Schema loadSchemaFromClassPath( String class_path, String extension )
        throws IOException, SAXException
    {
        CompositeMap map = mCompositeLoader.loadFromClassPath(class_path, extension); 
        return loadSchema(map);
    }
    
    public Schema loadSchemaFromClassPath( String class_path )
        throws IOException, SAXException    
    {
        return loadSchemaFromClassPath( class_path, DEFAULT_EXTENSION );        
    }

    
    public void addSchema( Schema schema ){
        mNamedObjectManager.putAll(schema.mNamedObjectManager);  
        schema.resolveReference();
    }

    public OCManager getOCManager() {
        return mOcManager;
    }

    public void setOCManager(OCManager ocManager) {
        mOcManager = ocManager;
        initOCManager();
    }
    
    public Collection getAllTypes(){
        return mNamedObjectManager.getObjectMap(SchemaConstant.TYPE_ITYPE).values();
    }
    
    /**
     * Get Element by CompositeMap's QName
     * @param data
     * @return
     */
    public Element getElement( CompositeMap data ){
        QualifiedName qname = data.getQName();
        Element element = getElement(qname);
        if(element==null){
            CompositeMap parent = data.getParent();
            if(parent!=null){
                Element parent_element = getElement(parent);
                if(parent_element!=null)
                    element = parent_element.getElement(qname);
            }
        }
        return element;
    }
    
    public void addAll( SchemaManager another ){
        mNamedObjectManager.addAll(another.mNamedObjectManager);
    }

}
