/*
 * Created on 2010-9-16 обнГ04:01:12
 * $Id: Stop.java 1407 2010-09-16 08:01:27Z seacat.zhou@gmail.com $
 */
package uncertain.proc;

public class Stop extends AbstractEntry {

    public void run(ProcedureRunner runner) throws Exception {
       runner.stop();
    }

}
