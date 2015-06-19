/*
 * Created on 2009-12-3 ����02:08:56
 * Author: Zhou Fan
 */
package uncertain.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.core.UncertainEngine;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IConfigurable;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;

public class ParticipantManager extends AbstractLocatableObject implements IParticipantManager, IGlobalInstance, IConfigurable {
    
    public static final String KEY_CATEGORY = "category";

    public static final String KEY_CLASS = "class";

    /**
     * @param objectRegistry
     * @param objectCreator
     */
    public ParticipantManager(IObjectRegistry objectRegistry,
            IObjectCreator objectCreator) {
        mObjectRegistry = objectRegistry;
        mObjectCreator = objectCreator;
        mParticipantsListMap = new HashMap();
        mParticipantConfigMap = new HashMap();
        
        ILoggerProvider p = LoggingContext.getLoggerProvider(mObjectRegistry);
        mLogger = p.getLogger(UncertainEngine.UNCERTAIN_LOGGING_TOPIC);
    }

    static final List   EMPTY_LIST =  Collections.unmodifiableList(new LinkedList());
    
    // category -> List<Object> created instance list
    Map                 mParticipantsListMap;
    // category -> List<Class> class config
    Map                 mParticipantConfigMap;
    IObjectRegistry     mObjectRegistry;
    IObjectCreator      mObjectCreator;
    ILogger             mLogger;

    /** Lazy load participant instance */
    protected List getParticipantInstanceList( String category)

    {
        List lst = (List)mParticipantsListMap.get(category);
        if(lst == null){
        	lst = new LinkedList();
        	mParticipantsListMap.put(category, lst);
        }
        return lst;        
    }
    
    protected List getParticipantConfigList( String category ){
        List lst = (List)mParticipantConfigMap.get(category);
        if(lst==null){
            lst = new LinkedList();
            mParticipantConfigMap.put(category, lst);
        }
        return lst;
    }

    public List getParticipantList( String category ){
            return getParticipantInstanceList(category);
/*
            try{

    }catch(Exception ex){
            throw new RuntimeException("Error when creating participant list for category "+category, ex);
        }
*/        
    }

    public Configuration getParticipantsAsConfig( String category ){
        Configuration config = new Configuration();
        List participants = getParticipantList(category);
        if(participants!=null)
        for(Iterator it = participants.iterator(); it.hasNext(); ){
            config.addParticipant(it.next());
        }
        return config;
    }
    
    /** Add a participant instance under specified category */
    public void addParticipant( String category, Class instance_type ){
        getParticipantConfigList(category).add(instance_type);
    }
    
    Object createInstance( String cls_name )
        throws Exception
    {
        Class cls = Class.forName(cls_name);
        Object participant = mObjectRegistry.getInstanceOfType(cls);
        if(participant == null)
            participant = mObjectCreator.createInstance(cls);
        return participant;
    }
    
    /**
     * Load participant by config
     * @param config A CompositeMap instance containing 
        <participant-list category="service">
            <participant class="a.Type1" />
            <participant class="a.Type2" />
        </participant-list>
     * @param category
     */
    void loadConfigByCategory( CompositeMap config, String category )
    {
        List lst = getParticipantConfigList(category);
        Iterator it = config.getChildIterator();
        if(it!=null)
            while(it.hasNext()){
                CompositeMap item = (CompositeMap)it.next();
                String cls_name = item.getString(KEY_CLASS);
                if(cls_name==null)
                    //throw new ConfigurationError("Must set 'class' property:"+item.toXML());
                    throw BuiltinExceptionFactory.createAttributeMissing(this, KEY_CLASS);
                try{
                    Class cls = Class.forName(cls_name);
                    lst.add(cls);
                }catch(ClassNotFoundException ex){
                    throw BuiltinExceptionFactory.createClassNotFoundException(this, cls_name);
                }
            }
    }
    
    public void loadConfig( CompositeMap config ){
        Iterator it = config.getChildIterator();
        if(it!=null)
            while(it.hasNext()){
                CompositeMap child = (CompositeMap)it.next();
                String category = child.getString(KEY_CATEGORY);
                if(category==null)
                    //throw new ConfigurationError("Must set 'category' property:"+child.toXML());
                    throw BuiltinExceptionFactory.createAttributeMissing(this, KEY_CATEGORY);
                loadConfigByCategory(child, category);
            }
    }
    
    public void beginConfigure(CompositeMap config){
        loadConfig(config);
    }
    
    public void endConfigure(){
        
    }

    public void addIServiceParticipant(IServiceParticipant participant) {
        String scope = participant.getScope();
        List lst = getParticipantConfigList(scope);
        lst.add(participant);
    }
    
    public void onInitialize()
        throws Exception
    {        
        Iterator cit = mParticipantConfigMap.entrySet().iterator();
        while(cit.hasNext()){
            int num = 1;
            Map.Entry entry = (Map.Entry)cit.next();
            String category = entry.getKey().toString();
            List cls_list = (List)entry.getValue();
            mLogger.info("=========== Loading participant in {"+category+"} scope =============" );
            List lst = new LinkedList();
            for(Iterator it = cls_list.iterator(); it.hasNext(); ){
                Class cls = (Class)it.next();
                mLogger.info("No."+num+" type="+cls.getName());
                Object participant = mObjectRegistry.getInstanceOfType(cls);
                if(participant == null){
                    participant = mObjectCreator.createInstance(cls);
                    mObjectRegistry.registerInstance(cls, participant);
                    mLogger.info("New instance created");
                }
                else
                    mLogger.info("Got from global instance registry");
                if(participant==null)
                    throw new RuntimeException("Can't create instance of "+cls);
                lst.add(participant);
                num++;
            }
            mParticipantsListMap.put(category, lst);            
        }
    }
}
