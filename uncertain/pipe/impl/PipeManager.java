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
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.mbean.MBeanRegister;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectCreator;
import uncertain.pipe.base.IDispatcher;
import uncertain.pipe.base.IEndPoint;
import uncertain.pipe.base.IFilter;
import uncertain.pipe.base.IFlowable;
import uncertain.pipe.base.IPipe;
import uncertain.pipe.base.IPipeManager;
import uncertain.pipe.base.IProcessor;
import uncertain.util.resource.ILocatable;

public class PipeManager extends AbstractLocatableObject implements IPipeManager, ILifeCycle {

    Map<String, IFlowable> elementMap;
    // Map<String, IDispatcher> dispatcherMap;
    IObjectCreator objectCreator;
    UncertainEngine engine;
    ILogger logger;

    public PipeManager() {
        elementMap = new HashMap<String, IFlowable>();
        // dispatcherMap = new HashMap<String, IDispatcher>();
    }

    public PipeManager(UncertainEngine uengine) {
        this();
        this.engine = uengine;
        this.objectCreator = uengine.getObjectCreator();
        engine.getObjectRegistry().registerInstance(IPipeManager.class, this);
        ILoggerProvider provider = (ILoggerProvider) engine.getObjectRegistry()
                .getInstanceOfType(ILoggerProvider.class);
        logger = provider.getLogger("uncertain.pipe");
    }

    public IPipe getPipe(String id) {
        return (IPipe) elementMap.get(id);
    }

    @Override
    public IFlowable getElement(String id) {
        return elementMap.get(id);
    };

    public IPipe createPipe(String id) {
        IPipe pipe = getPipe(id);
        if (pipe == null) {
            pipe = new AdaptivePipe(id);
            addPipe(pipe);
            return pipe;
        } else
            throw new GeneralException("uncertain.pipe.id_exists", new Object[] { id }, (Throwable) null);
    }

    public void addChild(IFlowable child) {
        String id = child.getId();
        ILocatable source = child instanceof ILocatable ? (ILocatable) child : this;
        if (elementMap.containsKey(id))
            throw new GeneralException("uncertain.pipe.id_exists", new Object[] { id }, source);
        elementMap.put(id, child);
    }

    public void addPipe(IPipe pipe) {
        addChild(pipe);
    }

    public void addDispatcher(IDispatcher disp) {
        addChild(disp);
    }

    public void addPipes(Collection<IPipe> pipes) {
        for (IPipe pipe : pipes) {
            addPipe(pipe);
        }
    }

    public void addDispatchers(Collection<IDispatcher> disps) {
        for (IDispatcher disp : disps) {
            addDispatcher(disp);
        }
    }

    /*
     * public void startAll() { for (IPipe pipe : pipeMap.values()) { if
     * (pipe.getProcessor() != null) pipe.start(); logger.info(String.format(
     * "Pipe %s has started",pipe.getId() )); } }
     */

    protected void startPipes() {
        logger.info("Start creating pipes");
        for (IFlowable element : elementMap.values()) {
            // set output object
            if (element.getOutputId() != null) {
                IFlowable output = getElement(element.getOutputId());
                if (output == null)
                    throw new GeneralException("uncertain.pipe.id_not_exist", new Object[] { element.getOutputId() },
                            element instanceof ILocatable ? (ILocatable) element : this);
                else
                    element.setOutput(output);
            }

            if (element instanceof AdaptivePipe) {
                AdaptivePipe apipe = (AdaptivePipe) element;
                // set processor
                if (apipe.getProcessor() == null && apipe.getProcessorClass() != null) {
                    Class cls = null;
                    try {
                        cls = Class.forName(apipe.getProcessorClass());
                    } catch (ClassNotFoundException cex) {
                        throw new GeneralException("uncertain.exception.classnotfoundexception",
                                new Object[] { apipe.getProcessorClass() }, apipe);
                    }
                    try {
                        IProcessor processor = (IProcessor) objectCreator.createInstance(cls);
                        if (processor == null)
                            throw new GeneralException("uncertain.exception.instance_dependency_not_meet",
                                    new Object[] { apipe.getProcessorClass() }, apipe);
                        apipe.setProcessor(processor);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
                // create filter instances
                if (apipe.getFilters() != null) {
                    String cls_array[] = apipe.getFilters().split(",");
                    for (String cls : cls_array) {
                        try {
                            Class filter_type = Class.forName(cls);
                            IFilter filter = (IFilter) objectCreator.createInstance(filter_type);
                            if (filter == null)
                                throw new GeneralException("uncertain.exception.instance_dependency_not_meet",
                                        new Object[] { cls }, apipe);
                            apipe.addFilter(filter);
                        } catch (ClassNotFoundException ex) {
                            throw new GeneralException("uncertain.exception.classnotfoundexception",
                                    new Object[] { cls }, apipe);
                        } catch (Exception ex) {
                            throw new GeneralException("uncertain.exception.instance_creation_exception",
                                    new Object[] { cls }, ex, apipe);
                        }
                    }
                }
            }
            // pipe.start();

            if (element instanceof IPipe) {
                IPipe pipe = (IPipe) element;
                //if (pipe.getProcessor() != null) {
                    pipe.start();
                    logger.info(
                            String.format("Pipe %s has started, processor:%s", element.getId(), pipe.getProcessor()));
                    // System.out.println(pipe.getId() + " started");
                //}
            }

        }

    }

    private String convertId(String id) {
        StringBuffer converted_id = new StringBuffer();
        for (int i = 0; i < id.length(); i++) {
            char ch = id.charAt(i);
            if (Character.isJavaIdentifierPart(ch))
                converted_id.append(ch);
            else
                converted_id.append('_');
        }
        return converted_id.toString();

    }

    @Override
    public boolean startup() {
        for (IFlowable element : elementMap.values()) {
            if (engine != null && element instanceof IPipe) {
                IPipe pipe = (IPipe)element;
                String id = convertId(pipe.getId());
                String name = engine.getMBeanName("pipe", "name=" + id);
                MBeanRegister.resiterMBean(name, pipe);
            }
        }
        return true;
    }

    public void postInitialize() {
        startPipes();
    }

    @Override
    public void shutdown() {
        for (IFlowable element  : elementMap.values()) {
            if(element instanceof IPipe){
                IPipe pipe = (IPipe)element;
                pipe.shutdown();
            }
        }
    }

}
