/*
 * Created on 2011-5-5 ����10:30:42
 * $Id$
 */
package uncertain.cache;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.core.DirectoryConfig;
import uncertain.core.ILifeCycle;
import uncertain.core.UncertainEngine;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.util.FilePatternFilter;
import uncertain.util.FileUtil;

public class CacheFactoryConfig implements INamedCacheFactory, ILifeCycle {

	public static ICache getNamedCache(IObjectRegistry reg, String name) {
		INamedCacheFactory fact = (INamedCacheFactory) reg.getInstanceOfType(INamedCacheFactory.class);
		if (fact != null) {
			if (!fact.isCacheEnabled(name))
				return null;
			else
				return fact.getNamedCache(name);
		} else
			return null;
	}

	private static final CacheWrapper NOT_ENABLED_CACHE = new CacheWrapper();

	String mName;
	String cacheConfig = "cacheConfig";

	INamedCacheFactory mDefaultCacheFactory;
	String mDefaultCacheFactoryName;
	INamedCacheFactory[] mNamedCacheFactoryArray;
	CacheMapping[] mCacheMappingArray;

	// factory name -> factory instance
	Map mCacheFactoryMap = new HashMap();
	// cache name -> factory instance
	Map mPredefinedCacheMap = new HashMap();

	IObjectRegistry mRegistry;
	ILogger mLogger;
	Set<ICacheManager> mCacheManagerSet = new HashSet<ICacheManager>();

	public CacheFactoryConfig() {

	}

	public CacheFactoryConfig(IObjectRegistry reg) {
		this.mRegistry = reg;
		reg.registerInstance(INamedCacheFactory.class, this);
		reg.registerInstance(ICacheFactory.class, this);
	}

	public String getDefaultCacheFactory() {
		return mDefaultCacheFactoryName;
	}

	public void setDefaultCacheFactory(String mDefaultCacheFactory) {
		this.mDefaultCacheFactoryName = mDefaultCacheFactory;
	}

	public ICacheReader getCacheReader() {
		return mDefaultCacheFactory == null ? null : mDefaultCacheFactory.getCacheReader();
	}

	public ICacheWriter getCacheWriter() {
		return mDefaultCacheFactory == null ? null : mDefaultCacheFactory.getCacheWriter();
	}

	public ICache getCache() {
		return mDefaultCacheFactory == null ? null : mDefaultCacheFactory.getCache();
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public ICache getNamedCache(String name) {
		Object o = mPredefinedCacheMap.get(name);
		if (o == null)
			return mDefaultCacheFactory == null ? null : mDefaultCacheFactory.getNamedCache(name);
		else {
			if (NOT_ENABLED_CACHE.equals(o))
				return null;
			else
				return ((INamedCacheFactory) o).getNamedCache(name);
		}
	}

	/*
	 * public void setNamedCache(String name, ICache cache) {
	 * if(mDefaultCacheFactory!=null) mDefaultCacheFactory.setNamedCache(name,
	 * cache); }
	 */
	public void addCacheFactories(INamedCacheFactory[] factories) {
		mNamedCacheFactoryArray = factories;
	}

	public void addCacheMappings(CacheMapping[] mappings) {
		mCacheMappingArray = mappings;
	}

	public boolean startup() {
		mLogger = LoggingContext.getLogger(this.getClass().getCanonicalName(), mRegistry);
		for (int i = 0; i < mNamedCacheFactoryArray.length; i++)
			mCacheFactoryMap.put(mNamedCacheFactoryArray[i].getName(), mNamedCacheFactoryArray[i]);
		for (int i = 0; i < mCacheMappingArray.length; i++) {
			CacheMapping cm = mCacheMappingArray[i];
			INamedCacheFactory fact = (INamedCacheFactory) mCacheFactoryMap.get(cm.getCacheFactory());
			if (fact == null)
				throw new ConfigurationError("Can't find cache factory named " + cm.getCacheFactory());
			if (!cm.getEnabled()) {
				mPredefinedCacheMap.put(cm.getName(), NOT_ENABLED_CACHE);
			} else {
				mPredefinedCacheMap.put(cm.getName(), fact);
			}
		}
		if (mDefaultCacheFactoryName != null) {
			mDefaultCacheFactory = (INamedCacheFactory) mCacheFactoryMap.get(mDefaultCacheFactoryName);
			if (mDefaultCacheFactory == null)
				throw new ConfigurationError("Can't find cache factory named " + mDefaultCacheFactoryName);
		}
		if (cacheConfig != null) {
		    DirectoryConfig dcfg = (DirectoryConfig)mRegistry.getInstanceOfType(DirectoryConfig.class);
			File cacheConfigDir = new File(dcfg.getConfigDirectory(), cacheConfig);
			if (cacheConfigDir.exists()) {
				scanConfigFiles(cacheConfigDir, UncertainEngine.DEFAULT_CONFIG_FILE_PATTERN);
			}
			
		}
		return true;
	}

	public boolean isCacheEnabled(String name) {
		Object o = mPredefinedCacheMap.get(name);
		if (o == null)
			return mDefaultCacheFactory == null ? false : mDefaultCacheFactory.isCacheEnabled(name);
		else {
			if (NOT_ENABLED_CACHE.equals(o))
				return false;
			else
				return true;
		}
	}

	public void shutdown() {
		if (mNamedCacheFactoryArray == null)
			return;
		for (int i = 0; i < mNamedCacheFactoryArray.length; i++) {
			Object o = mNamedCacheFactoryArray[i];
			if (o instanceof ILifeCycle) {
				ILifeCycle s = (ILifeCycle) o;
				s.shutdown();
			}
		}
		for ( ICacheManager cm : mCacheManagerSet){
		    if( cm instanceof ILifeCycle){
		        ((ILifeCycle)cm).shutdown();
		    }
		}
	}

	/*
	 * public void onShutdown(){ shutdown(); }
	 */
	public void setNamedCache(String name, ICache cache) {
		Object o = mPredefinedCacheMap.get(name);
		if (o == null) {
			if (mDefaultCacheFactory != null)
				mDefaultCacheFactory.setNamedCache(name, cache);
		} else {
			if (NOT_ENABLED_CACHE.equals(o))
				return;
			else
				((INamedCacheFactory) o).setNamedCache(name, cache);
		}
	}

	public String getCacheConfig() {
		return cacheConfig;
	}

	public void setCacheConfig(String cacheConfig) {
		this.cacheConfig = cacheConfig;
	}

	private void scanConfigFiles(File dir, String file_pattern) 
	{
		CompositeLoader compositeLoader = CompositeLoader.createInstanceForOCM();
		OCManager ocm = (OCManager)mRegistry.getInstanceOfType(OCManager.class);
		FilePatternFilter filter = new FilePatternFilter(file_pattern);
		File cfg_files[] = dir.listFiles(filter);
		List file_list = FileUtil.getSortedList(cfg_files);
		if (cfg_files.length > 0) {
			ListIterator fit = file_list.listIterator(cfg_files.length);
			while (fit.hasPrevious()) {
				File file = (File) fit.previous();
				String file_path = file.getAbsolutePath();
				mLogger.log("Loading cache config file " + file_path);
				CompositeMap config_map = null;
			    try{
			        config_map = compositeLoader.loadByFullFilePath(file_path);
			    }catch(Exception ex){
			        throw BuiltinExceptionFactory.createResourceLoadException(null, file_path, ex);
			    }
				Object inst = ocm.createObject(config_map);
				if(inst instanceof ILifeCycle )
				    ((ILifeCycle)inst).startup();
                if (inst == null)
                    throw BuiltinExceptionFactory.createCannotCreateInstanceFromConfigException(null, file_path);
                if (!(inst instanceof ICacheManager)) 
                    throw BuiltinExceptionFactory.createInstanceTypeWrongException(file_path, ICacheManager.class, inst.getClass());

                mCacheManagerSet.add((ICacheManager) inst);
			}
		}
	}

	public void onInitialize() {
		int cacheMSize = mCacheManagerSet.size();
		CountDownLatch doneSignal = new CountDownLatch(cacheMSize);   
		for (ICacheManager cacheApp : mCacheManagerSet) {
			new Thread(new CacheInitor(cacheApp, doneSignal)).start();//线程启动了   
		}
        try {
			doneSignal.await();//等待所有的线程执行完毕   
		} catch (InterruptedException e) {
			mLogger.log(Level.SEVERE, "", e);
		}
	}
	class CacheInitor implements Runnable {   
        private CountDownLatch doneSignal;   
        private ICacheManager cacheManager;
        CacheInitor(ICacheManager cacheManager, CountDownLatch doneSignal) {   
        	this.cacheManager = cacheManager;
            this.doneSignal = doneSignal;   
        }   
        public void run() {   
            try {   
            	cacheManager.initialize();
            } catch (Exception e) {   
            	mLogger.log(Level.SEVERE, "", e);
            } finally {   
                doneSignal.countDown();   
            }   
        }   
    }   
}
