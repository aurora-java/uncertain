/*
 * Created on 2010-5-19 ����02:12:07
 * $Id: Invoke.java 3116 2011-07-13 15:31:59Z seacat.zhou@gmail.com $
 */
package uncertain.proc;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.exception.BuiltinExceptionFactory;

/**
 * invoke another procedure <invoke procedure="some_procedure_name" />
 */
public class Invoke extends AbstractEntry {

    String mProcedure;
    boolean newContext = false;
    IProcedureManager mProcedureManager;

    /**
     * @param procedureManager
     */
    public Invoke(IProcedureManager procedureManager) {
        super();
        mProcedureManager = procedureManager;
    }

    public void run(ProcedureRunner runner) throws Exception {
        if(mProcedure==null)
            //throw new ConfigurationError("<invoke>: must set 'procedure' property");
            throw BuiltinExceptionFactory.createAttributeMissing(this, "procedure");
        String proc_name = TextParser.parse(mProcedure, runner.getContext());
        Procedure proc = mProcedureManager.loadProcedure(proc_name);
        if(proc==null)
            //throw new IllegalArgumentException("Can't load procedure "+proc_name);
            throw BuiltinExceptionFactory.createResourceLoadException(this, proc_name, null);
        if(!newContext){
            runner.call(proc);
            runner.checkAndThrow();
        }else{
            CompositeMap context = new CompositeMap("context");
            ProcedureRunner sub_runner = runner.spawn(proc);
            sub_runner.setContext(context);
            sub_runner.run();
            context.clear();
            sub_runner.checkAndThrow();
        }
    }

    public String getProcedure() {
        return mProcedure;
    }

    public void setProcedure(String procedure) {
        this.mProcedure = procedure;
    }
    
    public boolean getNewContext() {
        return newContext;
    }

    public void setNewContext(boolean newContext) {
        this.newContext = newContext;
    }

    

}
