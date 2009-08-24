/*
 * Created on 2005-7-24
 */
package uncertain.core;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import uncertain.composite.CharCaseProcessor;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import uncertain.composite.DynamicObject;
import uncertain.event.Configuration;
import uncertain.event.IContextListener;
import uncertain.event.RuntimeContext;
import uncertain.logging.BasicConsoleHandler;
import uncertain.logging.ConfigurableLoggerProvider;
import uncertain.logging.DefaultLogger;
import uncertain.logging.DummyLogger;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.logging.ILoggingTopicRegistry;
import uncertain.logging.LoggingConfig;
import uncertain.logging.LoggingTopic;
import uncertain.logging.TopicManager;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IChildContainerAcceptable;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.proc.ParticipantRegistry;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;
import uncertain.util.FilePatternFilter;

/**
 * The facade class of uncertain object collaboration framework
 * @author Zhou Fan
 * 
 */
public class UncertainEngine implements IChildContainerAcceptable {
    
    public static final String KEY_NAME = "NAME";
    public static final String UNCERTAIN_NAMESPACE = "http://engine.uncertain.org/defaultns";
    public static final String DEFAULT_CONFIG_FILE_PATTERN = ".*\\.config";
    
    public static final String UNCERTAIN_LOGGING_TOPIC = "uncertain.core";
    public static final String UNCERTAIN_ERROR_TOPIC = "error";
    
    //CompositeMapParser		mCompositeParser;
    CompositeLoader			mCompositeLoader = CompositeLoader.createInstanceForOCM();
    OCManager				mOcManager;
    ObjectRegistryImpl		mObjectRegistry;
    ClassRegistry			mClassRegistry;
    ParticipantRegistry		mParticipantRegistry;
    Configuration           mConfig;
    CompositeMap            mGlobalContext;
    DirectoryConfig         mDirectoryConfig;
    
    Set                     mContextListenerSet;
    LinkedList				mExtraConfig = new LinkedList();
    File					mConfigDir;    
    boolean                 mIsRunning = true;

    // Logging
    ILogger                 mLogger;    
    ILogger                 mErrorLogger;
    TopicManager            mTopicManager;
    
    public static UncertainEngine createInstance(){
        UncertainEngine     engine = new UncertainEngine();
        engine.initialize(null);
        return engine;
    }
    
    /* ================== Constructors ======================================= */
    
    public UncertainEngine(InputStream config_stream) throws IOException {
        //mCompositeParser = OCManager.defaultParser();
        try{
	        CompositeMap config = mCompositeLoader.loadFromStream(config_stream);
	        initialize(config);
        } catch(SAXException ex){
            throw new IOException(ex.getMessage());
        }
    }
    
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
        mObjectRegistry.registerInstanceOnce(ILoggingTopicRegistry.class, mTopicManager);
    }
    
    private void setDefaultClassRegistry(){
        
        mClassRegistry.registerPackage("uncertain.proc");
        mClassRegistry.registerPackage("uncertain.ocm");
        mClassRegistry.registerPackage("uncertain.logging");
        mClassRegistry.registerPackage("uncertain.core");
        mClassRegistry.registerPackage("uncertain.core.admin");
        
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
        // create internal CompositeMapLoader 
        mCompositeLoader = CompositeLoader.createInstanceForOCM();
        mDirectoryConfig = (DirectoryConfig)DynamicObject.cast(this.mGlobalContext, DirectoryConfig.class);

        // create bootstrap object instance
        mContextListenerSet = new HashSet();
        mObjectRegistry = new ObjectRegistryImpl();
        mOcManager = new OCManager(mObjectRegistry);  

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
        ConfigurableLoggerProvider clp = new ConfigurableLoggerProvider();
        clp.addTopics( new LoggingTopic[]{
                new LoggingTopic(UNCERTAIN_LOGGING_TOPIC, Level.INFO),
                new LoggingTopic(OCManager.LOGGING_TOPIC, Level.WARNING)
        });
        clp.addHandles( new Handler[]{
                new BasicConsoleHandler()
        });
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
        if(mErrorLogger==null)
            mErrorLogger = logger_provider.getLogger(UNCERTAIN_ERROR_TOPIC);
        if(mErrorLogger==DummyLogger.getInstance()){
            DefaultLogger l =new DefaultLogger("error");
            l.addHandler( new BasicConsoleHandler());
            mErrorLogger = l;
        }
        //mLogger.info("Logging provider set to "+logger_provider);
    }
    
    public void addLoggingConfig( LoggingConfig logging_config ){
        //System.out.println("Adding "+logging_config);
        logging_config.registerTo(mObjectRegistry);
        mContextListenerSet.add(logging_config);
        String log_path = mDirectoryConfig.getLogDirectory();
        if(log_path!=null && logging_config.getLogPath()==null ){
            System.out.println("log path set to "+log_path);
            logging_config.setLogPath(log_path);
        }
        //System.out.println("Logger set to "+getLogger(UNCERTAIN_LOGGING_TOPIC));        
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
        mErrorLogger.log(Level.SEVERE, message, thr);
        //mLogger.log(Level.SEVERE, thr.getMessage(), thr);
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
                getObjectSpace().registerInstance(inst);
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
    
    /**
     * Scan a directory for files that matches certain pattern, and load these file
     * to perform configuration task
     * @param dir Directory that contains config file
     * @param file_pattern regular expression string to specify file name pattern 
     */
    public void scanConfigFiles(File dir, String file_pattern) {
        FilePatternFilter filter = new FilePatternFilter(file_pattern);
        File cfg_files[] = dir.listFiles(filter);
        if(cfg_files.length>0){
            LinkedList cfg_list = new LinkedList();
            for(int i=cfg_files.length-1; i>=0; i--){
                String file_path = cfg_files[i].getPath();
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
    
    public CompositeMap loadCompositeMap(String class_path)
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
    
    public Procedure loadProcedure(String class_path){
        CompositeMap m = loadCompositeMap(class_path);
        if(m==null) return null;
        Procedure proc = (Procedure)mOcManager.createObject(m);
        return proc;
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
    
    public ProcedureRunner createProcedureRunner(String proc_path, String config_path){
        ProcedureRunner runner = createProcedureRunner(proc_path);
        Configuration config = loadConfig(config_path);
        if(config!=null) runner.addConfiguration(config);
        else throw new IllegalArgumentException("Can't load " + config_path);
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
    
    public void addDocumentLoader(CompositeLoader loader){
        if(mCompositeLoader==null) mCompositeLoader = loader;
        else mCompositeLoader.addExtraLoader(loader);
    }
    
    /**
     * @return Returns the classRegistry.
     */
    public ClassRegistry getClassRegistry() {
        return mClassRegistry;
    }
    /**
     * @param classRegistry The classRegistry to set.
     */
    /*
    public void setClassRegistry(ClassRegistry classRegistry) {
        this.classRegistry = classRegistry;
    }
    */
    /**
     * @return Returns the compositeLoader.
     */
    public CompositeLoader getCompositeLoader() {
        return mCompositeLoader;
    }

    /**
     * @return Returns the compositeParser.
     */
    /*
    public CompositeMapParser getCompositeParser() {
        return mCompositeParser;
    }
    */
    /**
     * @param compositeParser The compositeParser to set.
     */
    /*
    public void setCompositeParser(CompositeMapParser compositeParser) {
        this.mCompositeParser = compositeParser;
    }
    */
    /**
     * @return Returns the ocManager.
     */
    public OCManager getOcManager() {
        return mOcManager;
    }
    
    public IObjectCreator getObjectCreator(){
        return mObjectRegistry;
    }

    public IObjectRegistry getObjectSpace(){
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
    public Logger getLogger() {
        //return mLogger;
        if(mLogger instanceof DefaultLogger)
            return ((DefaultLogger)mLogger);
        else
            return Logger.getAnonymousLogger();
    
    }
    /**
     * @return Returns the config_dir.
     */
    public File getConfigDirectory() {
        return mConfigDir;
    }
    
    public boolean isRunning(){
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
    
    public String getName(){
        return mGlobalContext.getString(KEY_NAME);
    }
    
    public void setName( String name ){
        mGlobalContext.putString(KEY_NAME, name);
    }
}
