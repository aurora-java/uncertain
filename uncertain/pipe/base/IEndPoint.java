/*
 * Created on 2014年12月31日 下午10:18:54
 * $Id$
 */
package uncertain.pipe.base;

public interface IEndPoint extends IFlowable {
    
    public void addData(Object data);
    
    public void addData(Object data, IPipe return_pipe );    

}
