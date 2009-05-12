/*
 * Created on 2009-5-1
 */
package uncertain.pkg;

import java.io.IOException;
import java.util.HashMap;

import uncertain.composite.CharCaseProcessor;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMapParser;
import uncertain.ocm.OCManager;

public class PackageManager {

    CompositeLoader        mCompositeLoader;
    OCManager              mOCManager;
    HashMap                mPackageNameMap = new HashMap();    
    
    public PackageManager(){
        mCompositeLoader = new CompositeLoader();
        CompositeMapParser     parser = CompositeMapParser.createInstance(
                mCompositeLoader,
                new CharCaseProcessor(CharCaseProcessor.CASE_LOWER, CharCaseProcessor.CASE_UNCHANGED)
        );
        mCompositeLoader.setCompositeParser(parser);
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
    
    public void addPackage( ComponentPackage pkg ){
        pkg.setPackageManager(this);
        mPackageNameMap.put(pkg.getName(), pkg);        
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
        addPackage(pkg);
        pkg.load(path);
        return pkg;
    }
    
    public ComponentPackage getPackage( String name ){
        return (ComponentPackage)mPackageNameMap.get(name);
    }

}
