/*
 * Created on 2015年7月6日 下午9:53:17
 * zhoufan
 */
package uncertain.pipe.base;

public interface IFlowable {

    public String getId();
    
    public IFlowable getOutput();
    
    public void setOutput(IFlowable output);
    
    public String getOutputId();
    
    public void setOutputId(String id);
    
    
}
