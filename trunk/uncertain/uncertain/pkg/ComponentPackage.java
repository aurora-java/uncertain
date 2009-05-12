/*
 * Created on 2008-8-5
 */
package uncertain.pkg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.OCManager;

public class ComponentPackage {
    
    static final String CONFIG_PATH = "config";
    
    static final String CLASS_REGISTRY_FILE = "class-registry.xml";
    static final String PACKAGE_CONFIG_FILE = "package.xml";
    
    protected File                mBasePathFile;
    protected File                mConfigPathFile;
    protected String              mBasePath;    
    
    ClassRegistry       mClassRegistry;
    String              mVersion;
    String              mDescription;
    String              mName;
    PackageManager      mOwner;
    
    CompositeMap        mPackageConfig;
    
    protected ComponentPackage(){
        
    }
    
    public void load( String path )
        throws IOException
    {
        setBasePath(path);
        initPackage();
    }
    
    public File getConfigDirectory(){
        return mConfigPathFile;
    }
    
    protected CompositeMap loadConfigFile( File config_path, String name, boolean is_required )
        throws IOException
    {
        CompositeLoader loader = mOwner.getCompositeLoader();
        File config_file = new File( config_path, name);
        if(!config_file.exists()){
            if(is_required )
                throw new FileNotFoundException("package config file "+config_file.getPath()+" is not found");
            else 
                return null;
        }   
        String path = config_file.getPath();
        try{
            return loader.loadByFullFilePath(path);
        }catch(SAXException ex){
            throw new RuntimeException("Config file is not valid: "+path, ex);
        }
    }
    
    protected void initPackage()
        throws IOException
    {
        
        OCManager       oc_manager = mOwner.getOCManager();        
        File config_path = new File( mBasePathFile, CONFIG_PATH);
        mPackageConfig = loadConfigFile( config_path, PACKAGE_CONFIG_FILE, true);
        oc_manager.populateObject(mPackageConfig, this);        
        CompositeMap registry = loadConfigFile( config_path, CLASS_REGISTRY_FILE, false);
        if(registry!=null)
            try{
                mClassRegistry = (ClassRegistry)oc_manager.createObject(registry);
            }catch(ClassCastException ex){
                throw new RuntimeException(CLASS_REGISTRY_FILE+" is not valid, the root element should be mapped to "+ClassRegistry.class.getName());
            }
    }
    
    /**
     * @return Name of this package
     */
    public String   getName(){
        return mName;
    }
    
    /**
     * Version of this package 
     * @return
     */
    public String   getVersion(){
        return mVersion;
    }

    /**
     * @return the base path of this package
     */
    public String getBasePath() {
        return mBasePath;
    }


    /**
     * @param basePath of this package
     */
    public void setBasePath(String basePath) 
        throws FileNotFoundException
    {
        this.mBasePath = basePath;
        mBasePathFile = new File(mBasePath);
        if( !mBasePathFile.exists() )
            throw new FileNotFoundException("Package base path "+basePath+" does not exist");
        if( !mBasePathFile.isDirectory() )
            throw new IllegalArgumentException("Path "+basePath+" is not a valid directory");
        mConfigPathFile = new File( mBasePath, CONFIG_PATH);
    }

    /**
     * @return ClassRegistry instance that contains class and tag mapping in
     * this package
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
     * @param description A brief package description
     */
    public void setDescription(String description) {
        this.mDescription = description;
    }

    /**
     * @param version the version of this package, such as 2.1.4
     */
    public void setVersion(String version) {
        this.mVersion = version;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.mName = name;
    }
    
    public void setPackageManager( PackageManager owner ){
        this.mOwner = owner;
    }
    
    public PackageManager getPackageManager(){
        return mOwner;
    }

}
