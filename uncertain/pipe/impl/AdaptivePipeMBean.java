/*
 * Created on 2014年12月27日 上午12:16:58
 * $Id$
 */
package uncertain.pipe.impl;

import uncertain.pipe.base.IFlowable;

public interface AdaptivePipeMBean {

    public int getThreadCount();

    public int getQueueSize();
    
    public int getExpandCount();

    public int getMaxThreads();

    public int getMinThreads();

    public int getIdleTime();

    public void setExpandCount(int expandCount);

    public void setMaxThreads(int maxThreads);

    public void setMinThreads(int minThreads);

    public void setIdleTime(int idleTime);
    
    public int getMaxTaskCount();

    public boolean getOverheat();

    public void setMaxTaskCount(int maxTaskCount);
    
    public int getReleaseCount();

    public void setReleaseCount(int releaseCount);
    
    public IFlowable getOutput();
    
    public String getOutputId();
    
    public String getProcessorClass();
    

}