/*
 * Created on 2005-10-9
 */
package uncertain.proc;

import java.util.Iterator;
import java.util.LinkedList;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;

/**
 * implements <code><switch></code> tag
 * <code>
 * <switch Test="/path/to/@field">
 * 	    <case Value="test ${/other/path/@value}">
 *            ...
 * 		</case>
 *      <case Value="null">
 * 			  ...
 * 		</case>
 * 		<case Value="*">
 *            ...
 * 		</case>
 * 		<case>
 *            ...
 * 		</case>
 * </switch>
 * </code>
 * @author Zhou Fan
 * 
 */
public class Switch extends AbstractEntry {
    
    String			test;
    LinkedList		caseList = new LinkedList();
    
    public void addCase(Case c){
        caseList.add(c);
    }

    /* (non-Javadoc)
     * @see uncertain.proc.IEntry#run(uncertain.proc.ProcedureRunner)
     */
    public void run(ProcedureRunner runner) throws Exception {
        if(test==null) throw new ConfigurationError("Test Attribute not set for <switch> element");
        CompositeMap context = runner.getContext();
		Object obj = context.getObject(test);
//		System.err.println(caseList);
		Iterator it = caseList.iterator();
		Case caseToRun = null;
		while( it.hasNext()){
			Case theCase = (Case)it.next();
			String value = theCase.getEvaluatedValue(context);
			// the default case
			if( value == null) {
				caseToRun = theCase;
				break;
			}
			else{ 
			    // pass to default case
				if( obj == null){ 
				    if("null".equals(value)){
				        caseToRun = theCase;
				        break;
				    }
				    else
				        continue;
				}
				// if requires not null 
				if( "*".equals(value) )
					if( obj != null ){
						caseToRun = theCase;
						break;
					}				
				// matches case value
				if( value.equals(obj.toString())){
					caseToRun = theCase;
					break;				
				}
			}
			
		}
		
		if(caseToRun != null)
		    caseToRun.run(runner);
    }

    /**
     * @return Returns the test.
     */
    public String getTest() {
        return test;
    }
    /**
     * @param test The test to set.
     */
    public void setTest(String test) {
        this.test = test;
    }
}
