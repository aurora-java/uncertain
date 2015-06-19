/*
 * Created on 2014年12月24日 下午1:59:15
 * $Id$
 */
package uncertain.pipe.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uncertain.core.ILifeCycle;
import uncertain.core.UncertainEngine;
import uncertain.exception.GeneralException;
import uncertain.mbean.MBeanRegister;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectCreator;
import uncertain.pipe.base.IPipe;
import uncertain.pipe.base.IPipeManager;
import uncertain.util.resource.ILocatable;

public class PipeManager extends AbstractLocatableObject implements IPipeManager, ILifeCycle {
    
    Map<String,IPipe>   pipeMap;
    IObjectCreator      objectCreator;
    UncertainEngine     engine;

    public PipeManager() {
        pipeMap = new HashMap<String,IPipe>();
    }
    
    public PipeManager(UncertainEngine uengine){
        this();
        this.engine = uengine;
        this.objectCreator = uengine.getObjectCreator();
    }
    
    public IPipe getPipe(String id){
        return pipeMap.get(id);
    }
    
    public IPipe createPipe(String id){
        IPipe pipe = getPipe(id);
        if(pipe==null){
            pipe = new AdaptivePipe(id);
            addPipe(pipe);
            return pipe;
        }else
            throw new GeneralException("uncertain.pipe.id_exists", new Object[]{id}, (Throwable)null);
    }
    
    public void addPipe(IPipe pipe){
        String id = pipe.getId();
        ILocatable source = pipe instanceof ILocatable? (ILocatable)pipe:this;
        if(pipeMap.containsKey(id))
            throw new GeneralException("uncertain.pipe.id_exists", new Object[]{id}, source );
        pipeMap.put(pipe.getId(), pipe);        
    }
    
    public void addPipes(Collection<IPipe> pipes ){
        for(IPipe pipe:pipes){
            addPipe(pipe);
        }
    }
/*    
    public IProcessor createEndPoint(String cls_name)
    
    {
        
    }
*/
    public void startAll(){
        for(IPipe pipe:pipeMap.values()){
            if(pipe.getProcessor()!=null)
                pipe.start();
        }
    }

    @Override
    public boolean startup() {
        for(IPipe pipe:pipeMap.values()){
            if(engine!=null){
                String name = engine.getMBeanName("pipe", "name="+pipe.getId());
                MBeanRegister.resiterMBean(name, pipe);
            }
            if(pipe.getProcessor()!=null)
                pipe.start();
        }
        return true;
    }
    
    @Override
    public void shutdown() {
        for(IPipe pipe:pipeMap.values()){
            pipe.shutdown();
        }
        
    }
    

}
