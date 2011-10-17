/*
 * Created on 2011-9-29 下午02:36:48
 * $Id$
 */
package uncertain.pkg;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import uncertain.composite.CompositeLoader;
import uncertain.core.DirectoryConfig;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;

public class InstanceConfig extends AbstractLocatableObject {
    
    ConfigurableInstance[]  mInstances;
    ComponentPackage        mOwnerPackage;
    IObjectRegistry         mObjRegistry;
    //OCManager               mOcManager;
    //CompositeLoader         mCompositeLoader;
    DirectoryConfig         mDirectoryConfig;
    
    File                    mLocalConfigPath;
    boolean                 mHasLocalConfigPath = false;
    
    public static class ConfigurableInstanceComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            int n1 = ((ConfigurableInstance)o1).getRequiredCount();
            int n2 = ((ConfigurableInstance)o2).getRequiredCount();
            return n1-n2;
        }
        
    };
    
    static final ConfigurableInstanceComparator COMPARATOR = new ConfigurableInstanceComparator();
    
    public InstanceConfig( IObjectRegistry reg){
        this.mObjRegistry = reg;
        /*

        mOcManager = (OCManager)mObjRegistry.getInstanceOfType(OCManager.class);
        if(mOcManager==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this, OCManager.class);

        mCompositeLoader = (CompositeLoader)mObjRegistry.getInstanceOfType(CompositeLoader.class);
        if(mDirectoryConfig==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this, CompositeLoader.class);

        */
        mDirectoryConfig = (DirectoryConfig)mObjRegistry.getInstanceOfType(DirectoryConfig.class);
        if(mDirectoryConfig==null)
            throw BuiltinExceptionFactory.createInstanceNotFoundException(this, DirectoryConfig.class);
        
        
    }

    public ConfigurableInstance[] getInstances() {
        return mInstances;
    }

    public void setInstances(ConfigurableInstance[] instances) {
        this.mInstances = instances;
    }

    public ComponentPackage getOwnerPackage() {
        return mOwnerPackage;
    }

    public void setOwnerPackage(ComponentPackage ownerPackage) {
        this.mOwnerPackage = ownerPackage;
        mLocalConfigPath = new File(mDirectoryConfig.getConfigDirectory(), ownerPackage.getName());
        mHasLocalConfigPath = mLocalConfigPath.exists();
    }
    
    private File getConfigFile( ConfigurableInstance inst ){
        File result = null;
        File pkg_path = mOwnerPackage.getConfigPath();
        
        // First check if this file exists in package config directory,
        // if there is, use it as backup
        // <PackageRootPath>/aurora.feature/config/response-cache.config
        File config_file = new File(pkg_path, inst.getConfigFile());
        if(config_file.exists())
            result = config_file;
        
        // If there exists package directory, check if config file exists in local package config dir
        // <WebRoot>/WEB-INF/aurora.feature/response-cache.config
        if(mHasLocalConfigPath && inst.getConfigFile()!=null){
            config_file = new File( mLocalConfigPath, inst.getConfigFile());
            if(config_file.exists())
                result = config_file;
        }
        
        String acf = inst.getAlternativeConfigFile();
        if(acf!=null){
            acf = mDirectoryConfig.translateRealPath(acf);
            config_file = new File(acf);
            if(config_file.exists())
                result = config_file;
        }
        return result;
    }
    
    public Collection<ConfigurableInstance> getInstantiatableList(){
        Collection<ConfigurableInstance> inst_list = new LinkedList<ConfigurableInstance>();
        if(mInstances!=null)
            for( ConfigurableInstance inst: mInstances){
                File config_file = getConfigFile(inst);
                if(config_file!=null){
                    inst.setActualConfigFile(config_file);
                    inst_list.add(inst);
                }
            }
        return inst_list;
    }
    
    public static void loadComponents( Collection<InstanceConfig> inst_config_list, IObjectRegistry obj_registry,  CompositeLoader composite_loader, OCManager oc_manager, IInstanceCreationListener listener ,boolean continueWithException){
        Map<String,ConfigurableInstance> set = new HashMap<String,ConfigurableInstance>();
        List<ConfigurableInstance> inst_list = new LinkedList<ConfigurableInstance>();
        for(InstanceConfig config: inst_config_list){
            Collection lst = config.getInstantiatableList();
            inst_list.addAll(lst);
        }
        Collections.sort(inst_list, COMPARATOR);
        int created_num = 0;
        while(inst_list.size()>0){
            created_num=0;
            for( ListIterator<ConfigurableInstance> it = inst_list.listIterator();  it.hasNext(); ){
                ConfigurableInstance inst = it.next();
                if(inst.canCreateInstance(obj_registry)){
					try {
						ConfigurableInstance ci = set.get(inst.getActualConfigFile().getAbsolutePath());
						if (ci != null)
							throw new RuntimeException("duplicated config file:" + inst.getOriginSource()
									+ ", existing from " + ci.getOriginSource());
						set.put(inst.getActualConfigFile().getAbsolutePath(), inst);
						Object instance = inst.createInstance(composite_loader, oc_manager);
						inst.registerInstance(instance, obj_registry);
						if (listener != null)
							listener.onInstanceCreate(instance, inst.getActualConfigFile());
						created_num++;
						it.remove();
					} catch (Throwable e) {
						if (!continueWithException) {
							throw new RuntimeException(e);
						}
					}
                }
            }
            if(created_num==0){
				if (!continueWithException) {
					StringBuffer error_list = new StringBuffer();
					for (ConfigurableInstance inst : inst_list)
						error_list.append(inst.getActualConfigFile().getAbsolutePath()).append(" ");
					throw BuiltinExceptionFactory.createInstanceDependencyNotMeetException(null, error_list.toString());
				} else {
					break;
				}
            }
        }
    }

}
