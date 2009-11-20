/*
 * Created on 2009-5-1
 */
package uncertain.pkg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import uncertain.composite.CompositeLoader;
import uncertain.ocm.OCManager;
import uncertain.schema.SchemaManager;

/**
 * Manages ComponentPackages
 * PackageManager
 */
public class PackageManager {

    CompositeLoader        mCompositeLoader;
    OCManager              mOCManager;
    HashMap                mPackageNameMap = new HashMap();  
    SchemaManager          mSchemaManager = new SchemaManager();
    
    public static boolean isPackageDirectory( File dir ){
        if( !dir.isDirectory())
            return false;
        File config_dir = new File(dir, "config");
        if(!config_dir.exists() || !config_dir.isDirectory())
            return false;
        File pkg_xml = new File(config_dir, "package.xml");
        if(!pkg_xml.exists())
            return false;
        return true;
    }
    
    public PackageManager(){
        mCompositeLoader = CompositeLoader.createInstanceForOCM(null);
        mOCManager = OCManager.getInstance();
    }
    
    public PackageManager( CompositeLoader loader, OCManager oc_manager ){
        mCompositeLoader = loader;
        mOCManager = oc_manager;
    }
    
    public CompositeLoader getCompositeLoader(){
        return mCompositeLoader;
    }
    
    public OCManager getOCManager(){
        return mOCManager;
    }
    
    public ComponentPackage loadPackage( String path )
        throws IOException
    {
        return loadPackage( path, ComponentPackage.class);
    }

    protected void initPackage( ComponentPackage pkg){
        pkg.setPackageManager(this);
        mPackageNameMap.put(pkg.getName(), pkg); 
    }
    
    public void addPackage( ComponentPackage pkg ){
        if(pkg.getPackageManager()!=this)
            initPackage(pkg);
        SchemaManager sm = pkg.getSchemaManager();
        if(sm!=null)
            mSchemaManager.addAll(sm);
    }
    
    public ComponentPackage loadPackage( String path, Class implement_cls ) 
        throws IOException 
    {
        ComponentPackage pkg = null;
        try{
            pkg = (ComponentPackage)implement_cls.newInstance();
        }catch(Exception ex){
            throw new PackageConfigurationError("Can't create instance of "+implement_cls.getName(), ex);
        }
        initPackage(pkg);
        pkg.load(path);
        addPackage(pkg);
        return pkg;
    }
    
    public ComponentPackage getPackage( String name ){
        return (ComponentPackage)mPackageNameMap.get(name);
    }
    
    public SchemaManager getSchemaManager(){
        return mSchemaManager;
    }
    
    /**
     * Load all package under specified directory
     * @param directory root directory that contains packages to load
     * @throws IOException
     */
    public void loadPackgeDirectory( String directory )
        throws IOException
    {
        File path = new File(directory);
        if(!path.isDirectory())
            throw new IllegalArgumentException(directory + " is not a directory");
        File[] files = path.listFiles();
        for(int i=0; i<files.length; i++){
            File file = files[i];
            if(file.isDirectory() && isPackageDirectory(file))
                loadPackage(file.getAbsolutePath());
        }
    }

}
