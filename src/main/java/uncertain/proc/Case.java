package uncertain.proc;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.OCManager;

/**
 * Implements &lt;case&gt; tag
 * @author Zhou Fan
 * 
 */
public class Case extends Procedure {
    
    String	value;    

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getEvaluatedValue(CompositeMap context){
        if(value==null) 
            return null;
        else
            return TextParser.parse(value,context);
    }
    
    /**
     * Default constructor
     */
    public Case() {
        super();
    }

    /**
     * @param engine
     */
    public Case(OCManager om) {
        super(om);
    }

}
