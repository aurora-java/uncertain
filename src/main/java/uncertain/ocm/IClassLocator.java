/*
 * Created on 2005-7-15
 */
package uncertain.ocm;
import uncertain.composite.CompositeMap;

/**
 * Maps XML element to Java class by element name and name space
 * @author Zhou Fan
 * 
 */
public interface IClassLocator {
    
    public String getClassName(CompositeMap	config);

}