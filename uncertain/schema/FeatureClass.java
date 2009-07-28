/*
 * Created on 2009-7-28
 */
package uncertain.schema;

public class FeatureClass {
    
    public FeatureClass(){
        
    }
    
    String  name;
    Class   type;

    public String getName() {
        return name;
    }

    public void setName(String name) throws ClassNotFoundException {
        this.name = name;
        type = Class.forName(name);
    }
    
    public Class getType(){
        return type;
    }

}
