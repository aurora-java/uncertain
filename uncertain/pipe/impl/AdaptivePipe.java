/*
 * Created on 2014年12月23日 下午2:47:59
 * $Id$
 */
package uncertain.pipe.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import uncertain.core.ILifeCycle;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.pipe.base.IEndPoint;
import uncertain.pipe.base.IFilter;
import uncertain.pipe.base.IFlowable;
import uncertain.pipe.base.IPipe;
import uncertain.pipe.base.IProcessor;
import uncertain.pipe.base.Returnable;

public class AdaptivePipe extends AbstractLocatableObject implements IPipe, AdaptivePipeMBean {

    public static final int INIT_THREAD_ARRAY_SIZE = 50;

    String filters;
    List<IFilter> filterList;
    IProcessor processor;
    IFlowable output;
    String id;
    List<WorkerThread> workerThreadList;
    BlockingQueue<Object> taskQueue;
    ThreadGroup workerThreadGroup;
    AdaptivePipeWatcherThread watcherThread;

    /**
     * Max task limit. if task count in queue exceed this value, add() will fail
     */
    int maxTaskCount = 6000;

    /**
     * auto create new worker thread if remaining tasks in queue grow more than
     * this value
     */
    int expandCount = 995;

    /** auto release thread if tasks in queue is less than this value */
    int releaseCount = 5;

    /** won't create more threads than this */
    int maxThreads = 10;

    /** minimal threads active */
    int minThreads = 1;

    /** auto release working threads after idleTime (in ms) */
    int idleTime = 1000;

    String processorClass;

    String outputId;

    /** If this pipe is overheat, which means task queue grow too long */
    boolean overheat = false;

    boolean running = false;
    boolean shutdownInProcess = false;

    public AdaptivePipe() {
        taskQueue = new LinkedBlockingQueue<Object>();
        filterList = new LinkedList<IFilter>();
        workerThreadList = new ArrayList<WorkerThread>(INIT_THREAD_ARRAY_SIZE);
    }

    public AdaptivePipe(String id) {
        this();
        this.id = id;
    }

    public AdaptivePipe(String id, int min_threads) {
        this();
        this.id = id;
        this.minThreads = min_threads;
    }

    public void addFilter(IFilter filter) {
        filterList.add(filter);
    }

    public boolean removeFilter(IFilter filter) {
        return filterList.remove(filter);
    }

    @Override
    public void addData(Object data) {
        if (data == null)
            throw new NullPointerException();
        if (overheat)
            throw new IllegalStateException("queue " + getId() + " has reached max size");
        try {
            taskQueue.put(data);
        } catch (InterruptedException ex) {

        }
    }

    @Override
    public void addData(Object data, IPipe return_pipe)

    {
        Returnable r = new Returnable(data, return_pipe);
        addData(r);
    }

    public Object take() throws InterruptedException {
        if (!running)
            return null;
        return taskQueue.take();
    }

    public IProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(IProcessor processor) {
        this.processor = processor;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String getThreadName(int n) {
        StringBuffer name = new StringBuffer();
        name.append(id);
        name.append(".Worker.").append(n);
        return name.toString();
    }

    public int size() {
        return taskQueue.size();
    }

    protected void createWorkerThread(int id) {
        WorkerThread thread = new WorkerThread(this.workerThreadGroup, getThreadName(id), this);
        thread.start();
        onThreadStart(thread);
        workerThreadList.add(thread);
    }

    protected boolean expand() {
        if (this.getThreadCount() >= this.maxThreads)
            return false;
        createWorkerThread(workerThreadList.size());
        return true;
    }

    protected boolean release() {
        if (this.getThreadCount() <= this.minThreads)
            return false;
        int id = workerThreadList.size() - 1;
        WorkerThread thread = workerThreadList.get(id);
        thread.interrupt();
        workerThreadList.remove(id);
        return true;
    }

    public void start() {
        if (running)
            throw new IllegalStateException("Already started");
        running = true;
        shutdownInProcess = false;
        if (processor != null) {
            if (processor instanceof ILifeCycle)
                ((ILifeCycle) processor).startup();
            workerThreadGroup = new ThreadGroup(id + "Workers");
            for (int i = 0; i < minThreads; i++) {
                createWorkerThread(i);
            }
            watcherThread = new AdaptivePipeWatcherThread(this);
            watcherThread.start();
        }

    }

    public void shutdown() {
        shutdownInProcess = true;
        watcherThread.interrupt();
        workerThreadGroup.interrupt();
        Thread[] thread_array = new Thread[workerThreadGroup.activeCount()];
        workerThreadGroup.enumerate(thread_array);
        for (Thread thread : thread_array)
            onThreadStop(thread);
        if (processor instanceof ILifeCycle)
            ((ILifeCycle) processor).shutdown();
        clearUp();
    }

    private void clearUp() {
        workerThreadList.clear();
        taskQueue.clear();
    }

    /**
     * For subclass to process thread specific settings
     * 
     * @param thread
     */
    public void onThreadStart(Thread thread) {

    }

    public void onThreadStop(Thread thread) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see pipe.simple.SimplePipeMBean#getThreadCount()
     */
    public int getThreadCount() {
        return workerThreadList.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pipe.simple.SimplePipeMBean#getQueueSize()
     */
    public int getQueueSize() {
        return taskQueue.size();
    }

    public int getExpandCount() {
        return expandCount;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMinThreads() {
        return minThreads;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setExpandCount(int expandCount) {
        this.expandCount = expandCount;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setMinThreads(int minThreads) {
        this.minThreads = minThreads;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public int getMaxTaskCount() {
        return maxTaskCount;
    }

    public boolean getOverheat() {
        return overheat;
    }

    public void setMaxTaskCount(int maxTaskCount) {
        this.maxTaskCount = maxTaskCount;
    }

    public int getReleaseCount() {
        return releaseCount;
    }

    public void setReleaseCount(int releaseCount) {
        this.releaseCount = releaseCount;
    }

    @Override
    public void setOutput(IFlowable output) {
        this.output = output;
    }

    @Override
    public IFlowable getOutput() {
        return output;
    }

    public String getProcessorClass() {
        return processorClass;
    }

    public void setProcessorClass(String processorClass) {
        this.processorClass = processorClass;
    }

    @Override
    public String getOutputId() {
        return outputId;
    }

    @Override
    public void setOutputId(String outputId) {
        this.outputId = outputId;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public List<WorkerThread> getThreadList() {
        return this.workerThreadList;
    }

}
