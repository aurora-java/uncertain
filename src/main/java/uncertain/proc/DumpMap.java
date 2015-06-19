/*
 * Created on 2006-6-20
 */
package uncertain.proc;
import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;

public class DumpMap extends AbstractEntry {
    
    public String Path;

    public DumpMap() {
        super();        
    }

    public void run(ProcedureRunner runner) {
        String m = null;
        Object obj = Path==null?runner.getContext():runner.getContext().getObject(Path);
        if(obj==null)
            m = "["+Path+"] is null";
        else{
            if(obj instanceof CompositeMap){
                CompositeMap map = (CompositeMap)obj;
                m = map.toXML();
            }else{
                m = "object got from ["+Path+"] is not CompositeMap but "+obj.getClass().getName()+", toString():"+obj.toString();
            }
        }
        m = "<dump-map path=\""+Path+"\"> " + m;
        ILogger logger = runner.getLogger();//LoggingContext.getLogger(runner.getContext());
        if(logger!=null)
            logger.log(m);        
        else{
            System.out.println(m);
        }
            
    }

}
