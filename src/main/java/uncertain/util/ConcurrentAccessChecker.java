/*
 * Created on 2011-6-9 ÏÂÎç11:30:54
 * $Id$
 */
package uncertain.util;

public class ConcurrentAccessChecker {
    
    Thread                  owner_thread;
    StackTraceElement[]     created_stack_trace;
    
    public ConcurrentAccessChecker(){
        owner_thread = Thread.currentThread();
        created_stack_trace = Thread.currentThread().getStackTrace();
    }
    
    public void checkThread(){
        Thread current = Thread.currentThread();
        if(owner_thread!=current){
            StringBuffer msg = new StringBuffer("Concurrent use\nOrigin thread:"+owner_thread+" stack trace:\n");
            msg.append(StackTraceUtil.toString(created_stack_trace));
            msg.append("current thread:"+current);
            throw new RuntimeException(msg.toString());
        }
        
    }
    

}
