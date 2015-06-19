/*
 * Created on 2005-5-18
 */
package uncertain.ocm;
import uncertain.composite.CompositeMap;

/**
 * For adding any child container
 * @author Zhou Fan
 * 
 */
public interface IChildContainerAcceptable {
    
    /** Add a child container 
     * @param child container that don't conform to any add method
     */
    public void addChild(CompositeMap child);

}
