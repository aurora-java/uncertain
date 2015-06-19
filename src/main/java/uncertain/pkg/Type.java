/*
 * Created on 2011-9-28 下午09:19:47
 * $Id$
 */
package uncertain.pkg;

public class Type {
    
    public Type(){
        
    }
    
    String  typeName;
    Class   type;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) 
        throws ClassNotFoundException
    {
        this.typeName = typeName;
        this.type = Class.forName(typeName);
    }
    
    public Class getType(){
        return type;
    }

}
