/*
 * Created on 2006-11-15
 */
package uncertain.proc;

import java.util.HashMap;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;

/**
 * Implements <assert> tag
 * Like JDK1.4 assert keyword, evaluate a expression and throw AssertionError if fails
 * <assert field="<access path>" operator="operator to compare" value="value to compare, supports tag" message="customized message in thrown error" />  
 * @author Zhou Fan
 *
 */
public class Assert extends AbstractEntry {
    
//    public static final String OP_NULL = "null";
//    public static final String OP_NOTNULL = "not null";
//    public static final String OP_EQUAL = "==";
//    public static final String OP_NOTEQUAL = "!=";   
//    public static final String OP_GREAT_THAN = ">";
//    public static final String OP_LESS_THAN = "<";    
//    public static final String OP_GOE = ">=";
//    public static final String OP_LOE = "<=";    

//    public static final String[] OPERATORS = {OP_NULL,OP_NOTNULL,OP_EQUAL,OP_NOTEQUAL,OP_GREAT_THAN,OP_LESS_THAN,OP_GOE,OP_LOE};
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
    
    public String   Field;
    public String   Operator;
    public String   Value;
    public String   Message;
    
    public int operatorID(String op){
        Integer id = (Integer)_operator_map.get(op.toLowerCase());
        return id==null?-1:id.intValue();
    }
    
    public void run(ProcedureRunner runner) {
        if( Field == null ) throw new ConfigurationError("assert: 'field' property must be set");
        if( Operator == null ) throw new ConfigurationError("assert: 'operator' property must be set");
        int opid = operatorID(Operator);
        if(opid<0) throw new ConfigurationError("assert: unknown operator "+Operator);
        CompositeMap    context = runner.getContext();
        Object test_field = context.getObject(Field);
        
        // First, test for expression without operant
        switch(opid){
                
            case NULL:
                if(test_field!=null) 
                    throw new AssertionError(Message==null?"Field '"+Field+"' is expected to be null":Message);
                break;
            case NOTNULL:                
                if(test_field==null) 
                    throw new AssertionError(Message==null?"Field '"+Field+"' is expected to be not null":Message);
                break;
            // Then test for expression that must specify 'value'
            case EQUAL:
            case NOTEQUAL:
                if(Value==null) throw new ConfigurationError("assert: 'value' property must be set");
                Value = TextParser.parse(Value, context);                
                if(opid==EQUAL){
                    if(!Value.equals(test_field))
                        throw new AssertionError(Message==null?"Field '"+Field+"' is expected to be "+Value+", but actual value is "+test_field:Message);
                }
                else{
                    if(Value.equals(test_field))
                        throw new AssertionError(Message==null?"Field '"+Field+"' is not expected to be "+Value:Message);
                }
                break;
             // Here we got numeric expressions                
            default:
                if(Value==null) throw new ConfigurationError("assert: 'value' property must be set");
                String parsed_value = TextParser.parse(Value, context);             
                Double d_value = null;
                if(test_field==null) throw new AssertionError("Field '"+Field+"' is null, can't be compared as number");
                try{
                    d_value = new Double(Double.parseDouble(parsed_value));
                }catch(NumberFormatException ex){
                    throw new ConfigurationError("specified value '"+parsed_value+"' can't be parsed as number");
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
                    throw new AssertionError("Field '"+Field+"' is not a number");
                // perform comparation
                switch(opid){
                    case GREAT_THAN:
                        if(d_test_field.doubleValue()<=d_value.doubleValue())
                            throw new AssertionError(Message==null?"Field '"+Field+"' is expected to be >"+Value+", but actual value is "+test_field:Message);
                        break;
                    case LESS_THAN:
                        if(d_test_field.doubleValue()>=d_value.doubleValue())
                            throw new AssertionError(Message==null?"Field '"+Field+"' is expected to be <"+Value+", but actual value is "+test_field:Message);
                        break;
                    case GOE:
                        if(d_test_field.doubleValue()<d_value.doubleValue())
                            throw new AssertionError(Message==null?"Field '"+Field+"' is expected to be >="+Value+", but actual value is "+test_field:Message);
                        break;
                    case LOE:
                        if(d_test_field.doubleValue()>d_value.doubleValue())
                            throw new AssertionError(Message==null?"Field '"+Field+"' is expected to be <="+Value+", but actual value is "+test_field:Message);
                        break;
                 }  // end inner switch
             }  // end outter switch

        } // end function
    

}
