/*
 * Created on 2005-10-8
 */
package uncertain.ocm;

import uncertain.composite.QualifiedName;

/**
 * FeatureMapping
 * @author Zhou Fan
 * 
 */
public class FeatureAttach extends AbstractLocatableObject {
    
    String elementName;
    String featureClass;
    String nameSpace;    

    /**
     * Default empty constructor
     */
    public FeatureAttach() {

    }
    
    public FeatureAttach(String ns, String elName, String fClass){
        setNameSpace(ns);
        setElementName(elName);
        setFeatureClass(fClass);
    }
    
    public FeatureAttach( String ns, String elName, Class type ){
        this( ns, elName, type.getName() );
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
     * @return Returns the featureClass.
     */
    public String getFeatureClass() {
        return featureClass;
    }
    /**
     * @param featureClass The featureClass to set.
     */
    public void setFeatureClass(String featureClass) 
    {
        try{
            this.featureClass = featureClass;
            Class.forName(featureClass);
        }catch(ClassNotFoundException ex){
            throw new RuntimeException("Feature class not found", ex);
        }
    }
    /**
     * @return Returns the nameSpace.
     */
    public String getNameSpace() {
        return nameSpace;
    }
    /**
     * @param nameSpace The nameSpace to set.
     */
    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }
    
    public QualifiedName getElementIdentifier(){
        return new QualifiedName(nameSpace,elementName);
    }
    
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {        
        return "FeatureAttach["+nameSpace+":"+elementName+" -> " + featureClass + "]";
    }
}
