/*
 * Created on 2005-7-24
 */
package uncertain.core;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;

import org.xml.sax.SAXException;

import uncertain.cache.ICache;
import uncertain.cache.ICacheFactory;
import uncertain.cache.INamedCacheFactory;
import uncertain.cache.MapBasedCacheFactory;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.event.Configuration;
import uncertain.event.IContextListener;
import uncertain.event.RuntimeContext;
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
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IChildContainerAcceptable;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.proc.IProcedureManager;
import uncertain.proc.ParticipantRegistry;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureManager;
import uncertain.proc.ProcedureRunner;
import uncertain.util.FilePatternFilter;

/**
 * The facade class of uncertain object collaboration framework
 * @author Zhou Fan
 * 
 */
public class UncertainEngine implements IChildContainerAcceptable {

    public static final String UNCERTAIN_NAMESPACE = "http://engine.uncertain.org/defaultns";
    public static final String DEFAULT_CONFIG_FILE_PATTERN = ".*\\.config";
    public static final String DEFAULT_ENGINE_NAME = "uncertain_engine";
    public static final String UNCERTAIN_LOGGING_TOPIC = "uncertain.core";
    //public static final String UNCERTAIN_ERROR_TOPIC = "error";
    
    String                  mName = DEFAULT_ENGINE_NAME;
    //CompositeMapParser		mCompositeParser;
    CompositeLoader			mCompositeLoader = CompositeLoader.createInstanceForOCM();
    OCManager				mOcManager;
    ObjectRegistryImpl		mObjectRegistry;
    ClassRegistry			mClassRegistry;
    ParticipantRegistry		mParticipantRegistry;
    Configuration           mConfig;
    CompositeMap            mGlobalContext;
    DirectoryConfig         mDirectoryConfig;
    ProcedureManager        mProcedureManager;
    Set                     mContextListenerSet;
    LinkedList				mExtraConfig = new LinkedList();
    File					mConfigDir;    
    boolean                 mIsRunning = true;
    /* =========== logging related members =================== */
    String                  mLogPath;
    String                  mDefaultLogLevel = "WARNING";
    ILogger                 mLogger;    
    TopicManager            mTopicManager;
    /* =========== cache ===================================== */
    INamedCacheFactory      mNamedCacheFactory;
    boolean                 mCacheConfigFiles = false;

    public static UncertainEngine createInstance(){
        UncertainEngine     engine = new UncertainEngine();
        engine.initialize(null);
        return engine;
    }
    
    /* ================== Constructors ======================================= */
    
    public UncertainEngine(CompositeMap config){
        bootstrap();
        initialize(config);
    }
    
    public UncertainEngine(){
        bootstrap();
    }
    
    public UncertainEngine(File config_dir){
        bootstrap();
        setConfigDirectory(config_dir);
    }
    
    public UncertainEngine(File config_dir, String config_file_name) throws IOException {
        bootstrap();
        setConfigDirectory(config_dir);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(new File(config_dir, config_file_name));
            CompositeMap config_map = mCompositeLoader.loadFromStream(fis);
            initialize(config_map);
        }catch(SAXException ex){
            logException("Error when reading configuration file "+config_file_name,ex);
        }        
        finally{
            if(fis!=null) fis.close();
        }        
    }
        
    
    /* ================== Initialize methods ======================================= */    
    
    private void registerBuiltinInstances(){
        //objectSpace.registerParameter(compositeParser);
        mObjectRegistry.registerInstance(mCompositeLoader);
        mObjectRegistry.registerInstance(mClassRegistry);
        mObjectRegistry.registerInstance(mParticipantRegistry);
        mObjectRegistry.registerInstance(mOcManager);           
        mObjectRegistry.registerInstanceOnce(UncertainEngine.class,this);
        mObjectRegistry.registerInstanceOnce(IObjectRegistry.class, mObjectRegistry);  
        mObjectRegistry.registerInstanceOnce(IObjectCreator.class, mObjectRegistry);
        mObjectRegistry.registerInstanceOnce(ILoggingTopicRegistry.class, mTopicManager);
        mObjectRegistry.registerInstanceOnce(ILogger.class, mLogger);
        mObjectRegistry.registerInstanceOnce(IProcedureManager.class, this.getProcedureManager());
        mObjectRegistry.registerInstance(ICacheFactory.class, mNamedCacheFactory);
        mObjectRegistry.registerInstance(INamedCacheFactory.class, mNamedCacheFactory);
    }
    
    private void setDefaultClassRegistry(){
        
        mClassRegistry.registerPackage("uncertain.proc");
        mClassRegistry.registerPackage("uncertain.ocm");
        mClassRegistry.registerPackage("uncertain.logging");
        mClassRegistry.registerPackage("uncertain.core");
        mClassRegistry.registerPackage("uncertain.core.admin");
        mClassRegistry.registerPackage("uncertain.event");
        
        //mClassRegistry.registerClass("document-loader","uncertain.composite","CompositeLoader");
        //mClassRegistry.registerClass("document-path","uncertain.composite","CompositeLoader");
        mClassRegistry.registerClass("class-registry","uncertain.ocm","ClassRegistry");
        mClassRegistry.registerClass("package-mapping","uncertain.ocm","PackageMapping");
        mClassRegistry.registerClass("class-mapping","uncertain.ocm","ClassMapping");
        mClassRegistry.registerClass("feature-attach","uncertain.ocm","FeatureAttach");
        //mClassRegistry.registerClass("extra-class-registry", "uncertain.init", "ExtraClassRegistry");
        
        loadInternalRegistry(LoggingConfig.LOGGING_REGISTRY_PATH);
    }
    
    private void loadBuiltinLoggingTopic(){
        mTopicManager.registerLoggingTopic(UNCERTAIN_LOGGING_TOPIC);
        mTopicManager.registerLoggingTopic(OCManager.LOGGING_TOPIC);
        mTopicManager.registerLoggingTopic(Configuration.LOGGING_TOPIC);
        mTopicManager.registerLoggingTopic(ProcedureRunner.LOGGING_TOPIC);
    }
    
    private void loadInternalRegistry( String file_path ){
        CompositeMap map = loadCompositeMap(file_path);
        if(map==null) throw new RuntimeException("Can't load internal resource "+file_path);
        ClassRegistry reg = (ClassRegistry)mOcManager.createObject(map);
        this.mClassRegistry.addAll(reg);
    }
    
    protected void bootstrap(){
        // create default cache factory
        mNamedCacheFactory = new MapBasedCacheFactory();
        // create internal CompositeMapLoader 
        mCompositeLoader = CompositeLoader.createInstanceForOCM();
        mDirectoryConfig = (DirectoryConfig)DynamicObject.cast(this.mGlobalContext, DirectoryConfig.class);

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
        registerBuiltinInstances();
        loadBuiltinLoggingTopic();
        // load internal registry
    } 

    public void initialize(CompositeMap config){
        // populate self from config
        if( config!=null )
            mOcManager.populateObject(config,this);
        checkLogger();
        mLogger.log("Uncertain engine startup");
        // perform configuration
        doConfigure(mExtraConfig);
        mIsRunning = true;
        
    }
    
    /* ================== logging process ======================================= */
    
    protected ILoggerProvider createDefaultLoggerProvider(){        
        LoggerProvider clp = new LoggerProvider();
        clp.setDefaultLogLevel(mDefaultLogLevel);
        clp.addTopics( new LoggingTopic[]{
                new LoggingTopic(UNCERTAIN_LOGGING_TOPIC, Level.INFO),
                new LoggingTopic(OCManager.LOGGING_TOPIC, Level.WARNING)
        });
        if(mLogPath==null)
            clp.addHandle(new BasicConsoleHandler());
        else{
            BasicFileHandler fh = new BasicFileHandler();
            fh.setLogPath(mLogPath);
            fh.setLogFilePrefix(getName());
            clp.addHandle( fh );
        }
        return clp;
    }
    
    protected void checkLogger(){
        ILoggerProvider logger_provider = (ILoggerProvider)mObjectRegistry.getInstanceOfType(ILoggerProvider.class);
        if(logger_provider==null){
            logger_provider = createDefaultLoggerProvider();
            mObjectRegistry.registerInstance(ILoggerProvider.class, logger_provider);
            mOcManager.setLoggerProvider(logger_provider);            
        }
        mLogger = logger_provider.getLogger(UNCERTAIN_LOGGING_TOPIC);
        /*
        if(mErrorLogger==null)
            mErrorLogger = logger_provider.getLogger(UNCERTAIN_ERROR_TOPIC);
        if(mErrorLogger==DummyLogger.getInstance()){
            DefaultLogger l =new DefaultLogger("error");
            l.addHandler( new BasicConsoleHandler());
            mErrorLogger = l;
        }
        */
        //mLogger.info("Logging provider set to "+logger_provider);
    }
    
    public void addLoggingConfig( ILoggerProvider logging_config ){
        if(logging_config instanceof ILoggerProviderGroup ){
            ILoggerProviderGroup group = (ILoggerProviderGroup)logging_config;
            group.registerTo(mObjectRegistry);
        }
        
        if( logging_config instanceof IContextListener)
            mContextListenerSet.add(logging_config);
        if( logging_config instanceof ILogPathSettable ){
            ILogPathSettable lp = (ILogPathSettable)logging_config;
            String log_path = mDirectoryConfig.getLogDirectory();
            if(log_path!=null && lp.getLogPath()==null ){
                lp.setLogPath(log_path);
            }            
        }
    }
    
    public ILogger getLogger( String topic ){
        ILoggerProvider provider = (ILoggerProvider)mObjectRegistry.getInstanceOfType(ILoggerProvider.class);
        if(provider==null)
            return DummyLogger.getInstance();
        else
            return provider.getLogger(topic);
    }
    
    
    
    /* ================== Configuration components ============================ */
    
    public void addChild(CompositeMap child){
        mExtraConfig.add(child);
    }

    public void logException(String message, Throwable thr){
        mLogger.log(Level.SEVERE, message, thr);
    }

    
    /**
     * 
     * UncertainEngine
     * @author Zhou Fan
     *
     */
    public void doConfigure(Collection cfg){
        //if(config!=null) 
        mConfig = createConfig();
        mConfig.setLogger(mLogger);
        //logger.info("Attached:"+this.ocManager.getClassRegistry().getFeatures(new ElementIdentifier(null,"class-registry")));
        mConfig.loadConfigList(cfg);
        if(mConfig.getParticipantList().size()>0)
            mLogger.log("Adding configuration participant "+mConfig.getParticipantList());
        // regsiter global instances
        Iterator it = mConfig.getParticipantList().iterator();
        while(it.hasNext()){
            Object inst = it.next();
            if(inst instanceof IGlobalInstance) 
                getObjectRegistry().registerInstance(inst);
            if(inst instanceof IContextListener)
                addContextListener((IContextListener)inst);
        }
        // run  procedure
        Procedure proc = loadProcedure("uncertain.core.EngineInit");
        if(proc==null) throw new IllegalArgumentException("Can't load uncertain/core/EngineInit.xml from class loader");
        ProcedureRunner runner = createProcedureRunner(proc);
        runner.addConfiguration(mConfig);
        runner.run();  
        Throwable thr = runner.getException();
        if(thr!=null){
            mLogger.log(Level.SEVERE, "An error happened during initialize process");
            logException("Error when running procedure", thr);
        }
        // update logger instance if new LoggerProvider generated
        checkLogger();        
    }
    
    public void scanConfigFiles(){
        if(mConfigDir!=null){
            mLogger.log("Scanning config directory "+mConfigDir);
            scanConfigFiles(mConfigDir, DEFAULT_CONFIG_FILE_PATTERN);
        }
        else{
            if(mDirectoryConfig!=null)
                if(mDirectoryConfig.getBaseDirectory()!=null){
                    mLogger.log("Scanning config directory "+mCompositeLoader.getBaseDir());
                    scanConfigFiles(new File(mDirectoryConfig.getBaseDirectory()), DEFAULT_CONFIG_FILE_PATTERN);
                }
        }
    }
    
    public void scanConfigFiles(String pattern){
        scanConfigFiles(mConfigDir, pattern);
    }
    
    private List getSortedList( File[] files ){
        List lst = new LinkedList();
        for(int i=0; i<files.length; i++)
            lst.add(files[i]);
        Collections.sort(lst, new Comparator(){
            
            public int compare(Object o1, Object o2){
                return ((File)o1).getAbsolutePath().compareTo(((File)o2).getAbsolutePath());
            }
            
            public boolean equals(Object obj){
                return obj==this;
            }
            
        });
        return lst;
    }
    
    /**
     * Scan a directory for files that matches certain pattern, and load these file
     * to perform configuration task
     * @param dir Directory that contains config file
     * @param file_pattern regular expression string to specify file name pattern 
     */
    public void scanConfigFiles(File dir, String file_pattern) {
        FilePatternFilter filter = new FilePatternFilter(file_pattern);
        File cfg_files[] = dir.listFiles(filter);
        List file_list = getSortedList(cfg_files);
        if(cfg_files.length>0){
            LinkedList cfg_list = new LinkedList();
            ListIterator fit = file_list.listIterator(cfg_files.length);
            while(fit.hasPrevious()){
                String file_path = ((File)fit.previous()).getAbsolutePath();
                mLogger.log("Loading configuration file "+file_path);
                try{
                    CompositeMap config_map = mCompositeLoader.loadByFullFilePath(file_path);
                    cfg_list.add(config_map);
                }catch(Throwable thr){
                    mLogger.log(Level.SEVERE, "Can't load initialize config file "+file_path);
                    logException("Error when loading configuration file "+file_path, thr);
                }
            }
            if(cfg_list.size()>0)
                doConfigure(cfg_list);
        }
    }
    
    /* ================== factory methods ======================================= */
  
    public Configuration createConfig(){
        Configuration conf = new Configuration(mParticipantRegistry, mOcManager);
        return conf;
    }
    
    public Configuration createConfig(CompositeMap cfg){
        Configuration conf = new Configuration(mParticipantRegistry, mOcManager);
        conf.loadConfig(cfg);
        return conf;
    }
    
    private CompositeMap loadCompositeMap(String class_path)
    {
        try{

	        CompositeMap m = mCompositeLoader.loadFromClassPath(class_path);
	        return m;
        }catch(Exception ex){
            throw new RuntimeException("Can't load CompositeMap from path "+class_path, ex);
        }
    }
        
    
    public Configuration loadConfig(String class_path){
        CompositeMap m = loadCompositeMap(class_path);
        if(m==null) return null;
        Configuration config = createConfig();
        config.loadConfig(m);
        return config;
    }    
    
    private Procedure loadProcedure(String class_path)
    {
        /*
        CompositeMap m = loadCompositeMap(class_path);
        if(m==null) return null;
        Procedure proc = (Procedure)mOcManager.createObject(m);
        return proc;
        */
        try{
            return mProcedureManager.loadProcedure(class_path);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ProcedureRunner createProcedureRunner(){
        ProcedureRunner runner = new ProcedureRunner();
        //initContext(runner.getContext());
        runner.setUncertainEngine(this);
        return runner;        
    }

    public ProcedureRunner createProcedureRunner(Procedure proc){
        if(proc==null) return null;
        ProcedureRunner runner = createProcedureRunner();
        runner.setProcedure(proc);
        return runner;       
    }
    
    public ProcedureRunner createProcedureRunner(String proc_path){
        Procedure proc = loadProcedure(proc_path);
        if(proc==null) return null;
        ProcedureRunner runner = createProcedureRunner(proc);
        return runner;
    }
    

    
    public void initContext( CompositeMap context ){
        //mLogger.log(mContextListenerSet.size()+" listeners");
        for(Iterator it = mContextListenerSet.iterator(); it.hasNext(); ){
            IContextListener listener = (IContextListener)it.next();
            //mLogger.log("invoking init on "+listener);
            listener.onContextCreate(RuntimeContext.getInstance(context));
        }
    }
    
    public void destroyContext( CompositeMap context ){
        for(Iterator it = mContextListenerSet.iterator(); it.hasNext(); ){
            IContextListener listener = (IContextListener)it.next();
            listener.onContextDestroy(RuntimeContext.getInstance(context));
        }        
    }

    /* ================== get/set methods ======================================= */

    public void setConfigDirectory(File dir){
        mConfigDir = dir;
        if(mDirectoryConfig==null)
            mDirectoryConfig = (DirectoryConfig)DynamicObject.cast(this.mGlobalContext, DirectoryConfig.class);
        mDirectoryConfig.setConfigDirectory(dir.getPath());
    }
    
    public void addClassRegistry(ClassRegistry reg){
        mClassRegistry.addAll(reg);
    }
    
    public void addClassRegistry(ClassRegistry reg, boolean override){
        mClassRegistry.addAll(reg, override);
    }

    /*
    public void addDocumentLoader(CompositeLoader loader){
        if(mCompositeLoader==null) mCompositeLoader = loader;
        else mCompositeLoader.addExtraLoader(loader);
    }
    */

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
    
    public IObjectCreator getObjectCreator(){
        return mObjectRegistry;
    }

    public IObjectRegistry getObjectRegistry(){
        return mObjectRegistry;
    }    

    /**
     * @param ocManager The ocManager to set.
     */
    public void setOcManager(OCManager ocManager) {
        this.mOcManager = ocManager;
    }
    /**
     * @return Returns the participantRegistry.
     */
    public ParticipantRegistry getParticipantRegistry() {
        return mParticipantRegistry;
    }
    /**
     * @param participantRegistry The participantRegistry to set.
     */
    public void setParticipantRegistry(ParticipantRegistry participantRegistry) {
        this.mParticipantRegistry = participantRegistry;
    }

/*    
    public void setObjectSpace(ObjectSpace objectSpace) {
        this.mObjectSpace = objectSpace;
    }    
*/
    public CompositeMap getGlobalContext(){
        return mGlobalContext;
    }
    
    public ILoggingTopicRegistry getLoggingTopicRegistry(){
        return mTopicManager;
    }

    /**
     * @return Returns the logger.
     */
    /*
    public Logger getLogger() {
        //return mLogger;
        if(mLogger instanceof DefaultLogger)
            return ((DefaultLogger)mLogger);
        else
            return Logger.getAnonymousLogger();
    
    }
    */
    /**
     * @return Returns the config_dir.
     */
    public File getConfigDirectory() {
        return mConfigDir;
    }
    
    public boolean getIsRunning(){
        return mIsRunning;
    }
    
    public void addContextListener( IContextListener listener ){
        mContextListenerSet.add(listener);
    }
    
    public void shutdown(){
        mIsRunning = false;
        mLogger.log("Uncertain engine shutdown");        
        Procedure proc = loadProcedure("uncertain.core.EngineShutdown");
        if(proc==null) throw new IllegalArgumentException("Can't load uncertain/core/EngineShutdown.xml from class loader");
        ProcedureRunner runner = createProcedureRunner(proc);
        runner.addConfiguration(mConfig);
        runner.run(); 
    }
    
    public DirectoryConfig getDirectoryConfig(){
        return mDirectoryConfig;
    }
    
    public IProcedureManager getProcedureManager(){
        return mProcedureManager;
    }
    
    public String getName(){
        return mName;
    }
    
    public String getMBeanName( String category, String sub_name ){
        String name = "org.uncertain:instance="+getName();
        if(category!=null)
            name = name + ",category="+category;
        if(sub_name!=null)
            name = name +","+sub_name;
        return name;
    }
    
    public void setName( String name ){
        mName = name;
    }

    public String getLogPath() {
        return mLogPath;
    }

    public void setLogPath(String logPath) {
        File dir = new File(logPath);
        if(!dir.exists() || !dir.isDirectory())
            throw new ConfigurationError(logPath + " is not a valid logging directory");
        mLogPath = logPath;        
    }

    public String getDefaultLogLevel() {
        return mDefaultLogLevel;
    }

    public void setDefaultLogLevel(String defaultLogLevel) {
        Level.parse(defaultLogLevel);
        mDefaultLogLevel = defaultLogLevel;
    }
    
    
    public INamedCacheFactory getNamedCacheFactory() {
        return mNamedCacheFactory;
    }

    public void setNamedCacheFactory(INamedCacheFactory mCacheFactory) {
        this.mNamedCacheFactory = mCacheFactory;
    }
    
    
    public boolean getCacheConfigFiles() {
        return mCacheConfigFiles;
    }

    public void setCacheConfigFiles(boolean mCacheConfigFiles) {
        this.mCacheConfigFiles = mCacheConfigFiles;
        if(mProcedureManager!=null)
            mProcedureManager.setIsCache(mCacheConfigFiles);
    }
    
    public ICache createNamedCache( String name ){
        if(getCacheConfigFiles()){
            String mbean_name = getMBeanName("cache", "name="+name);
            if(mNamedCacheFactory!=null)
                return mNamedCacheFactory.getNamedCache(mbean_name);
            else
                return null;
        }else
            return null;
    }
    
    public void prepareCacheSettings( CompositeLoader loader, String name ){
        name = getMBeanName("cache","name="+name);
        ICache cache = mNamedCacheFactory.getNamedCache(name);
        loader.setCache(cache);
        loader.setCacheEnabled(true);
    }
    
}
