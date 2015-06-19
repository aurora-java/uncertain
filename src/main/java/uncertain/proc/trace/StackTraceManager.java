/*
 * Created on 2011-9-15 下午01:47:09
 * $Id$
 */
package uncertain.proc.trace;

import uncertain.proc.IEntry;



public class StackTraceManager {
    
    TraceElement        rootNode;
    TraceElement        currentNode;
    
    public StackTraceManager(){
        rootNode = new TraceElement("service");
        currentNode = rootNode;
    }
    
    public StackTraceManager( TraceElement root ){
        rootNode = root;
        currentNode = root;
    }

    public TraceElement getCurrentNode(){
        return currentNode;
    }
    
    public TraceElement getRootNode(){
        return rootNode;
    }
    
    private TraceElement push( TraceElement element ){
        element.setParent(currentNode);
        currentNode.addChild(element);
        currentNode = element;
        return element;
    }
    
    public TraceElement enter( IEntry entry ){
        TraceElement element = new TraceElement(entry);
        return push(element);
    }
    
    public TraceElement enter(String node_name){
        TraceElement element = new TraceElement(node_name);
        return push(element);
    }
    
    public TraceElement enter( TraceElement element ){
        return push(element);
    }
    
    public void fillException( Throwable thr ){
        IWithProcedureStackTrace    wpt = null;
        if(thr instanceof IWithProcedureStackTrace)
            wpt = (IWithProcedureStackTrace)thr;
        else{
            Throwable cause = thr.getCause();
            if(cause!=null && cause instanceof IWithProcedureStackTrace){
                wpt = (IWithProcedureStackTrace)cause;
            }
        }
        if(wpt!=null){
            if(currentNode!=null && wpt.getTraceElement()==null)
                wpt.setTraceElement(currentNode);
        }
            
        
    }
    
    public void exit(){
        if(currentNode==null)
            throw new IllegalStateException("Root node already exited");
        currentNode.setExitTime(System.currentTimeMillis());
        currentNode = currentNode.getParent();
    }
    
    public String getStackTrace(){
        StringBuffer buf = new StringBuffer();
        for(TraceElement elm = currentNode; elm!=null; elm = elm.getParent()){
            buf.append(elm.getSourceName()).append("(").append(elm.getSourceFile()).append(")\r\n");
        }
        return buf.toString();
    }


}
