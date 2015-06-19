/**
 * Implements &lt;loop&gt; tag
 * &lt;loop&gt; Source="/path"
 */
package uncertain.proc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.ConfigurationFileException;
import uncertain.ocm.OCManager;

public class Loop extends Procedure {
	
	public static final String KEY_PARENT_RECORD = "parent_record";
    
    String  source;
    boolean nullable = true;

    public Loop(){
        super();
    }
    
    public Loop(OCManager om){
        super(om);
    }    
    /**
     * @return Returns source collection path
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source path to a CompositeMap access path
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return Returns the nullable.
     */
    public boolean getNullable() {
        return nullable;
    }

    /**
     * @param nullable The nullable to set.
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
    
    /**
     * @see uncertain.proc.Procedure#run(uncertain.proc.ProcedureRunner)
     */
    public void run(ProcedureRunner runner) throws Exception {
        //if(source==null) throw new ConfigurationError("loop: 'source' property must be set");
        CompositeMap context = runner.getContext();
        Object obj = null;
        if(source!=null)
            obj=context.getObject(uncertain.composite.TextParser.parse(source, context));
        else
            obj=context.getChilds();
        if(obj==null){
            if(nullable)
                return;
            else throw 
                BuiltinExceptionFactory.createDataFromXPathIsNull(this,source);
        }
        Iterator source_it = null;
        if(obj instanceof CompositeMap){
            source_it = ((CompositeMap)obj).getChildIterator();
        }
        else if (obj instanceof Collection){
            source_it = ((Collection)obj).iterator();
        }
        else{
            String type = obj==null?"(null)":obj.getClass().getName();
            ConfigurationFileException ex = new ConfigurationFileException("uncertain.proc.loop_source_not_collection", new Object[]{source,type}, this  );
            throw ex;
        }
        if(source_it!=null){
            CompositeMap old_context = runner.getContext();
            while(source_it.hasNext()){
                CompositeMap item = (CompositeMap)source_it.next();
                if("record".equals(context.getName())){
                	item.put(KEY_PARENT_RECORD, context);
                }
                runner.setContext(item);
                super.run(runner);
                Throwable thr = runner.getException(); 
                if(thr!=null) break;
            }
            runner.setContext(old_context);
        }
        //super.run(runner);
    }    
    

}
