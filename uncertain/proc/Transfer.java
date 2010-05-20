/*
 * Created on 2010-5-19 ÏÂÎç02:12:07
 * $Id$
 */
package uncertain.proc;

import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;

/**
 * Call another procedure <call procedure="some_procedure_name" />
 */
public class Transfer extends AbstractEntry {

    String mProcedure;
    IProcedureManager mProcedureManager;

    /**
     * @param procedureManager
     */
    public Transfer(IProcedureManager procedureManager) {
        super();
        mProcedureManager = procedureManager;
    }

    public void run(ProcedureRunner runner) throws Exception {
        if(mProcedure==null)
            throw new ConfigurationError("<call>: must set 'procedure' property");
        String proc_name = TextParser.parse(mProcedure, runner.getContext());
        Procedure proc = mProcedureManager.loadProcedure(proc_name);
        if(proc==null)
            throw new IllegalArgumentException("Can't load procedure "+proc_name);
        runner.stop();
        runner.setProcedure(proc);
        runner.run();
    }

    public String getProcedure() {
        return mProcedure;
    }

    public void setProcedure(String procedure) {
        this.mProcedure = procedure;
    }

}
