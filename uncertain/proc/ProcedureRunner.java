/*
 * Created on 2005-4-18
 */
package uncertain.proc;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.event.RuntimeContext;
import uncertain.logging.DummyLogger;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.ocm.OCManager;
import uncertain.proc.trace.StackTraceManager;

/**
 * ProcedureRunner
 * @author Zhou Fan
 * 
 */
public class ProcedureRunner {
    
    public static final String LOGGING_TOPIC = "uncertain.proc";    
    
    CompositeMap		mContext;
    Procedure			mProcedure;
    ProcedureRunner		mCaller;
    RuntimeContext      mRuntimeContext;
    UncertainEngine		mUncertainEngine;
    //Configuration       config;

    /** flag to designate whether the process is running */
    boolean				mIsContinue = true;

    /** flag to designate whether running a list of IEntry */
    boolean				mRunningList = false;    
    
    /** flag to designate whether in handle exception state */
    boolean             mInExceptionHandle = false;
    
    /** flag to designate continue running after an exception caught */
    boolean             mResumeAfterException = false;
    
    boolean             mSaveStackTrace = true;
    
    /** current iterator on EntryList */
    ListIterator		current_iterator;
    
    /** current EntryList object to run */ 
    EntryList			current_entry_list;
    
    //Collection          exception_handle_list;

    int					current_event_sequence = 0; 
    String				current_event = null;
    
    Throwable           lastException;
    
    // 2012-12-18
    // Add exception handle stack to implement nested exception handle
    LinkedList          mExceptionHandleStack = null;
    
    /** exception thrown during running process */
    Throwable			procException;
    
    ILogger             mLogger;
    
    /** Stack trace manager */
    StackTraceManager   mStackTraceManager;
    
    public ProcedureRunner(){
        setContext(new CompositeMap("runtime-context"));
    }
    
    public ProcedureRunner(Procedure proc){
        this();
        setProcedure(proc);
    }

    /**
     * @return Returns the context.
     */
    public CompositeMap getContext() {
        return mContext;
    }
    /**
     * @param context The context to set.
     */
    public void setContext(CompositeMap context) {
        this.mContext = context;
        this.mRuntimeContext = RuntimeContext.getInstance(context);
        this.mStackTraceManager = mRuntimeContext.getStackTraceManager();
        createDefaultConfig();
    }
    /**
     * @return Returns the procedure.
     */
    public Procedure getProcedure() {
        return mProcedure;
    }
    /**
     * @param procedure The procedure to set.
     */
    public void setProcedure(Procedure procedure) {
        this.mProcedure = procedure;
        reset();
    }    
    
    /**
     * @return Returns the caller.
     */
    public ProcedureRunner getCaller() {
        return mCaller;
    }
    
    public void reset(){
        if(mProcedure==null) 
            throw new IllegalArgumentException("Procedure not set");
        current_entry_list = mProcedure;
        current_iterator = null;
        mIsContinue = true;
    }
    
    public void stop(){
        mIsContinue = false;
    }
    
    public boolean isRunning(){
        return mIsContinue;
    }
    
    
    public void setResumeAfterException( boolean flag ){
        mResumeAfterException = flag;
    }
    
    /**
     * Locate to a specific named entry, this entry will be the next one to execute in procedure
     * @param entry_name the name of entry to locate to
     * @return true if specified entry is found and successfully located to
     */
    public boolean locateTo(String entry_name){ 
        IEntry entry = null;
        if(current_entry_list!=null)
            entry = current_entry_list.getNamedEntry(entry_name);
        if(entry==null)
            entry = mProcedure.getNamedEntry(entry_name);
        if(entry==null) throw new IllegalStateException("Can't find entry '"+entry_name+"' in current procedure");
        IEntry owner = entry.getOwner();
        if(owner==null || !(owner instanceof EntryList)) 
            throw new IllegalStateException("Node entry '"+entry_name+"' has no parent");
        current_entry_list = (EntryList)owner;
        current_iterator = current_entry_list.locateEntry(entry);
        if(current_iterator==null)throw new IllegalStateException("Internal structure error, named entry map is corrupt");
        return true;
    }
    
    public void throwException(Throwable thr){
        procException = thr.getCause();        
        if(procException==null) procException = thr;
        lastException = procException;
        mResumeAfterException = false;
        mRuntimeContext.setException(procException);
        mRuntimeContext.setSuccess(false);
        if(handleException(procException)){
            mRuntimeContext.setException(null);
            mRuntimeContext.setLastHandledException(procException);
            procException = null;
        }
        if(!mResumeAfterException)
            stop();
    }
    
    public void throwUnhandledException(Throwable thr){
        mRuntimeContext.setSuccess(false);
        procException = thr.getCause();
        if(procException==null) procException = thr;
        lastException = procException;
        stop();
    }
    
    public Throwable getException(){
        return procException;
    }
    
    /**
     * Check if there is exception during execution
     * if there is, throw as Exception
     * @throws Exception
     */
    public void checkAndThrow() throws Exception {
        Throwable thr = getException();
        if(thr==null) return;
        if(mStackTraceManager!=null)
            mStackTraceManager.fillException(thr);
        if( thr instanceof Exception )
            throw (Exception)thr;
        else if( thr instanceof Error )
            throw (Error)thr;
    }
    
    /**
     * Add a exception handle that will be invoked when a exception is caught during running process
     * @param handle A instance of IExceptionHandle
     */
    public void addExceptionHandle(IExceptionHandle handle){
        Configuration config = getConfiguration();
        if(config!=null)
            config.addExceptionHandle(handle);
    }
/*    
    public void addFirstExceptionHandle(IExceptionHandle handle){
        Configuration config = getConfiguration();
        if(config!=null)
            config.addFirstExceptionHandle(handle);        
    }
*/    
    public void addExceptionHandles(Collection handle_list){
        Configuration config = getConfiguration();
        if(config!=null)
            config.addExceptionHandles(handle_list);
        /*
        if(exception_handle_list==null) exception_handle_list = handle_list;
        else exception_handle_list.addAll(handle_list);
        */
    }

    public boolean handleException(Throwable thr){
        Configuration config = getConfiguration();
        // Check if is in exception handle
        if(mInExceptionHandle){ 
            throwUnhandledException(thr);
            return false;
        }
        // Check if has exception handle list
        Collection exception_handle_list = null;
        exception_handle_list = getCurrentExceptionHandleInStack();
        //if(exception_handle_list==null)
        //    exception_handle_list = config==null?null:config.getExceptionHandles();
        if(exception_handle_list==null && config!=null)
            exception_handle_list = config.getExceptionHandles();
        
        if(exception_handle_list==null) return false;  
        // Handle exception
        mInExceptionHandle = true;
        Iterator it = exception_handle_list.iterator();
        while(it.hasNext()){
            IExceptionHandle handle = (IExceptionHandle)it.next();
            boolean result = handle.handleException(this, thr);
            if(result){
                mInExceptionHandle = false;
                return true;
            }
        }
        mInExceptionHandle = false;
        return false;
    }
    
    public void clearException(){
        procException = null;
    }
    /**
     * Run the procedure
     */
    public void run() {
        try{
            if(current_entry_list !=null && current_iterator==null){
                // enter & exit
                if(mSaveStackTrace && mStackTraceManager!=null)
                    mStackTraceManager.enter(current_entry_list);
                current_entry_list.run(this);
                if(mSaveStackTrace && mStackTraceManager!=null)
                    mStackTraceManager.exit();
                // end
            }
            else
                while(mIsContinue && current_entry_list !=null && current_iterator!=null){
                    while(mIsContinue && current_iterator.hasNext()){
                        IEntry entry = (IEntry)current_iterator.next();
                        // enter & exit
                        if(mSaveStackTrace && mStackTraceManager!=null)
                            mStackTraceManager.enter(entry);
                        entry.run(this);
                        if(mSaveStackTrace && mStackTraceManager!=null)
                            mStackTraceManager.exit();
                        // end
                        
                    }
                    if(mRunningList) return;
                    IEntry prev = current_entry_list;
                    IEntry owner = current_entry_list.getOwner();
                    if(owner ==null) 
                        current_entry_list = null;
                    else if(owner instanceof EntryList){
                        current_entry_list = (EntryList)owner;
                        current_iterator=current_entry_list.locateEntry(prev);
                        if(current_iterator!=null)current_iterator.next();
                    }else{
                        // log a error info in the future
                        current_entry_list = null;
                    }                
                }
        }catch(Throwable ex){
                throwException(ex);
        }
    }
    
    /**
     * Run a partion of procedure, for IEntry.run to invoke
     * @param list An EntryList instance to run
     */
    public void run(EntryList	list) {
        List l = list.getEntryList();
        if(l==null)return;
        if(l.size()==0) return;
        // save prev internal state
        EntryList		prev_entry_list = current_entry_list;
        ListIterator	prev_iterator   = current_iterator;
        boolean			prev_flag = mRunningList;
        // set current list to run
        current_entry_list = list;
        current_iterator   = list.getEntryList().listIterator();
        mRunningList = true;
        run();
        // restore prev internal state
        current_entry_list = prev_entry_list;
        current_iterator   = prev_iterator;
        mRunningList = prev_flag;
    }
    
    public void fireEvent(String event_name){
        fireEvent(event_name, null);
    }
    
    /**
     * Fire a named event, underlying participants that are hooked with this event
     * may be invoked
     * @param event_name name of event
     */
    public void fireEvent(String event_name, Object[] parameters){
        Configuration config = getConfiguration();
        if(config==null) return;        
        // set current event
        current_event = event_name;
        try{
            config.fireEvent(event_name, parameters, this, config.getHandleManager());
            // modify?
            /*
            int result = config.fireEvent(event_name, parameters, this, config.getHandleManager());
            if(EventModel.HANDLE_STOP==result)
                stop();
            */    
        }catch(Exception ex){
            //System.out.println("error in "+config.getCurrentHandle());
            //ex.printStackTrace();
            throwException(ex);
        }
        /*
        for(int i=EventModel.PRE_EVENT; i<=EventModel.POST_EVENT; i++){
            current_event_sequence = i;
            // invoke event listeners
            ListIterator	lsnr_it = handle_manager.getEventListenerIterator();
            if(lsnr_it!=null){
                while(lsnr_it.hasNext()){
                    handle_flag = EventModel.HANDLE_NORMAL;
                    IEventListener lnr = (IEventListener)lsnr_it.next();
                    try{
                        lnr.onEvent(this, i,event_name);
                    }catch(Exception ex){
                        throwException(ex);
                        return;
                    }
                    if(handle_flag==EventModel.HANDLE_NO_SAME_SEQUENCE)
                        break;
                    else if(handle_flag==EventModel.HANDLE_STOP)
                        return;
                }
            }
            // invoke event handles hooked with this event
            Iterator it = handle_manager.getEventHandleIterator(event_name, i);
            if(it==null) continue;
            while(it.hasNext()){
                handle_flag = EventModel.HANDLE_NORMAL;
                IEventHandle handle = (IEventHandle)it.next();
                try{
                    handle.handleEvent(i, this, parameters);
                }catch(Exception ex){
                    throwException(ex);
                    return;
                }
                if(handle_flag==EventModel.HANDLE_NO_SAME_SEQUENCE)
                    break;
                else if(handle_flag==EventModel.HANDLE_STOP)
                    return;                
            }
        }
        */
    }
    
    public String getCurrentEvent(){
        return current_event;
    }
    
    public int getCurrentSequence(){
        return current_event_sequence;
    }
    
    /**
     * Creates a ProcedureRunner that share config and context with current
     * @param proc Procedure to run
     * @return created ProcedureRunner instance
     */
    public ProcedureRunner spawn(Procedure proc){
        ProcedureRunner child = new ProcedureRunner();
        child.setProcedure(proc);
        child.mCaller = this;
        child.setContext(mContext);
        child.mRuntimeContext = mRuntimeContext;
        //child.config_map = config_map;
        // child.config = config;
        child.mUncertainEngine = mUncertainEngine;
        child.mLogger = mLogger;
        child.mSaveStackTrace = this.mSaveStackTrace;
        child.reset();
        //child.mStackTraceManager = this.mStackTraceManager;
        return child;
    }
    
    public void call(Procedure another){
        ProcedureRunner newRunner = spawn(another);
        newRunner.run();
        if(newRunner.getException()!=null)
            throwException(newRunner.getException());
    }
    
    private void createDefaultConfig(){
        Configuration config = getConfiguration();
        if(config==null){
            if(mUncertainEngine!=null)
                config = mUncertainEngine.createConfig();
            else
                config = new Configuration();
            setConfiguration(config);
         }
    }
    
    public void setConfiguration( Configuration config ){
        mRuntimeContext.setConfig(config);
        //this.config = config;
    }
    
    public Configuration getConfiguration(){
        return mRuntimeContext==null?null:mRuntimeContext.getConfig();
        //return config;
    }
    
    /**
     * Add configuration and all participants of a Configuration
     * @param procConfig Configuration containing parsed CompositeMap
     */
    public void addConfiguration( Configuration procConfig ){
        /*
        config_map.addChilds(procConfig.getConfigList());
        if(handle_manager!=null){
	        Iterator it = procConfig.getParticipantList().iterator();
	        while(it.hasNext()){
	            handle_manager.addParticipant(it.next());
	        }
        }
        if(procConfig.getExceptionHandles()!=null){
            if(exception_handle_list==null) exception_handle_list = new LinkedList();
            exception_handle_list.addAll(procConfig.getExceptionHandles());
        }
        */
        Configuration config = getConfiguration();
        if(config==null)
            setConfiguration(procConfig);
        else
            config.addConfiguration(procConfig);
    }
    

    //TODO remove in future
    public UncertainEngine getUncertainEngine() {
        return mUncertainEngine;
    }

    public void setUncertainEngine(UncertainEngine uncertainEngine) {
        this.mUncertainEngine = uncertainEngine;
        createDefaultConfig();
        /*
        if( getConfiguration()==null)
            setConfiguration(uncertainEngine.createConfig());
        */    
    }
    
    /**
     * set value of a context field defined in current running procedure
     * @param name name of context field
     * @param value value to set
     * @throws java.lang.IllegalArgumentException if given field name is not defined
     */
    public void setContextField(String name, Object value){
        Field field = mProcedure.getField(name);
        if(field==null) throw new IllegalArgumentException("Field "+name+" is not defined in procedure");
        mContext.putObject(field.getPath(), value, true);
    }
    
    /**
     * get value of a context field defined in current running procedure
     * @param name name of context field to get
     * @return value of specified context field
     * @throws java.lang.IllegalArgumentException if given field name is not defined
     */
    public Object getContextField(String name){
        Field field = mProcedure.getField(name);
        if(field==null) throw new IllegalArgumentException("Field "+name+" is not defined in procedure");
        return mContext.getObject(field.getName());
    }
    
    /**
     * Transfer context fields to or from participants
     * @param fields a collection of uncertain.proc.Field instance to define what fields to transfer
     * @param write_to_participant true if write context fields to participant instance, false if 
     * read from participant instance 
     * @throws Exception any posible exception that thrown when reading or writing participant instance
     */
    public void transferContextFields(Collection fields, boolean write_to_participant)
    throws Exception 
    {
        Configuration config = getConfiguration();
        Collection participants = config ==null? null: config.getParticipantList();
        if(participants==null) return;
        OCManager oc_manager = mUncertainEngine==null? OCManager.getInstance(): mUncertainEngine.getOcManager();
        // iterate through all fields
        Iterator it = fields.iterator();
        while(it.hasNext()){
            Field field = (Field)it.next();
            String name = field.getName();
            if(write_to_participant){
                // write context field to all participants
                Object value = mContext.getObject(field.getPath());
                if(value==null) continue;
                for(Iterator pit = participants.iterator(); pit.hasNext();){
                    Object participant = pit.next();
                    oc_manager.setAttribute(participant, name, value);
                }
            }else{
                // read field values from participants and put into context
                for(Iterator pit = participants.iterator(); pit.hasNext();){
                    Object participant = pit.next();
                    Object value = oc_manager.getAttribute(participant, name);
                    if(value!=null)
                        mContext.putObject(field.getPath(), value, true);
                }                
            }
        }        
    }
    
    public ILogger getLogger(){
        if(mCaller!=null)
            return mCaller.getLogger();
        if(mLogger!=null) return mLogger;
        ILogger logger = (ILogger)mRuntimeContext.getInstanceOfType(ILogger.class);
        if(logger==null){
            ILoggerProvider provider = (ILoggerProvider)mRuntimeContext.getInstanceOfType(ILoggerProvider.class);
            if(provider!=null)
                return provider.getLogger(LOGGING_TOPIC);
            else
                return DummyLogger.getInstance();
        }else
            return logger;
    }
    
    public void setLogger( ILogger logger ){
        this.mLogger = logger;
    }

    public Throwable getLatestException(){
        return lastException;
    }
    
    public StackTraceManager getStackTraceManager(){
        return mStackTraceManager;
    }
    
    public void setSaveStackTrace( boolean flag ){
        this.mSaveStackTrace = flag;
    }

    // 2012-12-18
    // Add exception handle stack to implement nested exception handle
    
    public void pushExceptionHandle(Collection handles ){
        if(mExceptionHandleStack==null)
            mExceptionHandleStack = new LinkedList();
        mExceptionHandleStack.add(0, handles);
    }
    
    public void popExceptionHandle(){
        if(mExceptionHandleStack != null && mExceptionHandleStack.size()>0)
            mExceptionHandleStack.remove(0);
        else
            throw new IllegalStateException("Illegal pop. Should call push first");
    }
    
    private Collection getCurrentExceptionHandleInStack(){
        if(mExceptionHandleStack==null)
            return null;
        if(mExceptionHandleStack.size()==0)
            return null;
        return (Collection)(mExceptionHandleStack.get(0));
    }

}
