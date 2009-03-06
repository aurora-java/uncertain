/*
 * Created on 2005-10-8
 */
package uncertain.composite;

/**
 * ElementIdentifier
 * @author Zhou Fan
 * 
 */
public class ElementIdentifier {
    
    String	nameSpace;
    String	elementName;
    String  full_name;  
    
    /**
     * Constructor from namespace and element name
     */
    public ElementIdentifier(String nameSpace, String elementName ) {
        this.nameSpace = nameSpace;
        this.elementName = elementName;
        if(nameSpace!=null)
        	full_name = nameSpace + elementName;
        else
            full_name = elementName;
    }
    
    public String getNameSpace(){
        return nameSpace;
    }
    
    public String getElementName(){
        return elementName;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {        
        if(obj instanceof ElementIdentifier){
            ElementIdentifier id = (ElementIdentifier)obj;
            if(nameSpace==null) return elementName.equals(id.elementName);            
            else {
                //System.out.println(this+" -> "+obj);
                return elementName.equals(id.elementName) && nameSpace.equals(id.nameSpace);
            }
        }else{
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {        
        return full_name.hashCode();
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {        
        return nameSpace+":"+elementName;
    }
}
