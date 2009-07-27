/*
 * Created on 2009-7-15
 */
package uncertain.schema;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.document.DocumentFactory;
import uncertain.ocm.OCManager;
import uncertain.ocm.PackageMapping;

/**
 * Holds all schema object loaded
 * @author Zhou Fan
 *
 */
public class SchemaManager implements ISchemaManager {
    
    public static final String DEFAULT_EXTENSION = "sxsd";
    
    DocumentFactory     mDocumentFactory;
    NamedObjectManager  mNamedObjectManager;  
    OCManager           mOcManager;
    
    static final PackageMapping SCHEMA_NS_PACKAGE_MAPPING 
        = new PackageMapping(ISchemaManager.SCHEMA_NAMESPACE, SchemaManager.class.getPackage().getName());
    
    
    public SchemaManager(){
        mDocumentFactory = new DocumentFactory();
        mNamedObjectManager = new NamedObjectManager();
        mOcManager = OCManager.getInstance();
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
         map = mDocumentFactory.getCompositeLoader().loadByFullFilePath( source_file );
         return loadSchema(map);
    }
    
    public Schema loadSchemaByClassPath( String class_path, String extension )
        throws IOException, SAXException
    {
        CompositeMap map = mDocumentFactory.loadCompositeMap(class_path, extension);
        return loadSchema(map);
    }
    
    public Schema loadSchemaByClassPath( String class_path )
        throws IOException, SAXException    
    {
        return loadSchemaByClassPath( class_path, DEFAULT_EXTENSION );        
    }

    
    public void addSchema( Schema schema ){
        mNamedObjectManager.putAll(schema.mNamedObjectManager);        
    }

    public OCManager getOCManager() {
        return mOcManager;
    }

    public void setOCManager(OCManager ocManager) {
        mOcManager = ocManager;
        initOCManager();
    }

}
