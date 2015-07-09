/*
 * Created on 2014年12月16日 下午3:41:06
 * $Id$
 */
package uncertain.pipe.base;

public interface IPipe extends IEndPoint {

    public void addFilter(IFilter filter);

    public boolean removeFilter(IFilter filter);
/*
    public void addData(Object data);
    
    public void addData(Object data, IPipe return_pipe );
*/
    public IProcessor getProcessor();

    public void setProcessor(IProcessor processor);

//    public String getId();
    
    public void start();
    
    public void shutdown();
    
//    public IEndPoint getOutput();
/*    
    public String getOutputId();

    public void setOutputId(String outputId);
*/
}