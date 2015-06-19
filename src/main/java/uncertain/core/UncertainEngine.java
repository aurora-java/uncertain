/*
 * Created on 2005-7-24
 */
package uncertain.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.event.IContextListener;
import uncertain.event.IEventDispatcher;
import uncertain.event.RuntimeContext;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.IExceptionListener;
import uncertain.logging.BasicConsoleHandler;
import uncertain.logging.BasicFileHandler;
import uncertain.logging.DummyLogger;
import uncertain.logging.ILogPathSettable;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.logging.ILoggerProviderGroup;
import uncertain.logging.ILoggingTopicRegistry;
import uncertain.logging.LoggerProvider;
import uncertain.logging.LoggingConfig;
import uncertain.logging.LoggingTopic;
import uncertain.logging.TopicManager;
import uncertain.mbean.IMBeanNameProvider;
import uncertain.mbean.IMBeanRegister;
import uncertain.mbean.IMBeanRegistrable;
import uncertain.mbean.MBeanRegister;
import uncertain.mbean.UncertainEngineWrapper;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IClassLocator;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.pkg.IInstanceCreationListener;
import uncertain.pkg.IPackageManager;
import uncertain.pkg.PackageManager;
import uncertain.pkg.PackagePath;
import uncertain.proc.IProcedureManager;
import uncertain.proc.ParticipantRegistry;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureManager;
import uncertain.proc.ProcedureRunner;
import uncertain.schema.ISchemaManager;
import uncertain.schema.SchemaManager;
import uncertain.util.FilePatternFilter;
import uncertain.util.FileUtil;
import uncertain.util.resource.ISourceFileManager;
import uncertain.util.resource.SourceFileManager;

/**
 * The facade class of uncertain object collaboration framework
 * 
 * @author Zhou Fan
 * 
 */

// TODO Refactor needed

public class UncertainEngine implements IContainer, IMBeanNameProvider {

    public static final String DEFAULT_CONFIG_FILE_PATTERN = ".*\\.config";
    public static final String DEFAULT_ENGINE_NAME = "uncertain_engine";
    public static final String UNCERTAIN_LOGGING_TOPIC = "uncertain.core";

    /* ============ builtin global instances ============= */
    CompositeLoader mCompositeLoader = CompositeLoader.createInstanceForOCM();
    OCManager mOcManager;
    ObjectRegistryImpl mObjectRegistry;
    ClassRegistry mClassRegistry;
    ParticipantRegistry mParticipantRegistry;
    Configuration mConfig;
    CompositeMap mGlobalContext;
    DirectoryConfig mDirectoryConfig;
    ProcedureManager mProcedureManager;
    SourceFileManager mSourceFileManager;
    SchemaManager     mSchemaManager;
    PackageManager mPackageManager;

    String mName = DEFAULT_ENGINE_NAME;
    Set mContextListenerSet;
    File mConfigDir;
    boolean mIsRunning = true;
    Set<String> mLoadedFiles = new HashSet<String>();
    List<ILifeCycle>    mLoadedLifeCycleList = new LinkedList<ILifeCycle>();
    
    /* =========== logging related members =================== */
    // String mLogPath;
    String mDefaultLogLevel = "WARNING";
    ILogger mLogger;
    TopicManager mTopicManager;

    // exception during init process
    Throwable mInitException;
    boolean mContinueLoadInstanceWithException = false;;
    public static UncertainEngine createInstance() {
        UncertainEngine engine = new UncertainEngine();
        engine.initialize(null);
        return engine;
    }

    /* ================== Constructors ======================================= */

    public UncertainEngine(CompositeMap config) {
        bootstrap();
        initialize(config);
    }

    public UncertainEngine() {
        bootstrap();
    }
/*
    public UncertainEngine(File config_dir) {
        bootstrap();
        setConfigDirectory(config_dir);
    }
*/
    public UncertainEngine(File config_dir, String config_file_name) {
        bootstrap();
        setConfigDirectory(config_dir);
        CompositeMap config_map = null;
        try {
            config_map = mCompositeLoader.loadByFile(new File(config_dir,
                    config_file_name).getAbsolutePath());
        } catch (Exception ex) {
            throw new RuntimeException("Error when loading configuration file "
                    + config_file_name, ex);
        }
        initialize(config_map);
    }

    /*
     * ================== Initialize methods
     * =======================================
     */

    private void setInitError(Throwable thr) {
        this.mIsRunning = false;
        this.mInitException = thr;
    }

    private void registerBuiltinInstances() {
        mObjectRegistry.registerInstanceOnce(IContainer.class, this);
        mObjectRegistry.registerInstanceOnce(UncertainEngine.class, this);
        mObjectRegistry.registerInstance(CompositeLoader.class, mCompositeLoader);
        mObjectRegistry.registerInstance(IClassLocator.class, mClassRegistry);
        mObjectRegistry.registerInstance(ClassRegistry.class, mClassRegistry);
        mObjectRegistry.registerInstance(ParticipantRegistry.class, mParticipantRegistry);
        mObjectRegistry.registerInstance(OCManager.class, mOcManager);
        mObjectRegistry.registerInstance(DirectoryConfig.class, mDirectoryConfig);
                
        mObjectRegistry.registerInstanceOnce(IObjectRegistry.class,
                mObjectRegistry);
        mObjectRegistry.registerInstanceOnce(IObjectCreator.class,
                mObjectRegistry);
        mObjectRegistry.registerInstanceOnce(ILoggingTopicRegistry.class,
                mTopicManager);
        mObjectRegistry.registerInstanceOnce(ILogger.class, mLogger);
        mObjectRegistry.registerInstanceOnce(IProcedureManager.class,
                this.getProcedureManager());
        mObjectRegistry.registerInstanceOnce(ISourceFileManager.class,
                mSourceFileManager);
        mObjectRegistry.registerInstance(IPackageManager.class, mPackageManager);

        mObjectRegistry.registerInstanceOnce(ISchemaManager.class, mSchemaManager);
    }

    private void loadBuiltinPackages() throws IOException {
        mPackageManager
                .loadPackageFromRootClassPath("uncertain_builtin_package");
    }

    private void setDefaultClassRegistry() {

        mClassRegistry.registerPackage("uncertain.proc");
        mClassRegistry.registerPackage("uncertain.ocm");
        mClassRegistry.registerPackage("uncertain.logging");
        mClassRegistry.registerPackage("uncertain.core");
        mClassRegistry.registerPackage("uncertain.core.admin");
        mClassRegistry.registerPackage("uncertain.event");
        mClassRegistry.registerPackage("uncertain.pkg");
        mClassRegistry.registerPackage("uncertain.cache");
        mClassRegistry.registerPackage("uncertain.cache.action");
        mClassRegistry.registerClass("class-registry", "uncertain.ocm",
                "ClassRegistry");
        mClassRegistry.registerClass("package-mapping", "uncertain.ocm",
                "PackageMapping");
        mClassRegistry.registerClass("class-mapping", "uncertain.ocm",
                "ClassMapping");
        mClassRegistry.registerClass("feature-attach", "uncertain.ocm",
                "FeatureAttach");
        mClassRegistry.registerClass("package-path", "uncertain.pkg", "PackagePath");

        loadInternalRegistry(LoggingConfig.LOGGING_REGISTRY_PATH);
    }

    private void loadBuiltinLoggingTopic() {
        mTopicManager.registerLoggingTopic(UNCERTAIN_LOGGING_TOPIC);
        mTopicManager.registerLoggingTopic(OCManager.LOGGING_TOPIC);
        mTopicManager.registerLoggingTopic(Configuration.LOGGING_TOPIC);
        mTopicManager.registerLoggingTopic(ProcedureRunner.LOGGING_TOPIC);
    }

    private void loadInternalRegistry(String file_path) {
        CompositeMap map = loadCompositeMap(file_path);
        if (map == null)
            throw new RuntimeException("Can't load internal resource "
                    + file_path);
        ClassRegistry reg = (ClassRegistry) mOcManager.createObject(map);
        this.mClassRegistry.addAll(reg);
    }

    protected void bootstrap() {

        mCompositeLoader = CompositeLoader.createInstanceForOCM();
        mDirectoryConfig = DirectoryConfig.createDirectoryConfig();

        // create bootstrap object instance
        mContextListenerSet = new HashSet();
        mObjectRegistry = new ObjectRegistryImpl();
        mOcManager = new OCManager(mObjectRegistry);
        mProcedureManager = new ProcedureManager(this);

        mClassRegistry = mOcManager.getClassRegistry();
        setDefaultClassRegistry();
        mParticipantRegistry = new ParticipantRegistry();
        mGlobalContext = new CompositeMap("global");
        mTopicManager = new TopicManager();

        mSourceFileManager = SourceFileManager.getInstance();
        mSourceFileManager.startup();
        mSchemaManager = new SchemaManager();
        mSchemaManager.addSchema(SchemaManager.getSchemaForSchema());
        mPackageManager = new PackageManager(mCompositeLoader, mOcManager, mSchemaManager);

        registerBuiltinInstances();
        try {
            loadBuiltinPackages();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        loadBuiltinLoggingTopic();
    }

    public void initialize(CompositeMap config) {
        // populate self from config
        if (config != null) {
            mOcManager.populateObject(config, this);
            CompositeMap child = config
                    .getChild(DirectoryConfig.KEY_PATH_CONFIG);
            if (child != null) {
                if(mDirectoryConfig==null)
                    mDirectoryConfig = DirectoryConfig.createDirectoryConfig(child);
                else
                    mDirectoryConfig.merge(child);
            }
            mDirectoryConfig.checkValidation();
        }
    }

    /*
     * ================== logging process =======================================
     */
    protected ILoggerProvider createDefaultLoggerProvider() {
        LoggerProvider clp = new LoggerProvider();
        clp.setDefaultLogLevel(mDefaultLogLevel);
        clp.addTopics(new LoggingTopic[] {
                new LoggingTopic(UNCERTAIN_LOGGING_TOPIC, Level.INFO),
                new LoggingTopic(OCManager.LOGGING_TOPIC, Level.WARNING) });

        String log_path = mDirectoryConfig.getLogDirectory();
        if (log_path == null)
            clp.addHandle(new BasicConsoleHandler());
        else {
            BasicFileHandler fh = new BasicFileHandler();
            fh.setLogPath(log_path);
            fh.setLogFilePrefix(getName());
            clp.addHandle(fh);
        }
        return clp;
    }

    protected void checkLogger() {
        ILoggerProvider logger_provider = (ILoggerProvider) mObjectRegistry
                .getInstanceOfType(ILoggerProvider.class);
        if (logger_provider == null) {
            logger_provider = createDefaultLoggerProvider();
            mObjectRegistry.registerInstance(ILoggerProvider.class,
                    logger_provider);
            mOcManager.setLoggerProvider(logger_provider);
        }
        mLogger = logger_provider.getLogger(UNCERTAIN_LOGGING_TOPIC);
    }

    public void addLoggingConfig(ILoggerProvider logging_config) {
        if (logging_config instanceof ILoggerProviderGroup) {
            ILoggerProviderGroup group = (ILoggerProviderGroup) logging_config;
            group.registerTo(mObjectRegistry);
        }

        if (logging_config instanceof IContextListener)
            mContextListenerSet.add(logging_config);
        if (logging_config instanceof ILogPathSettable) {
            ILogPathSettable lp = (ILogPathSettable) logging_config;
            String log_path = mDirectoryConfig.getLogDirectory();
            if (log_path != null && lp.getLogPath() == null) {
                lp.setLogPath(log_path);
            }
        }
    }

    public ILogger getLogger(String topic) {
        ILoggerProvider provider = (ILoggerProvider) mObjectRegistry
                .getInstanceOfType(ILoggerProvider.class);
        if (provider == null)
            return DummyLogger.getInstance();
        else
            return provider.getLogger(topic);
    }

    /* ================== Configuration components ============================ */

    public void logException(String message, Throwable thr) {
        mLogger.log(Level.SEVERE, message, thr);
        IExceptionListener exceptionListener = (IExceptionListener) mObjectRegistry
                .getInstanceOfType(IExceptionListener.class);
        if (exceptionListener != null)
            exceptionListener.onException(thr);
    }

    private boolean runInitProcedure(Procedure proc) {
        return runInitProcedure(proc, null);
    }

    private boolean runInitProcedure(Procedure proc, CompositeMap context) {
        ProcedureRunner runner = createProcedureRunner(proc);
        if (context != null)
            runner.setContext(context);
        runner.addConfiguration(mConfig);
        runner.run();
        Throwable thr = runner.getException();
        if (thr != null) {
            mLogger.log(Level.SEVERE,
                    "An error happened during initialize process");
            logException(
                    "Error when running procedure " + proc.getOriginSource() == null ? ""
                            : proc.getOriginSource(), thr);
            setInitError(thr);
            return false;
        }
        return true;
    }
    
    private boolean loadInstance( Object inst ){
        mConfig.addParticipant(inst);
        if (inst instanceof IContextListener)
            addContextListener((IContextListener) inst);
        if( inst instanceof ILifeCycle ){
            ILifeCycle c =(ILifeCycle)inst; 
            if(c.startup()){
                mLoadedLifeCycleList.add(c);
                return true;
            } else
                return false;
        }
        return true;
    }

    /**
     * Do configuration and startup works
     * 
     * @param cfg
     *            collection of CompositeMap containing configuration data.
     */
    private void doConfigure(Collection cfg) {
        // logger.info("Attached:"+this.ocManager.getClassRegistry().getFeatures(new
        // ElementIdentifier(null,"class-registry")));
        mConfig.loadConfigList(cfg);
        /*
        if (mConfig.getParticipantList().size() > 0)
            mLogger.log("Adding configuration participant "
                    + mConfig.getParticipantList());
                    */
        // regsiter global instances
        Iterator it = mConfig.getParticipantList().iterator();
        while (it.hasNext()) {
            Object inst = it.next();
            if (inst instanceof IGlobalInstance)
                getObjectRegistry().registerInstance(inst);
            if (inst instanceof IContextListener)
                addContextListener((IContextListener) inst);
            if( inst instanceof ILifeCycle ){
                ILifeCycle c =(ILifeCycle)inst; 
                if(c.startup()){
                    mLoadedLifeCycleList.add(c);
                }
            }
        }
    }
    
    private void runStartupProcedure(){
        // run procedure
        Procedure proc = loadProcedure("uncertain.core.EngineInit");
        if (proc == null)
            throw new IllegalArgumentException(
                    "Can't load uncertain/core/EngineInit from class loader");
        if (!runInitProcedure(proc))
            return;
    }
/*
    public void scanConfigFiles() {
        File cfg_dir = getConfigDirectory();
        if(cfg_dir==null && mDirectoryConfig!=null)
            if(mDirectoryConfig.getBaseDirectory()!=null){
                cfg_dir = new File(mDirectoryConfig.getBaseDirectory());
                if(!cfg_dir.exists())
                    cfg_dir = null;
            }
        if (cfg_dir != null) {
            mLogger.log("Scanning config directory " + cfg_dir);
            scanConfigFiles(cfg_dir, DEFAULT_CONFIG_FILE_PATTERN);
        }     
    }

    public void scanConfigFiles(String pattern) {
        scanConfigFiles(getConfigDirectory(), pattern);
    }

    public void scanConfigFiles(File dir, String file_pattern) {
        mIsRunning = true;
        mInitException = null;

        FilePatternFilter filter = new FilePatternFilter(file_pattern);
        File cfg_files[] = dir.listFiles(filter);
        List file_list = FileUtil.getSortedList(cfg_files);
        if (cfg_files.length > 0) {
            LinkedList cfg_list = new LinkedList();
            ListIterator fit = file_list.listIterator(cfg_files.length);
            while (fit.hasPrevious()) {
                File file = (File) fit.previous();
                if(mLoadedFiles.contains(file.getAbsolutePath()))
                    continue;
                String file_path = file.getAbsolutePath();
                mLogger.log("Loading configuration file " + file_path);
                try {
                    CompositeMap config_map = mCompositeLoader
                            .loadByFullFilePath(file_path);
                    cfg_list.add(config_map);
                } catch (Throwable thr) {
                    mLogger.log(Level.SEVERE,
                            "Can't load initialize config file " + file_path);
                    logException("Error when loading configuration file "
                            + file_path, thr);
                    setInitError(thr);
                    return;
                }
                mLoadedFiles.add(file.getAbsolutePath());
            }
            if (cfg_list.size() > 0)
                doConfigure(cfg_list);
        }
    }
*/    
    public void loadConfigFile( String full_path )
    {
        try{
            CompositeMap    data = mCompositeLoader.loadByFullFilePath(full_path);
            ArrayList lst = new ArrayList(1);
            lst.add(data);
            doConfigure(lst);
        }catch(Exception ex){
            throw new RuntimeException("Can't load file "+full_path,ex);
        }
    }

    /*
     * ================== factory methods =======================================
     */

    public Configuration createConfig() {
        Configuration conf = new Configuration(mParticipantRegistry, mOcManager);
        return conf;
    }

    public Configuration createConfig(CompositeMap cfg) {
        Configuration conf = new Configuration(mParticipantRegistry, mOcManager);
        conf.loadConfig(cfg);
        return conf;
    }

    private CompositeMap loadCompositeMap(String class_path) {
        try {

            CompositeMap m = mCompositeLoader.loadFromClassPath(class_path);
            return m;
        } catch (Exception ex) {
            throw new RuntimeException("Can't load CompositeMap from path "
                    + class_path, ex);
        }
    }
/*
    public Configuration loadConfig(String class_path) {
        CompositeMap m = loadCompositeMap(class_path);
        if (m == null)
            return null;
        Configuration config = createConfig();
        config.loadConfig(m);
        return config;
    }
*/
    private Procedure loadProcedure(String class_path) {
        try {
            return mProcedureManager.loadProcedure(class_path);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private ProcedureRunner createProcedureRunner(Procedure proc) {
        if (proc == null)
            return null;
        ProcedureRunner runner = new ProcedureRunner();
        runner.setContext(mGlobalContext);
        runner.setUncertainEngine(this);
        runner.setProcedure(proc);
        runner.setLogger(mLogger);
        return runner;
    }

    public void initContext(CompositeMap context) {
        // mLogger.log(mContextListenerSet.size()+" listeners");
        for (Iterator it = mContextListenerSet.iterator(); it.hasNext();) {
            IContextListener listener = (IContextListener) it.next();
            // mLogger.log("invoking init on "+listener);
            listener.onContextCreate(RuntimeContext.getInstance(context));
        }
    }

    public void destroyContext(CompositeMap context) {
        for (Iterator it = mContextListenerSet.iterator(); it.hasNext();) {
            IContextListener listener = (IContextListener) it.next();
            listener.onContextDestroy(RuntimeContext.getInstance(context));
        }
    }

    /*
     * ================== get/set methods =======================================
     */
    
    public void addPackages( PackagePath[] paths)
        throws IOException
    {
        for(int i=0; i<paths.length; i++)
            mPackageManager.loadPackage(paths[i]);
    }

    public Throwable getInitializeException() {
        return mInitException;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public void setConfigDirectory(File dir) {
        mConfigDir = dir;
        mDirectoryConfig.setConfigDirectory(dir.getPath());
    }

    /** for O/C mapping */
    public void addClassRegistry(ClassRegistry reg) {
        mClassRegistry.addAll(reg);
    }

    public void addClassRegistry(ClassRegistry reg, boolean override) {
        mClassRegistry.addAll(reg, override);
    }

    /**
     * @return Returns the classRegistry.
     */
    public ClassRegistry getClassRegistry() {
        return mClassRegistry;
    }

    /**
     * @return Returns the compositeLoader.
     */
    public CompositeLoader getCompositeLoader() {
        return mCompositeLoader;
    }

    /**
     * @return Returns the ocManager.
     */
    public OCManager getOcManager() {
        return mOcManager;
    }

    public IObjectCreator getObjectCreator() {
        return mObjectRegistry;
    }

    public IObjectRegistry getObjectRegistry() {
        return mObjectRegistry;
    }

    public IEventDispatcher getEventDispatcher() {
        return mConfig;
    }

/*
    public void setOcManager(OCManager ocManager) {
        this.mOcManager = ocManager;
    }

    public void setParticipantRegistry(ParticipantRegistry participantRegistry) {
        this.mParticipantRegistry = participantRegistry;
    }
*/
    /**
     * @return Returns the participantRegistry.
     */
    public ParticipantRegistry getParticipantRegistry() {
        return mParticipantRegistry;
    }


    public CompositeMap getGlobalContext() {
        return mGlobalContext;
    }

    /**
     * @return Returns the config_dir.
     */
    public File getConfigDirectory() {
        if(mConfigDir==null){
            String dir = mDirectoryConfig.getConfigDirectory();
            if(dir!=null)
                mConfigDir = new File(dir);
        }
        return mConfigDir;
    }

    public boolean getIsRunning() {
        return mIsRunning;
    }

    public void addContextListener(IContextListener listener) {
        mContextListenerSet.add(listener);
    }
    
    public void startup(){
        startup(true);
    }
    
    private void loadInstanceFromPackage(){
        mPackageManager.createInstances(mObjectRegistry, new IInstanceCreationListener(){
            
            public void onInstanceCreate( Object instance, File config_file ){
                if(!loadInstance(instance)){
                    throw BuiltinExceptionFactory.createInstanceStartError(instance, config_file.getAbsolutePath(), null);
                }
                mLoadedFiles.add(config_file.getAbsolutePath());
                mLogger.info("Loaded instance "+instance.getClass().getName()+" from "+config_file.getAbsolutePath());
            }
        },mContinueLoadInstanceWithException);
        
    }

    public void startup( boolean scan_config_files ) {
        long tick = System.currentTimeMillis();

        mIsRunning = false;
        mConfig = createConfig();
        mConfig.setLogger(mLogger);
        
        File local_config_file = new File(getConfigDirectory(), "uncertain.local.xml");
        CompositeMap local_config_map = null;
        if (local_config_file.exists()) {
            try {
                local_config_map = mCompositeLoader
                        .loadByFile(local_config_file.getAbsolutePath());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            initialize(local_config_map);
        }
        checkLogger();
        mLogger.log("Uncertain engine startup");
        
        if(scan_config_files){
            // new part, load all instances
            loadInstanceFromPackage();
            // old part
            // scanConfigFiles(DEFAULT_CONFIG_FILE_PATTERN);
        }
        runStartupProcedure();
        mIsRunning = true;
        registerMBean();
        tick = System.currentTimeMillis() - tick;
        mLogger.info("UncertainEngine startup success in " + tick +" ms");
    }

    public void shutdown() {
        if(mSourceFileManager!=null)
            mSourceFileManager.shutdown();
        if(mLogger!=null)
            mLogger.log("Uncertain engine shutdown");
        Procedure proc = loadProcedure("uncertain.core.EngineShutdown");
        if (proc == null)
            throw new IllegalArgumentException(
                    "Can't load uncertain/core/EngineShutdown.proc from class loader");
        ProcedureRunner runner = createProcedureRunner(proc);
        if(mConfig!=null)
            runner.addConfiguration(mConfig);
        runner.run();
        
        for(ILifeCycle l: mLoadedLifeCycleList){
            try{
                l.shutdown();
            }catch(Throwable thr){
                mLogger.log(Level.WARNING, "Error when shuting down instance "+l, thr);
            }
        }
        mIsRunning = false;        
    }

    public DirectoryConfig getDirectoryConfig() {
        return mDirectoryConfig;
    }

    public IProcedureManager getProcedureManager() {
        return mProcedureManager;
    }

    public String getName() {
        return mName;
    }

    public String getMBeanName(String category, String sub_name) {
        String name = "org.uncertain:instance=" + getName();
        if (category != null)
            name = name + ",category=" + category;
        if (sub_name != null)
            name = name + "," + sub_name;
        return name;
    }

    public void setName(String name) {
        mName = name;
    }

    /*
     * public String getLogPath() { return mLogPath; }
     * 
     * public void setLogPath(String logPath) { File dir = new File(logPath);
     * if(!dir.exists() || !dir.isDirectory()) throw new
     * ConfigurationError(logPath + " is not a valid logging directory");
     * mLogPath = logPath; }
     */
    public String getDefaultLogLevel() {
        return mDefaultLogLevel;
    }

    public void setDefaultLogLevel(String defaultLogLevel) {
        Level.parse(defaultLogLevel);
        mDefaultLogLevel = defaultLogLevel;
    }

    public PackageManager getPackageManager() {
        return mPackageManager;
    }
    
    public ISchemaManager getSchemaManager(){
        return mSchemaManager;
    }
    
    public void registerMBean(){
        IMBeanRegister register = MBeanRegister.getInstance();
        String name = getMBeanName(null,"name=Engine");
        UncertainEngineWrapper wrapper = new UncertainEngineWrapper(this);
        try{
            register.register(name, wrapper);
        }catch(Exception ex){
            mLogger.log(Level.WARNING, "Can't register MBean for UncertainEngine", ex);
        }
        Set reg_set = new HashSet();
        Iterator it = mObjectRegistry.getInstanceMapping().values().iterator();
        while(it.hasNext()){
            Object obj = it.next();
            if(obj instanceof IMBeanRegistrable && !reg_set.contains(obj)){
                IMBeanRegistrable reg = (IMBeanRegistrable)obj;
                try{
                    reg.registerMBean(register, this);
                    reg_set.add(obj);
                }catch(Exception ex){
                    mLogger.log(Level.WARNING, "Can't register MBean "+obj, ex);
                }
            }
        }
        reg_set.clear();
    }

	public boolean isContinueLoadConfigWithException() {
		return mContinueLoadInstanceWithException;
	}

	public void setContinueLoadConfigWithException(boolean continueLoadConfigWithException) {
		this.mContinueLoadInstanceWithException = continueLoadConfigWithException;
	}

}
