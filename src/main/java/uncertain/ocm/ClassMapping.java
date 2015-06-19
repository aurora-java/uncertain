/*
 * Created on 2005-7-21
 */
package uncertain.ocm;
import uncertain.composite.CompositeMap;

/**
 * ClassMapping
 * @author Zhou Fan
 * 
 */
public class ClassMapping extends AbstractLocatableObject implements IClassLocator {
    
    String elementName;
    String className;
    String packageName;

    /**
     * Default constructor
     */
    public ClassMapping() {

    }
    
    public String toString(){
        String s= elementName+" -> ";
        if(packageName!=null) s=s+packageName+'.'+className;
        else s=s+className;
        return s;
    }

    /* (non-Javadoc)
     * @see uncertain.ocm.ClassMappingMBean#getClassName()
     */
    public String getClassName() {
        return className;
    }
    /**
     * @param className The className to set.
     */
    public void setClassName(String className) {
        this.className = className;
    }
    /* (non-Javadoc)
     * @see uncertain.ocm.ClassMappingMBean#getElementName()
     */
    public String getElementName() {
        return elementName;
    }
    /**
     * @param elementName The elementName to set.
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }
    /* (non-Javadoc)
     * @see uncertain.ocm.ClassMappingMBean#getPackageName()
     */
    public String getPackageName() {
        return packageName;
    }
    /**
     * @param packageName The packageName to set.
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public String getClassName(CompositeMap config) {
        if(packageName!=null) return packageName + '.' + className;
        else return className;
    }
}
