/*
 * Created on 2009-6-23
 */
package uncertain.schema;

import uncertain.composite.DynamicObject;

public class Array extends Element {
    
    /*
    String          name;
    String          type;
    String          minOccur = "0";
    String          maxOccur = SchemaConstant.OCCUR_UNBOUNDED;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMinOccur() {
        return minOccur;
    }

    public void setMinOccur(String minOccur) {
        this.minOccur = minOccur;
    }

    public String getMaxOccur() {
        return maxOccur;
    }

    public void setMaxOccur(String maxOccur) {
        this.maxOccur = maxOccur;
    }
    
    public void doAssemble(){
        
    }    
    */
    String indexField;
    public boolean isArray(){
        return true;
    }
	public String getIndexField() {
		return indexField;
	}
	public void setIndexField(String idField) {
		this.indexField = idField;
	}

}
