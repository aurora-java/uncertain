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
public class ClassMapping implements IClassLocator {
    
    String elementName;
    String className;
    String packageName;

    /**
     * Default constructor
     */
    public ClassMapping() {

    }
    
    public String toString(){
        String s= "class mapping:"+elementName+" -> ";
        if(packageName!=null) s=s+packageName+'.'+className;
        else s=s+className;
        return s;
    }

    /**
     * @return Returns the className.
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
    /**
     * @return Returns the elementName.
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
    /**
     * @return Returns the packageName.
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
