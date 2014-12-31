/*
 * Created on 2014年12月23日 下午2:22:33
 * $Id$
 */
package uncertain.pipe.impl;

import java.util.Date;

import uncertain.pipe.base.IFilter;
import uncertain.pipe.base.IReturnable;

public class WorkerThread extends Thread {
    
    AdaptivePipe     owner;
    boolean        running = false;

    public WorkerThread(ThreadGroup group, String name, AdaptivePipe owner) {
        super(group, name);
        this.owner = owner;
    }
    
    public void run(){
        while(!interrupted() && owner.running && !owner.shutdownInProcess){
            try{
                Object payload = owner.take();
                if(payload==null) 
                    try{
                        sleep(100);
                    }catch(InterruptedException iex){
                        continue;
                    };
                boolean need_return = payload instanceof IReturnable;
                Object data = need_return? ((IReturnable)payload).getData(): payload;
                if(data==null) continue;
                try{
                    for(IFilter filter:owner.filters){
                        filter.filt(data);
                    }
                    Object result = owner.processor.process(data);
                    if(need_return){
                        ((IReturnable)payload).getReturnPipe().addData(data);
                    }
                    if( owner.getOutput()!=null){
                        owner.getOutput().addData(result);
                    }
                    
                }catch(Throwable thr){
                    thr.printStackTrace();
                }
            }catch(InterruptedException ex){
                break;
            }
        }
        running = false;
    }
    
    public void start(){
        running = true;
        super.start();
    }
    
    


}
