/*
 * Created on 2005-7-24
 */
package uncertain.core;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import uncertain.composite.CharCaseProcessor;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import uncertain.event.Configuration;
import uncertain.init.LoggingConfig;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IChildContainerAcceptable;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectSpace;
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
    
    public static final String UNCERTAIN_NAMESPACE = "http://engine.uncertain.org/defaultns";
    public static final String UNCERTAIN_LOGGING_SPACE = "uncertain.core";
    public static final String DEFAULT_CONFIG_FILE_PATTERN = ".*\\.config";
    
    CompositeMapParser		compositeParser;
    CompositeLoader			compositeLoader;
    OCManager				ocManager;
    ObjectSpace				objectSpace;
    ClassRegistry			classRegistry;
    ParticipantRegistry		participantRegistry;
    Configuration           config;
    Logger					logger;
    CompositeMap            globalContext;
    
    
    LinkedList				extra_config = new LinkedList();
    File					config_dir;    
    boolean                 is_running = true;
    
    /* ================== Constructors ======================================= */
    
    public UncertainEngine(InputStream config_stream) throws IOException {
        compositeParser = OCManager.defaultParser();
        try{
	        CompositeMap config = compositeParser.parseStream(config_stream);
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
            CompositeMap config_map = OCManager.defaultParser().parseStream(fis);
            initialize(config_map);
        }catch(SAXException ex){
            logger.log(Level.SEVERE,"Configuration file syntax error", ex);
        }        
        finally{
            if(fis!=null) fis.close();
        }        
    }
        
    
    /* ================== Initialize methods ======================================= */    
    
    void registerGlobalParameters(){
        //objectSpace.registerParameter(compositeParser);
        objectSpace.registerParameter(compositeLoader);
        objectSpace.registerParameter(ocManager);
        objectSpace.registerParameter(classRegistry);
        objectSpace.registerParameter(participantRegistry);
        //objectSpace.registerParameter(compositeParser);
        objectSpace.registerParamOnce(UncertainEngine.class,this);
        objectSpace.registerParameter(Logger.class, logger);
        //System.out.println(objectSpace.getParameters());
    }
    
    void setDefaultClassRegistry(){
        
        classRegistry.registerPackage("uncertain.proc");
        classRegistry.registerPackage("uncertain.ocm");

        classRegistry.registerClass("document-loader","uncertain.composite","CompositeLoader");
        classRegistry.registerClass("document-path","uncertain.composite","CompositeLoader");
        classRegistry.registerClass("class-registry","uncertain.ocm","ClassRegistry");
        classRegistry.registerClass("package-mapping","uncertain.ocm","PackageMapping");
        classRegistry.registerClass("class-mapping","uncertain.ocm","ClassMapping");
        classRegistry.registerClass("feature-attach","uncertain.ocm","FeatureAttach");
        classRegistry.registerClass("logging-config", "uncertain.init", "LoggingConfig");
        classRegistry.registerClass("extra-class-registry", "uncertain.init", "ExtraClassRegistry");
        //classRegistry.attachFeature(null,"class-registry",ExtraClassRegistry.class);
    }
    
    protected void bootstrap(){
        // create bootstrap object instance
        objectSpace = new ObjectSpace();
        ocManager = new OCManager(objectSpace);  
        // ocManager.addListener(new LoggingListener());
        classRegistry = ocManager.getClassRegistry();
        setDefaultClassRegistry();
        participantRegistry = new ParticipantRegistry();    
        globalContext = new CompositeMap("global");
    }    
    
    void validateConfig(){
        if(compositeLoader == null){
            compositeLoader = new CompositeLoader(".");
            //compositeLoader.setCacheEnabled(true);
            //System.out.println("Cache enabled");
        }
        //compositeLoader.setSupportXInclude(true);
        if(logger == null){
            logger = Logger.getLogger(UNCERTAIN_LOGGING_SPACE);
            //LoggingUtil.setHandleLevels(logger.getParent(), Level.WARNING);
        }
    }
    
    public void initialize(CompositeMap config){
        // populate self from config
        ocManager.populateObject(config,this);
        validateConfig();
        logger.info("Uncertain engine startup");
        // inits CompositeLoader
        compositeParser = CompositeMapParser.createInstance(
                compositeLoader,
                new CharCaseProcessor(CharCaseProcessor.CASE_LOWER, CharCaseProcessor.CASE_UNCHANGED)
        );
        compositeLoader.setCompositeParser(compositeParser);
        // register global parameters into ObjectSpace
        registerGlobalParameters(); 
        
        // perform configuration
        doConfigure(extra_config);
        
        is_running = true;
    }
    
    /* ================== Configuration components ============================ */
    
    public void addChild(CompositeMap child){
        extra_config.add(child);
    }

    void handleException(Throwable thr){
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(bos);
            thr.printStackTrace(pw);
            pw.close();
            logger.severe(new String(bos.toByteArray()));
            bos.close();
        }catch(IOException ex){
            
        }
    }    
    /**
     * 
     * UncertainEngine
     * @author Zhou Fan
     *
     */
    public void doConfigure(Collection cfg){
        //if(config!=null) 
        config = createConfig();
        //logger.info("Attached:"+this.ocManager.getClassRegistry().getFeatures(new ElementIdentifier(null,"class-registry")));
        config.loadConfigList(cfg);
        if(config.getParticipantList().size()>0)
            logger.info("Adding configuration participant "+config.getParticipantList());
        // regsiter global instances
        Iterator it = config.getParticipantList().iterator();
        while(it.hasNext()){
            Object inst = it.next();
            if(inst instanceof IGlobalInstance) 
                getObjectSpace().registerParameter(inst);
        }
        // run init procedure
        Procedure proc = loadProcedure("uncertain.core.EngineInit");
        if(proc==null) throw new IllegalArgumentException("Can't load uncertain/core/EngineInit.xml from class loader");
        ProcedureRunner runner = createProcedureRunner(proc);
        runner.addConfiguration(config);
        runner.run();  
        Throwable thr = runner.getException();
        if(thr!=null){
            logger.severe("An error happened during initialize process");
            handleException(thr);
        }
    }
    
    public void scanConfigFiles(){
        if(config_dir!=null)
            scanConfigFiles(config_dir, DEFAULT_CONFIG_FILE_PATTERN);
        else
            scanConfigFiles(new File(compositeLoader.getBaseDir()), DEFAULT_CONFIG_FILE_PATTERN);
    }
    
    public void scanConfigFiles(String pattern){
        scanConfigFiles(config_dir, pattern);
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
                logger.info("Loading configuration file "+file_path);
                try{
                    CompositeMap config_map = compositeLoader.loadByFullFilePath(file_path);
                    cfg_list.add(config_map);
                }catch(Throwable thr){
                    logger.log(Level.SEVERE, "Can't load initialize config file "+file_path, thr);
                }
            }
            if(cfg_list.size()>0)
                doConfigure(cfg_list);
        }
    }
    
    /* ================== factory methods ======================================= */
  
    public Configuration createConfig(){
        Configuration conf = new Configuration(participantRegistry, ocManager);
        return conf;
    }
    
    public Configuration createConfig(CompositeMap cfg){
        Configuration conf = new Configuration(participantRegistry, ocManager);
        conf.loadConfig(cfg);
        return conf;
    }
    
    public CompositeMap loadCompositeMap(String class_path)
    {
        try{

	        CompositeMap m = compositeLoader.loadFromClassPath(class_path, false);
	        return m;
        }catch(Exception ex){
            throw new RuntimeException("Can't load CompositeMap from path "+class_path, ex);
        }
/*            
            try{
            } catch(Throwable ex){
            logger.log(Level.SEVERE,"Can't load CompositeMap from "+class_path, ex);
            ex.printStackTrace();
            return null;
        }
*/        
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
        Procedure proc = (Procedure)ocManager.createObject(m);
        return proc;
    }

    public ProcedureRunner createProcedureRunner(){
        ProcedureRunner runner = new ProcedureRunner();
        runner.setUncertainEngine(this);
        return runner;        
    }

    public ProcedureRunner createProcedureRunner(Procedure proc){
        if(proc==null) return null;
        ProcedureRunner runner = createProcedureRunner();
        runner.setProcedure(proc);
        return runner;
        /*
        ProcedureRunner runner = new ProcedureRunner(proc);
        HandleManager handleManager = new HandleManager(participantRegistry);
        runner.setHandleManager(handleManager);
        return runner;
        */
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

    /* ================== get/set methods ======================================= */

    /**
     * Set a java.util.logging.Logger instance for logging
     */
    public void setLogger(Logger l){
        logger = l;
    }
    
    public void setConfigDirectory(File dir){
        config_dir = dir;
    }
    
    public void addClassRegistry(ClassRegistry reg){
        classRegistry.addAll(reg);
    }
    
    public void addClassRegistry(ClassRegistry reg, boolean override){
        classRegistry.addAll(reg, override);
    }
    
    public void addDocumentLoader(CompositeLoader loader){
        if(compositeLoader==null) compositeLoader = loader;
        else compositeLoader.addExtraLoader(loader);
    }
    
    public void addLoggingConfig(LoggingConfig lc){
        setLogger(lc.getLogger());
    }
    
    /**
     * @return Returns the classRegistry.
     */
    public ClassRegistry getClassRegistry() {
        return classRegistry;
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
        return compositeLoader;
    }

    /**
     * @return Returns the compositeParser.
     */
    public CompositeMapParser getCompositeParser() {
        return compositeParser;
    }
    /**
     * @param compositeParser The compositeParser to set.
     */
    public void setCompositeParser(CompositeMapParser compositeParser) {
        this.compositeParser = compositeParser;
    }
    /**
     * @return Returns the ocManager.
     */
    public OCManager getOcManager() {
        return ocManager;
    }
    
    public IObjectCreator getObjectCreator(){
        return objectSpace;
    }

    public ObjectSpace getObjectSpace(){
        return objectSpace;
    }    

    /**
     * @param ocManager The ocManager to set.
     */
    public void setOcManager(OCManager ocManager) {
        this.ocManager = ocManager;
    }
    /**
     * @return Returns the participantRegistry.
     */
    public ParticipantRegistry getParticipantRegistry() {
        return participantRegistry;
    }
    /**
     * @param participantRegistry The participantRegistry to set.
     */
    public void setParticipantRegistry(ParticipantRegistry participantRegistry) {
        this.participantRegistry = participantRegistry;
    }
    /**
     * @param objectSpace The objectSpace to set.
     */
    public void setObjectSpace(ObjectSpace objectSpace) {
        this.objectSpace = objectSpace;
    }    

    public CompositeMap getGlobalContext(){
        return globalContext;
    }

    /**
     * @return Returns the logger.
     */
    public Logger getLogger() {
        return logger;
    }
    /**
     * @return Returns the config_dir.
     */
    public File getConfigDirectory() {
        return config_dir;
    }
    
    public boolean isRunning(){
        return is_running;
    }
    
    public void shutdown(){
        is_running = false;
        logger.info("Uncertain engine shutdown");        
        Procedure proc = loadProcedure("uncertain.core.EngineShutdown");
        if(proc==null) throw new IllegalArgumentException("Can't load uncertain/core/EngineShutdown.xml from class loader");
        ProcedureRunner runner = createProcedureRunner(proc);
        runner.addConfiguration(config);
        runner.run(); 
    }
}
