/*
 * Created on 2005-6-14
 */
package uncertain.ocm;

/** Create object by constructor reflection, using instances associated with specific class
 *  as parameter
 *  @author Zhou Fan
 * 
 */
public interface IObjectRegistry {
    

    /**
     * Get instance for specified type which is previously registered or can get from parent ObjectSpace
     * @param type class of instance to get
     * @return instance of that type, or null if not registered
     */
    public Object getInstanceOfType(Class type);
    
    /** Associate a instance with specified type, without further association with super class
     *  or implemented interface
     * @param type The type to associate with
     * @param instance of specified type
     */
    public void registerInstanceOnce( Class type, Object instance);

    
    /** Associate a instance with a certain type, also associate all super types and interfaces
     *  of the type with this instance, if super type/interface has not been associated yet.     * 
     * @param type The type to associate with
     * @param instance of specified type
     */
    public void registerInstance( Class type, Object instance);
    
    /**
     * Equals to registerParameter(instance.getClass(), instance)
     * @param instance parameter instance to register
     */
    public void registerInstance(Object instance);


}
