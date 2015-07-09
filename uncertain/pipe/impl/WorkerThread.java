/*
 * Created on 2014年12月23日 下午2:22:33
 * $Id$
 */
package uncertain.pipe.impl;

import uncertain.pipe.base.IEndPoint;
import uncertain.pipe.base.IFilter;
import uncertain.pipe.base.IFlowable;
import uncertain.pipe.base.IReturnable;

public class WorkerThread extends Thread {
    
    AdaptivePipe     owner;
    boolean        running = false;

    public WorkerThread(ThreadGroup group, String name, AdaptivePipe owner) {
        super(group, name);
        this.owner = owner;
    }
    
    /*
    void executeDispatch( IDispatcher dispatch, Object result ){
        IDispatchData dd = null;
        if(result instanceof IDispatchData)
            dd = (IDispatchData)result;
        else
            dd = new DispatchData(result);
        dispatch.doDispatch(dd);
        
        
    }
    */
    
    public void run(){
        while(!interrupted() && owner.running && !owner.shutdownInProcess){
            if(owner.processor==null)
                return;
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
                    for(IFilter filter:owner.filterList){
                        filter.filt(data);
                    }
                    Object result = owner.processor.process(data);
                    if(result!=null && need_return){
                        IEndPoint ep = ((IReturnable)payload).getReturnPipe();
                        ep.addData(result);
                    }
                    if(result!=null && owner.getOutput()!=null){
                        IFlowable ep = owner.getOutput();
                        PipeExecutor.executeNext(ep,result);
                        /*
                        if( ep !=null && ep instanceof IDispatcher ){
                            Set<IDispatcher> executed_set = new HashSet<IDispatcher>();
                            List<IDispatchData> rs_list = null;
                            while( ep instanceof IDispatcher ){
                                executed_set.add((IDispatcher)ep);
                                if(rs_list==null)
                                    rs_list = executeDispatch((IDispatcher)ep, result);
                                else{
                                    List<IDispatchData> return_list = new LinkedList<IDispatchData>();
                                    for(IDispatchData d:rs_list){
                                        return_list.addAll(executeDispatch((IDispatcher)ep, d));
                                    }
                                    rs_list = return_list;
                                }
                                if(rs_list.size()==0)
                                    break;
                                ep = ep.getOutput();
                                if(executed_set.contains(ep))
                                    throw new CircularReferenceException(toString(executed_set));
                            }
                        }
                        if(ep!=null && ep instanceof IEndPoint )
                            ((IEndPoint)ep).addData(result);
                            */
                    }
                }catch(Throwable thr){
                    /** @TODO log exception here */ 
                    thr.printStackTrace(System.err);
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
