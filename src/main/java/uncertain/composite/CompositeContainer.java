/*
 * Created on 2005-4-18
 *
 * Defines a general purpose data container that extends from Map, and may
 * contain a list of childs
 */
package uncertain.composite;

//import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * CompositeContainer
 * @author Zhou Fan
 * 
 */
public interface CompositeContainer extends Map {

    
    /** get list of childs
     * @return List of childs or null if no childs
     */
    public List getChilds();
    
    /** same as {@link getChilds()} except that if child list is null, a empty list is created
     * thus this method will never return null
     * @return child list
     */
    public List getChildsNotNull();
    
    /** get Iterator object of child list
     * @return child Iterator, or null if child list is null
     */
    public Iterator getChildIterator();
    
    public int iterate( IterationHandle handle, boolean root_first);
    
    
}
