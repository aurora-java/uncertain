/*
 * Created on 2015年7月7日 下午3:03:26
 * zhoufan
 */
package uncertain.pipe.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uncertain.pipe.base.CircularReferenceException;
import uncertain.pipe.base.DispatchData;
import uncertain.pipe.base.IDispatchData;
import uncertain.pipe.base.IDispatcher;
import uncertain.pipe.base.IEndPoint;
import uncertain.pipe.base.IFlowable;

public class PipeExecutor {

    private static List<IDispatchData> executeDispatch( IDispatcher dispatch, Object result ){
        IDispatchData dd = null;
        if(result instanceof IDispatchData)
            dd = (IDispatchData)result;
        else
            dd = new DispatchData(result);
        List<IDispatchData> lst = new LinkedList<IDispatchData>();
        dispatch.doDispatch(dd, lst);
        return lst;
    }

    public static void executeNext( IFlowable ep, Object result ){
        if( ep==null )
            return;
        if( ep instanceof IDispatcher ){
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
            if( ep != null  && (ep instanceof IEndPoint) ){
                if(rs_list!=null&&rs_list.size()>0)
                    executeNext(ep, rs_list);
            }
            executed_set.clear();
        }else if( ep instanceof IEndPoint){
            Object input = result instanceof IDispatchData ? ((IDispatchData)result).getData(): result; 
            ((IEndPoint)ep).addData(input);
        }
    }

    public static String toString(Set<IDispatcher> set){
        StringBuffer buf = new StringBuffer();
        for(IDispatcher disp:set)
            buf.append(disp.getId()).append(' ');
        return buf.toString();
    }

    public static void executeNext( IFlowable ep, List<IDispatchData> input ){
        for(IDispatchData data:input){
            executeNext(ep, data);
        }
    }

}
