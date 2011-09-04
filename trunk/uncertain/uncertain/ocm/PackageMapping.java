/**
 * Created on: 2004-6-11 17:43:45
 * Author:     zhoufan
 */
package uncertain.ocm;

import java.util.HashMap;

import uncertain.composite.CompositeMap;

/**
 *  Get class name by package name + converted element name or registered class name
 */
public class PackageMapping extends AbstractLocatableObject implements IClassLocator {
	
	String 		package_name;
	String 		name_space;
	HashMap		class_mapping;
	
	boolean		keep_original_input = false;

	/**
	 * Constructor for DefaultObjectFactory.
	 */
	
	public PackageMapping(){
	}
	
	public String toString(){
	    return "package mapping: "+name_space+" -> "+package_name;
	}
	
	public PackageMapping(String _name_space, String _package_name) {
		name_space = _name_space;
		package_name = _package_name;
	}	
	
	public void setPackageName(String name){
	    package_name = name;
	}
	
	public String getPackageName(){
	    return package_name;
	}

    /**
     * @return Returns the keep_original_input.
     */
    public boolean getKeepOriginalInput() {
        return keep_original_input;
    }
    /**
     * @param keep_original_input The keep_original_input to set.
     */
    public void setKeepOriginalInput(boolean keep_original_input) {
        this.keep_original_input = keep_original_input;
    }
    /**
     * @return Returns the name_space.
     */
    public String getNameSpace() {
        return name_space;
    }
    /**
     * @param name_space The namespace to set.
     */
    public void setNameSpace(String name_space) {
        this.name_space = name_space;
    }
	
    /**
     * Add a specific mapping between element name and class name
     * @param m
     */
    public void addClassMapping(ClassMapping m){
        if(class_mapping==null) class_mapping = new HashMap();
        class_mapping.put(m.getElementName(),m);
    }
    
    /*
    public String getMappedClassName(String element_name){
        if(class_mapping==null) return null;
        ClassMapping m = (ClassMapping)class_mapping.get(element_name);
        if(m!=null) return m.getClassName();
        else return null;
    }
    */
    
    public ClassMapping getClassMapping(String element_name){
        if(class_mapping==null) return null;
        ClassMapping m = (ClassMapping)class_mapping.get(element_name);
        return m;
    }
    
    /**
     * Get mapped Java class name by namespace and element name
     */
    public String getClassName(CompositeMap	config){
		String element_name = config.getName();
		if( element_name == null) return null;
		ClassMapping m = getClassMapping(element_name);
		if(m!=null){
		    if(m.getPackageName()==null)
		        return package_name + '.' + m.getClassName();
		    else
		        return m.getClassName(config);
		}
		else{
		    String class_name = element_name;
		    if(!keep_original_input ) class_name = NamingUtil.toClassName(element_name);
			return package_name + '.' + class_name;
		}
    }
	
}
