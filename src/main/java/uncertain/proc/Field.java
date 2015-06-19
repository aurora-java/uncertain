/*
 * Created on 2006-11-20
 */
package uncertain.proc;

import uncertain.core.ConfigurationError;
import uncertain.util.StringEnum;

/**
 * Implements context field defined in procedure config
 * @author Zhou Fan
 *
 */
public class Field {

    public static final String  USAGE_INPUT = "input";    
    public static final String  USAGE_RETURN = "return";
    public static final String[] USAGE_VALUE_STRING = {USAGE_INPUT, USAGE_RETURN };

    public static final StringEnum  USAGE_VALUES = new StringEnum(USAGE_VALUE_STRING);
    String      name;
    String      path;
    String      type;
    String      usage;
    String      description;
    
    Class       type_class;
    int         usage_value = -1;
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
        if(path==null) path = '@' + name;
    }
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    /**
     * @param mType the type to set
     */
    public void setType(String t) throws ClassNotFoundException {
        type = t;
        if(type.indexOf('.')<0) type = "java.lang." + type;
        type_class = Class.forName(type);
    }
    /**
     * @return the usage
     */
    public String getUsage() {
        return usage;
    }
    /**
     * @param usage the usage to set
     */
    public void setUsage(String usage) {
        this.usage = usage.toLowerCase();
        this.usage_value = USAGE_VALUES.valueOf(this.usage);
        if(!USAGE_VALUES.valid(this.usage_value)) 
            throw new ConfigurationError("Unknown usage value:"+usage); 
    }
    
    public boolean isReturnField(){
        return usage_value == 1;
    }
    
    public boolean isInputField(){
        return usage_value == 0;
    }
    
    
    
    /** Default constructor */
    public Field(){
        
    }
    
    /** Constructor with name and type */
    public Field(String name, String type) throws ClassNotFoundException {
        setName(name);
        setType(type);
    }
    
    /** Constructor with name and Class */
    public Field(String name, Class type)  {
        this.type_class = type;
        setName(type.getName());
    }
    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
/*    
    public Object readFrom(Object obj, CompositeMap context)
        throws Exception
    {
        
        
    }
    
    public void writeTo(Object obj, CompositeMap context) 
        throws Exception 
     {
        
    }
*/    
    public String toString(){
        StringBuffer desc = new StringBuffer();
        desc.append(type).append(' ').append(name);
        if(usage!=null) desc.append(' ').append(usage);
        desc.append(':').append(path);
        if(description!=null) desc.append(':').append(description);
        return desc.toString();
    }
    
}
