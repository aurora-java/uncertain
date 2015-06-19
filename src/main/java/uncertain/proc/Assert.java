/*
 * Created on 2006-11-15
 */
package uncertain.proc;

import java.util.HashMap;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.ConfigurationFileException;

/**
 * Implements <assert> tag
 * Like JDK1.4 assert keyword, evaluate a expression and throw AssertionError if fails
 * <assert field="<access path>" operator="operator to compare" value="value to compare, supports tag" message="customized message in thrown error" />  
 * @author Zhou Fan
 *
 */
public class Assert extends AbstractEntry {
    
public static final String UNCERTAIN_PROC_ASSERT_UNKNOWN_OPERATOR = "uncertain.proc.assert_unknown_operator";
    public static final int NULL = 0;
    public static final int NOTNULL = 1;
    public static final int EQUAL = 2;
    public static final int NOTEQUAL = 3;   
    public static final int GREAT_THAN = 4;
    public static final int LESS_THAN = 5;    
    public static final int GOE = 6;
    public static final int LOE = 7;     

    static HashMap _operator_map = new HashMap();
    static {
        _operator_map.put("null", new Integer(NULL));
        _operator_map.put("not null", new Integer(NOTNULL));        
        _operator_map.put("=", new Integer(EQUAL));
        _operator_map.put("==", new Integer(EQUAL));        
        _operator_map.put("equal", new Integer(EQUAL));
        _operator_map.put("!=", new Integer(NOTEQUAL));
        _operator_map.put("<>", new Integer(NOTEQUAL));
        _operator_map.put(">", new Integer(GREAT_THAN));
        _operator_map.put("<", new Integer(LESS_THAN));  
        _operator_map.put(">=", new Integer(GOE));
        _operator_map.put("<=", new Integer(LOE));
    }
    
    public String   field;
    public String   operator;
    public String   value;
    public String   message;
    
    public int operatorID(String op){
        Integer id = (Integer)_operator_map.get(op.toLowerCase());
        return id==null?-1:id.intValue();
    }
    
    public void run(ProcedureRunner runner) {
        if( field == null ) 
            //throw new ConfigurationError("assert: 'field' property must be set");
            throw BuiltinExceptionFactory.createAttributeMissing(this, "field");
        if( operator == null ) 
            //throw new ConfigurationError("assert: 'operator' property must be set");
            throw BuiltinExceptionFactory.createAttributeMissing(this, "operator");
        int opid = operatorID(operator);
        if(opid<0) 
            //throw new ConfigurationError("assert: unknown operator "+Operator);
            throw new ConfigurationFileException(UNCERTAIN_PROC_ASSERT_UNKNOWN_OPERATOR, new Object[]{operator}, this);
        CompositeMap    context = runner.getContext();
        Object test_field = context.getObject(field);
        
        // First, test for expression without operant
        switch(opid){
                
            case NULL:
                if(test_field!=null) 
                    throw new AssertionError(message==null?"Field '"+field+"' is expected to be null":message);
                break;
            case NOTNULL:                
                if(test_field==null) 
                    throw new AssertionError(message==null?"Field '"+field+"' is expected to be not null":message);
                break;
            // Then test for expression that must specify 'value'
            case EQUAL:
            case NOTEQUAL:
                if(value==null) throw new ConfigurationError("assert: 'value' property must be set");
                value = TextParser.parse(value, context);                
                if(opid==EQUAL){
                    if(!value.equals(test_field))
                        throw new AssertionError(message==null?"Field '"+field+"' is expected to be "+value+", but actual value is "+test_field:message);
                }
                else{
                    if(value.equals(test_field))
                        throw new AssertionError(message==null?"Field '"+field+"' is not expected to be "+value:message);
                }
                break;
             // Here we got numeric expressions                
            default:
                if(value==null) 
                    //throw new ConfigurationError("assert: 'value' property must be set");
                    throw BuiltinExceptionFactory.createAttributeMissing(this, "value");
                String parsed_value = TextParser.parse(value, context);             
                Double d_value = null;
                if(test_field==null) throw new AssertionError("Field '"+field+"' is null, can't be compared as number");
                try{
                    d_value = new Double(Double.parseDouble(parsed_value));
                }catch(NumberFormatException ex){
                    //throw new ConfigurationError("specified value '"+parsed_value+"' can't be parsed as number");
                    throw BuiltinExceptionFactory.createValueNotNumberException(this, parsed_value);
                }
                Number d_test_field = null;
                // parse field to test into number
                if(test_field instanceof String){
                    d_test_field = new Double(Double.parseDouble((String)test_field));
                }
                else if(test_field instanceof Number){
                    d_test_field = (Number)test_field;
                }
                else
                    throw new AssertionError("Field '"+field+"' is not a number");
                // perform comparation
                switch(opid){
                    case GREAT_THAN:
                        if(d_test_field.doubleValue()<=d_value.doubleValue())
                            throw new AssertionError(message==null?"Field '"+field+"' is expected to be >"+value+", but actual value is "+test_field:message);
                        break;
                    case LESS_THAN:
                        if(d_test_field.doubleValue()>=d_value.doubleValue())
                            throw new AssertionError(message==null?"Field '"+field+"' is expected to be <"+value+", but actual value is "+test_field:message);
                        break;
                    case GOE:
                        if(d_test_field.doubleValue()<d_value.doubleValue())
                            throw new AssertionError(message==null?"Field '"+field+"' is expected to be >="+value+", but actual value is "+test_field:message);
                        break;
                    case LOE:
                        if(d_test_field.doubleValue()>d_value.doubleValue())
                            throw new AssertionError(message==null?"Field '"+field+"' is expected to be <="+value+", but actual value is "+test_field:message);
                        break;
                 }  // end inner switch
             }  // end outter switch

        } // end function
    
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
