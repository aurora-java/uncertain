/*
 * Created on 2009-12-3 ÏÂÎç02:08:56
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
import uncertain.core.ConfigurationError;
import uncertain.core.IGlobalInstance;
import uncertain.ocm.IConfigurable;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;

public class ParticipantManager implements IParticipantManager, IGlobalInstance, IConfigurable {
    
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
    }

    static final List   EMPTY_LIST =  Collections.unmodifiableList(new LinkedList());
    
    // category -> List<Object> created instance list
    Map                 mParticipantsListMap;
    // category -> List<Class> class config
    Map                 mParticipantConfigMap;
    IObjectRegistry     mObjectRegistry;
    IObjectCreator      mObjectCreator;

    /** Lazy load participant instance */
    protected List getParticipantInstanceList( String category)
        throws Exception
    {
        List lst = (List)mParticipantsListMap.get(category);
        if(lst==null){            
            List cls_list = (List)mParticipantConfigMap.get(category);
            if(cls_list==null)
                return null;
            lst = new LinkedList();
            for(Iterator it = cls_list.iterator(); it.hasNext(); ){
                Class cls = (Class)it.next();
                Object participant = mObjectRegistry.getInstanceOfType(cls);
                if(participant == null)
                    participant = mObjectCreator.createInstance(cls);
                if(participant==null)
                    throw new RuntimeException("Can't create instance of "+cls);
                lst.add(participant);
            }
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
        try{
            return getParticipantInstanceList(category);
        }catch(Exception ex){
            throw new RuntimeException("Error when creating participant list for category "+category, ex);
        }
    }

    public Configuration getParticipantsAsConfig( String category ){
        Configuration config = new Configuration();
        List participants = getParticipantList(category);
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
                    throw new ConfigurationError("Must set 'class' property:"+item.toXML());
                try{
                    Class cls = Class.forName(cls_name);
                    lst.add(cls);
                }catch(ClassNotFoundException ex){
                    throw new ConfigurationError("Can't create instance of "+cls_name);
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
                    throw new ConfigurationError("Must set 'category' property:"+child.toXML());
                loadConfigByCategory(child, category);
            }
    }
    
    public void beginConfigure(CompositeMap config){
        loadConfig(config);
    }
    
    public void endConfigure(){
        
    }
}
