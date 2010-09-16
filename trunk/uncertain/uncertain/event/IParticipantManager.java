/*
 * Created on 2009-12-3 ÏÂÎç02:00:49
 * Author: Zhou Fan
 */
package uncertain.event;

import java.util.List;

/**
 * Manages predefined global participants, and provide list of participant
 * by category.  
*/
public interface IParticipantManager {
    
    public static final String  GLOBAL_SCOPE = "global";
    
    public static final String  APPLICATION_SCOPE = "application";
    
    public static final String SERVICE_SCOPE = "service";
    
    /** Add a participant instance under specified category */
    public void addParticipant( String category, Class participant_type);
    
    /** return a List<object> of participants in specified category 
     * @param category A string to identify what type of participant is desired.
     * Actual meaning of category is subject to implementation and configuration. 
     */
    public List getParticipantList( String category );    
    
    /** same as getParticipantList(), but returns a Configuration instance that
     * adds all participants
     */
    public Configuration getParticipantsAsConfig( String category );
    
    public void addIServiceParticipant( IServiceParticipant participant );

}
