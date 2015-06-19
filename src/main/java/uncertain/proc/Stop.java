/*
 * Created on 2010-9-16 обнГ04:01:12
 * $Id$
 */
package uncertain.proc;

public class Stop extends AbstractEntry {

    public void run(ProcedureRunner runner) throws Exception {
       runner.stop();
    }

}
