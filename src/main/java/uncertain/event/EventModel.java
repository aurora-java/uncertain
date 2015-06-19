/*
 * Created on 2005-5-24
 */
package uncertain.event;

/**
 * Defines some constant flag in procedure event model
 * @author Zhou Fan
 * 
 */
public final class EventModel {
    
    static final String[] sequence_names = {"pre", "on","post"};
    
    public static final int PRE_EVENT = 0;
    public static final int ON_EVENT = 1;
    public static final int POST_EVENT = 2;
    
    public static final int HANDLE_NORMAL = 0;
    public static final int HANDLE_NO_SAME_SEQUENCE = 1;
    public static final int HANDLE_STOP = 2;
    
    public static final int NORMAL_PARTICIPANT = 0;
    public static final int SINGLTON_PARTICIPANT = 1;
    
    public static String getFullEventName(int sequence, String event_name){
        //assert sequence>=0 && sequence<3;
        return sequence_names[sequence]+event_name;
    }
    
    public static int getSequence(String sequence_name){
        for(int i=0; i<sequence_names.length; i++){
            if(sequence_names[i].equals(sequence_name))
                return i;
        }
        return -1;
    }
    
    public static String getSequenceName(int sequence){
        return sequence_names[sequence];
    }

}
