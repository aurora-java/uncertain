/*
 * Created on 2009-7-15
 */
package uncertain.schema;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ocm.OCManager;

/**
 * Holds all schema object loaded
 * @author Zhou Fan
 *
 */
public class SchemaManager implements ISchemaManager {
    
    public static final String DEFAULT_EXTENSION = "sxsd";
    
    static SchemaManager        DEFAULT_INSTANCE = new SchemaManager();
    static Schema               SCHEMA_FOR_SCHEMA;
    
    static void loadBuiltInSchema()
        throws IOException, SAXException
    {
        String pkg_name = SchemaManager.class.getPackage().getName();
        String schema_name = pkg_name + ".SchemaForSchema";
        SCHEMA_FOR_SCHEMA = DEFAULT_INSTANCE.loadSchemaFromClassPath(schema_name);
    }
    
    static {
        try{
            loadBuiltInSchema();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static SchemaManager getDefaultInstance(){
        return DEFAULT_INSTANCE;
    }
    
    public static Schema getSchemaForSchema(){
        return SCHEMA_FOR_SCHEMA;
    }
    
    //DocumentFactory     mDocumentFactory;
    CompositeLoader     mCompositeLoader;
    NamedObjectManager  mNamedObjectManager;  
    OCManager           mOcManager;
    
    private void _init(){
        mCompositeLoader = CompositeLoader.createInstanceForOCM(DEFAULT_EXTENSION);
        mCompositeLoader.setSaveNamespaceMapping(true);
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
        mOcManager.getClassRegistry().addPackageMapping( SchemaConstant.SCHEMA_NS_PACKAGE_MAPPING );
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
    
    Namespace[] getNameSpaces( CompositeMap map ){
        Map ns_map = map.getNamespaceMapping();
        if(ns_map!=null){
            Namespace[] ns_array = new Namespace[ns_map.size()];
            int n=0;
            for(Iterator it = ns_map.entrySet().iterator(); it.hasNext();){
                Map.Entry entry = (Map.Entry)it.next();
                String namespace = (String)entry.getKey();
                String prefix = (String)entry.getValue();
                ns_array[n] = new Namespace();
                ns_array[n].setUrl(namespace);
                ns_array[n].setPrefix(prefix);
                n++;
            }
            return ns_array;
        }else
            return null;
    }
    
    public Schema loadSchema( CompositeMap schema_config ){
        /*
        Schema schema = (Schema)mOcManager.createObject(schema_config);
        schema.setSchemaManager(this);
        */
        Schema schema = new Schema();
        schema.setSchemaManager(this);
        Namespace[] ns = getNameSpaces(schema_config);
        schema.addNameSpaces(ns);
        
        mOcManager.populateObject(schema_config, schema);
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
    
    public List getElementsOfType( IType parent_type ){
        return null;
    }

}
