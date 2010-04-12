/*
 * Created on 2005-10-9
 */
package uncertain.proc;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;

/**
 * implements <code><set></code> 
 * <code>
 * <set Field="/path/to/@field" [SourceField="/other/path/@field" | Value="string contains ${@tag}" ] />
 * </code>
 * @author Zhou Fan
 * 
 */
public class Set extends AbstractEntry {
    
    String field;
    String sourceField;
    String value;  
    

    /**
     * @return Returns the field.
     */
    public String getField() {
        return field;
    }
    /**
     * @param field The field to set.
     */
    public void setField(String field) {
        this.field = field;
    }
    /**
     * @return Returns the textValue.
     */
    public String getValue() {
        return value;
    }
    /**
     * @param textValue The textValue to set.
     */
    public void setValue(String textValue) {
        this.value = textValue;
    }
    /**
     * @return Returns the value.
     */
    public String getSourceField() {
        return sourceField;
    }
    /**
     * @param value The value to set.
     */
    public void setSourceField(String value) {
        this.sourceField = value;
    }
    
    public void run(ProcedureRunner runner) {
        if(field==null) 
            throw new ConfigurationError("'Field' attribute must be set for <set> element");
        if(sourceField==null && value==null)
            throw new ConfigurationError("either 'SourceField' or 'Value' attribute must be set for <set> element");
        CompositeMap context = runner.getContext();
        if(sourceField!=null)
            context.putObject(field, context.getObject(sourceField), true);
        else
            context.putObject(field, TextParser.parse(value,context), true);
    }

}
