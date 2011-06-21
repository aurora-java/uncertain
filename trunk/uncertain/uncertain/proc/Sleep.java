/*
 * Created on 2008-11-5
 */
package uncertain.proc;

import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

/** Sleep specified time ( in milliseconds ) so simulate long response time. for debug. 
 * <pre><sleep time="1000" /></pre>
 */
public class Sleep extends AbstractEntry {
    
    int  time = 0;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void run(ProcedureRunner runner) throws Exception {
        Thread.sleep(time);
    }

}
