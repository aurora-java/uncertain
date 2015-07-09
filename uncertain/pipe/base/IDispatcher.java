/*
 * Created on 2015年7月6日 下午2:25:43
 * zhoufan
 */
package uncertain.pipe.base;

import java.util.List;

public interface IDispatcher extends IFlowable {

    /**
     * @param input Original input data
     * @param output An empty list to hold output, if any
     */
    public void doDispatch( IDispatchData input, List<IDispatchData> output );
}
