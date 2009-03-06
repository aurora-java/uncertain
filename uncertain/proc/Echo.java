/*
 * Created on 2006-6-20
 */
package uncertain.proc;
import java.util.logging.Logger;

import uncertain.composite.TextParser;

public class Echo extends AbstractEntry {
    
    public String Message;

    public Echo() {
        super();        
    }

    public void run(ProcedureRunner runner) {
        String m = null;
        if(Message==null) m = runner.getContext().toXML();
        else m = TextParser.parse( Message, runner.getContext() );
        if(runner.getUncertainEngine()!=null){
            Logger logger = runner.getUncertainEngine().getLogger();        
            logger.info(m);
        }else{
            System.out.println(m);
        }
            
    }

}
