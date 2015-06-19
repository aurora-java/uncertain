/*
 * Created on 2005-7-24
 */
package uncertain.ocm;
import uncertain.composite.CompositeMap;
/**
 * Key operation in OC mapping process
 * @author Zhou Fan 
 */

public interface IMappingHandle {
    
    /**
     * create a new instance defined by container
     * @param container data container from which object will be created
     * @return
     */
    public Object createNewInstance(CompositeMap container);
    
    /**
     * called by framework when a data container can't be mapped to a class
     * @param container
     */
    public void getUnknownContainer(CompositeMap container);
    
    /**
     * Whether accept data container that can't be mapped to a class
     * @return
     */
    public boolean acceptUnknownContainer();

}
