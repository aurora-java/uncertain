/*
 * Created on 2005-5-24
 */
package uncertain.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import uncertain.proc.ParticipantRegistry;
import uncertain.proc.ParticipantRegistry.HandleMethod;


/**
 * Maintains mapping between event hand event handle
 * @author Zhou Fan
 * 
 */
public class HandleManager implements Cloneable {
    
    ParticipantRegistry	registry;
    
    // event name -> List[] handle list
    HashMap	            event_handle_map;
    // list of IEventHandle
    LinkedList			listener_list;
    // Maintain all participant instances added
    HashSet             participants_set;

    public HandleManager(){
        registry = ParticipantRegistry.defaultInstance();
        event_handle_map = new HashMap();
    }
    
    public HandleManager(ParticipantRegistry m){
        registry = m;
        event_handle_map = new HashMap();
    }
   
    private List[] createHandleList(){
        List[] lst = new List[3];
        return lst;
    }
    
    private void addHandle(List[] handle_list, int sequence, IEventHandle handle, boolean add_to_first){
       List l = handle_list[sequence];
       if(l==null){
           l = new LinkedList();
           handle_list[sequence] = l;
       }
       if(add_to_first)
           l.add(0,handle);
       else
       	   l.add(handle);
    }
 
    public void addEventHandle(String event_name, int sequence, IEventHandle handle, boolean add_to_first){
        if(sequence<0 || sequence>2) throw new IllegalArgumentException("sequence must be one of the following values: EventModel.PRE_EVENT , EventModel.ON_EVENT , EventModel.POST_EVENT");
        event_name = event_name.toLowerCase();
        List[] handle_list = (List[])event_handle_map.get(event_name);
        if(handle_list==null){ 
            handle_list = createHandleList();
            event_handle_map.put(event_name,handle_list);
        }
        addHandle(handle_list,sequence,handle, add_to_first);
    }
    
    public void addEventHandle(String event_name, int sequence, IEventHandle handle){
        addEventHandle(event_name, sequence, handle, true);
    }
    
    public List getEventHandleList(String event_name, int sequence){
        if(sequence<0 || sequence>2) throw new IllegalArgumentException("sequence must be one of the following values: EventModel.PRE_EVENT , EventModel.ON_EVENT , EventModel.POST_EVENT");
        event_name = event_name.toLowerCase();
        List[] handle_list = (List[])event_handle_map.get(event_name);
        if(handle_list==null) return null;        
        return handle_list[sequence];
    }
    
    public ListIterator getEventHandleIterator(String event_name, int sequence){
        List l = getEventHandleList(event_name, sequence);
        if(l==null)return null;
        else return l.listIterator();
    }
    
    public ListIterator getEventListenerIterator(){
        if(listener_list!=null)
            return listener_list.listIterator();
        else
            return null;
    }
    
    public void addParticipant(Object participant, boolean add_to_first){
        // Add to participant set
        if(participants_set==null) participants_set = new HashSet();
        participants_set.add(participant);
        // Check if it's IEventListener
        if(participant instanceof IEventListener){
            if(listener_list==null) listener_list = new LinkedList();
            if(add_to_first) listener_list.addFirst(participant);
            else listener_list.add(participant);
        }
        // Add handle methods
        int i=0;
        Class cls = participant.getClass();
        ParticipantRegistry.HandleMethod[] methods = registry.getHandleMethods(cls);
        for(i=0; i<methods.length; i++){
            ParticipantRegistry.HandleMethod hm = methods[i];
            ReflectionMethodHandle handle = new ReflectionMethodHandle(participant,hm.method,hm.arg_type);
            addEventHandle(hm.event_name, hm.sequence, handle, add_to_first);
        }
    }

    public void addParticipant(Object participant){
        addParticipant(participant, true);
    }
    
    public Set getParticipants(){
        if(participants_set==null) return null;
        return participants_set;
    }
    
    public Map getHandleMap(){
        return event_handle_map;
    }
    
    public Object clone() {
        HandleManager manager = new HandleManager(registry);
        manager.event_handle_map.putAll(event_handle_map);
        if(listener_list!=null){
            manager.listener_list = new LinkedList();
            manager.listener_list.addAll(listener_list);
        }
        if(participants_set!=null){
            manager.participants_set = new HashSet();
            manager.participants_set.addAll(participants_set);
        }
        return manager;
    }
    
    public void addSingleEventHandle( ISingleEventHandle handle, boolean add_to_first ){
        addEventHandle( handle.getEvent(), handle.getHandleSequence(), handle, add_to_first);
    }
    
    public void addSingleEventHandle( ISingleEventHandle handle ){
        addSingleEventHandle( handle, true );
    }
    
    public void clear(){
        if( event_handle_map != null)
            event_handle_map.clear();
        if( listener_list != null )
            listener_list.clear();
        if( participants_set != null)
            participants_set.clear();
    }
    
}
