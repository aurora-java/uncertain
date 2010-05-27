/*
 * Created on 2005-3-23
 */
package uncertain.proc;

import java.util.LinkedList;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.logging.ILogger;
import uncertain.util.StringSplitter;

/**
 * Implements &lt;action&gt; tag
 */
public class Action extends AbstractEntry {

    String[] input_fields;
    String   input;
    String[] output_fields;
    String   output;
    
    boolean  isNameDynamic = false;
    

    public void setName(String n) {
        super.setName(n);
        if(n.indexOf("${")>=0)
            isNameDynamic = true;
        else
            isNameDynamic = false;
    }    
    
    private String[] split(String input){
        String[] array = StringSplitter.splitToArray(input, ',', false);
        for(int i=0; i<array.length; i++)
            array[i] = array[i].trim();
        return array;
    }
    
    public void setInput(String input){
        this.input = input;
        input_fields = split(input);
    }
    
    public String getInput(){
        return input;
    }
    
    public void setOutput(String output){
        this.output = output;
        output_fields = split(output);
    }
    
    public String getOutput(){
        return output;
    }
    
    /*
    public static final String RETURN_FLAG = "return";
   
    static final int INPUT_FIELD_ID = 0;
    static final int RETURN_FIELD_ID = 1;
    static final int MIN_RETURN_FIELD_DECL_LENGTH = RETURN_FLAG.length()+2;
    
    String          parameters;
    LinkedList[]    action_fields;
    
   
    public void setParameters(String param) {
        if(param==null) return;
        this.parameters = param.trim();
        action_fields = new LinkedList[]{new LinkedList(), new LinkedList()};
        String[] params = StringSplitter.splitToArray(parameters, ',', true);
        for(int i=0; i<params.length; i++){
            String field_name=null;
            int id=INPUT_FIELD_ID;            
            String param_decl = params[i].trim();
            String[] sa = StringSplitter.splitToArray(param_decl, ' ', false);
            if(sa.length==1)
                field_name = sa[0];
            else if(sa.length==2){
                if(RETURN_FLAG.equals(sa[1])){
                    id = RETURN_FIELD_ID;
                    field_name = sa[0];
                }
                else 
                    throw new ConfigurationError("Illegal action parameter: "+param_decl);
            }
            else
                throw new ConfigurationError("Illegal action parameter: "+param_decl);            
            action_fields[id].add(field_name);
        }
    }
    
    public List getInputFields(){
        if(action_fields==null) return null;
        return action_fields[INPUT_FIELD_ID];
    }
    
    public List getReturnFields(){
        if(action_fields==null) return null;
        return action_fields[RETURN_FIELD_ID];       
    }
    
    public String getParameters(){
        return parameters;
    }
    */
    
    void getOutputFields(
            ProcedureRunner runner, 
            Procedure proc 
            )
    throws Exception
    {
        if(output_fields==null) return;
        LinkedList lst = new LinkedList();
        for(int i=0; i<output_fields.length; i++){
            String name = output_fields[i];
            Field field = proc.getField(name);
            if(field==null) throw new ConfigurationError("field "+name+" is not defined in root procedure");
            lst.add(field);
        }
        runner.transferContextFields(lst, false);        
    }

    
    Object[] getFieldValues(Procedure proc, CompositeMap context){
        Object[] fields = null;
        if(input_fields!=null){
            fields = new Object[input_fields.length];
            for(int i=0; i<input_fields.length; i++){
                Field fld = proc.getField(input_fields[i]);
                if(fld!=null)
                    fields[i] = context.getObject(fld.getPath());
                else{ 
                    throw new IllegalArgumentException("Field '"+input_fields[i]+"' is not defined in procedure");
                }
            }
        }
        return fields;
    }
    
    public Procedure getParentProcedure(){
        IEntry owner = this.getOwner();
        while(owner!=null){
            if(owner instanceof Procedure)
                return (Procedure)owner;
            owner = owner.getOwner();
        }
        return null;
    }

    
    public void run(ProcedureRunner runner) throws Exception {
        // ================== to be enhanced =======================
        Procedure proc = getParentProcedure();
        if(proc==null)
            proc = (Procedure)runner.getProcedure().getRootOwner();
        // ================== end ==================================
        ILogger logger = runner.getLogger();
        CompositeMap context = runner.getContext();
        Object[] args = getFieldValues(proc, context);
        
        String event_name = isNameDynamic? TextParser.parse(getName(), context): getName();
        logger.log(Level.CONFIG, "[action] "+event_name);
        
        runner.fireEvent(event_name, args);
        // Fetch return fields from participant
        getOutputFields(runner, proc);       
    }

     
}