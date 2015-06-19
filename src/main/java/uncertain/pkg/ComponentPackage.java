/*
 * Created on 2008-8-5
 */
package uncertain.pkg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.QualifiedName;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.MessageFactory;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.OCManager;
import uncertain.schema.ComplexType;
import uncertain.schema.IType;
import uncertain.schema.SchemaManager;

public class ComponentPackage {

    static final String CONFIG_PATH = "config";

    static final String CLASS_REGISTRY_FILE = "class-registry.xml";
    static final String PACKAGE_CONFIG_FILE = "package.xml";

    protected File mBasePathFile;
    protected File mConfigPathFile;
    protected String mBasePath;

    ClassRegistry mClassRegistry;
    String mVersion;
    String mDescription;
    String mName;
    PackageManager mOwner;
    SchemaManager mSchemaManager;
    InstanceConfig  mInstanceConfig;
    // configuration of this package
    CompositeMap mConfigData;
    PackageConfig mPackageConfig;
    // loaded schema file path
    Set mLoadedSchema = new HashSet();

    protected ComponentPackage() {

    }

    public void load(String path) throws IOException {
        setBasePath(path);
        initPackage();
    }

    public File getConfigPath() {
        return mConfigPathFile;
    }

    protected void loadSchemaFileByFullPath(String path) throws IOException {
        if (mLoadedSchema.contains(path))
            return;
        try {
            mSchemaManager.loadSchemaByFile(path);
            mLoadedSchema.add(path);
        } catch (SAXException ex) {
            throw new IOException("Error in schema config file "+path+":"+ex.getMessage());
        }
    }

    protected CompositeMap loadConfigFile(File config_path, String name,
            boolean is_required) {
        CompositeLoader loader = mOwner.getCompositeLoader();
        File config_file = new File(config_path, name);
        if (!config_file.exists()) {
            if (is_required)
                throw BuiltinExceptionFactory.createRequiredFileNotFound(config_file.getAbsolutePath());
            else
                return null;
        }
        String path = config_file.getPath();
        return loader.silently().loadByFullFilePath(path);
    }

    protected void loadSchemaFile(File config_path) throws IOException {
        // see if package config contains schema file
        if (mPackageConfig != null) {
            String[] schema_files = mPackageConfig.getSchemaFiles();
            if (schema_files != null)
                for (int i = 0; i < schema_files.length; i++){
                    //System.out.println(schema_files[i]);
                    loadSchemaFileByFullPath((new File(config_path,
                            schema_files[i])).getAbsolutePath());
                }
        }
        // load all schema in config directory
        String extension = "." + SchemaManager.DEFAULT_EXTENSION;
        File[] files = config_path.listFiles();
        if (files == null)
            return;
        for (int i = 0; i < files.length; i++) {
            String file = files[i].getName().toLowerCase();
            if (file.endsWith(extension)) {
                try {
                    loadSchemaFileByFullPath(files[i].getAbsolutePath());
                } catch (Exception ex) {
                    throw new RuntimeException(
                            "Error when parsing schema file "
                                    + files[i].getAbsolutePath(), ex);
                }
            }
        }
        // add attached feature classes
        Collection cl = mSchemaManager.getAllTypes();
        if (cl != null) {
            for (Iterator it = cl.iterator(); it.hasNext();) {
                IType type = (IType) it.next();
                if (type instanceof ComplexType) {
                    ComplexType complex_type = (ComplexType) type;
                    List lst = complex_type.getAllAttachedClasses();
                    if (lst != null && lst.size() > 0) {
                        QualifiedName qname = complex_type.getQName();
                        for (Iterator tit = lst.iterator(); tit.hasNext();)
                            mClassRegistry.attachFeature(qname, (Class) tit
                                    .next());
                    }
                }
            }
        }
    }
    
    private void loadResources(){
        if(mPackageConfig==null)
            return;
        String[] resources = mPackageConfig.getResourceFiles();
        if(resources==null)
            return;
        for(int i=0; i<resources.length; i++)
            MessageFactory.loadResource(resources[i], false);
    }
    
    private Object createOptionalObject( String config_file, Class type ){
        File config_path = new File(mBasePathFile, CONFIG_PATH);
        OCManager oc_manager = mOwner.getOCManager();
        File source_file = new File(config_path,config_file);
        if(!source_file.exists())
            return null;
        CompositeMap data = loadConfigFile(config_path, config_file, false);
        if ( data != null){
            Object inst = oc_manager.createObject(data);
            if(inst==null)
                throw BuiltinExceptionFactory.createCannotCreateInstanceFromConfigException(null, source_file.getAbsolutePath());
            if(!type.isInstance(inst))
                throw BuiltinExceptionFactory.createInstanceTypeWrongException(source_file.getAbsolutePath(), type, inst.getClass());
            return inst;
        }
        return null;
    }

    protected void initPackage() throws IOException {

        OCManager oc_manager = mOwner.getOCManager();
        File config_path = new File(mBasePathFile, CONFIG_PATH);
        mConfigData = loadConfigFile(config_path, PACKAGE_CONFIG_FILE, false);
        if (mConfigData != null) {
            oc_manager.populateObject(mConfigData, this);
            mPackageConfig = (PackageConfig) DynamicObject.cast(mConfigData,
                    PackageConfig.class);
        } else {
            // Do default init work
            setName(mBasePathFile.getName());
        }

        mClassRegistry = (ClassRegistry)createOptionalObject(CLASS_REGISTRY_FILE,ClassRegistry.class);
        mInstanceConfig = (InstanceConfig)createOptionalObject("instance.xml", InstanceConfig.class);
        if(mInstanceConfig!=null)
            mInstanceConfig.setOwnerPackage(this);

        /*
        CompositeMap registry = loadConfigFile(config_path,
                CLASS_REGISTRY_FILE, false);
        if (registry != null)
            try {
                mClassRegistry = (ClassRegistry) oc_manager
                        .createObject(registry);
            } catch (ClassCastException ex) {
                throw new RuntimeException(
                        CLASS_REGISTRY_FILE
                                + " is not valid, the root element should be mapped to "
                                + ClassRegistry.class.getName());
            }
 */       
        // create SchemaManager
        mSchemaManager = new SchemaManager(oc_manager);
        mSchemaManager.setParent(mOwner.getSchemaManager());
        loadSchemaFile(config_path);
        loadResources();
        
    }

    /**
     * @return Name of this package
     */
    public String getName() {
        return mName;
    }

    /**
     * Version of this package
     * 
     * @return
     */
    public String getVersion() {
        return mVersion;
    }

    /**
     * @return the base path of this package
     */
    public String getBasePath() {
        return mBasePath;
    }

    /**
     * @param basePath
     *            of this package
     */
    public void setBasePath(String basePath) throws FileNotFoundException {
        this.mBasePath = basePath;
        mBasePathFile = new File(mBasePath);
        if (!mBasePathFile.exists())
            throw new FileNotFoundException("Package base path " + basePath
                    + " does not exist");
        if (!mBasePathFile.isDirectory())
            throw new IllegalArgumentException("Path " + basePath
                    + " is not a valid directory");
        mConfigPathFile = new File(mBasePath, CONFIG_PATH);
    }

    /**
     * @return ClassRegistry instance that contains class and tag mapping in
     *         this package
     */
    public ClassRegistry getClassRegistry() {
        return mClassRegistry;
    }

    /**
     * @return A brief package description
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * @param description
     *            A brief package description
     */
    public void setDescription(String description) {
        this.mDescription = description;
    }

    /**
     * @param version
     *            the version of this package, such as 2.1.4
     */
    public void setVersion(String version) {
        this.mVersion = version;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.mName = name;
    }

    public void setPackageManager(PackageManager owner) {
        this.mOwner = owner;
    }

    public PackageManager getPackageManager() {
        return mOwner;
    }

    public SchemaManager getSchemaManager() {
        return mSchemaManager;
    }
    
    public InstanceConfig getInstanceConfig(){
        return mInstanceConfig;
    }
    
    public String toString(){
        return "package "+ mName +" from " + mBasePathFile==null?"(null)":mBasePathFile.getAbsolutePath();
    }

}
