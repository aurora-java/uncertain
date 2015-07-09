/*
 * Created on 2015年7月6日 下午2:53:13
 * zhoufan
 */
package uncertain.pipe.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.GeneralException;
import uncertain.pipe.base.AbstractFlowable;
import uncertain.pipe.base.IDispatchData;
import uncertain.pipe.base.IDispatcher;
import uncertain.pipe.base.IEndPoint;
import uncertain.pipe.base.IFlowable;
import uncertain.pipe.base.IPipeManager;

public class Router extends AbstractFlowable implements IDispatcher {

    IPipeManager pipeManager;
    List<RoutingRule> ruleList;
    Map<String, RoutingRule> resultMapping;
    RoutingRule defaultRule;

    protected Router() {
        ruleList = new LinkedList<RoutingRule>();
        resultMapping = new HashMap<String, RoutingRule>();
    }

    public Router(IPipeManager pm) {
        this();
        this.pipeManager = pm;
    }

    public RoutingRule getRule(String result) {
        return resultMapping.get(result == null ? null : result.toLowerCase());
    }

    public void addRoutingRule(RoutingRule rule) {
        if (rule.getTarget() == null) {
            throw BuiltinExceptionFactory.createAttributeMissing(rule, "endPoint");
        }
        ruleList.add(rule);
        String result = rule.getResult();
        if (result != null)
            result = result.toLowerCase();
        resultMapping.put(result, rule);
        if (rule.getDefault())
            defaultRule = rule;
    }

    public void addRules(Collection<RoutingRule> rules) {
        for (RoutingRule rule : rules)
            addRoutingRule(rule);
    }

    @Override
    public void doDispatch(IDispatchData data,  List<IDispatchData> output ) {
        IDispatchData disp_data = (IDispatchData) data;
        String result = disp_data.getDispatchResult();
        RoutingRule rule = getRule(result);
        if (rule == null) {
            if (defaultRule == null)
                throw new IllegalStateException(
                        String.format("Can't find routing rule for result %s and no default rule is set", result));
            else
                rule = defaultRule;
        }
        String endp = rule.getTarget();
        IFlowable end_point = pipeManager.getElement(endp);
        if (end_point == null)
            throw new GeneralException("uncertain.pipe.id_not_exist", new Object[] { endp }, this);
        PipeExecutor.executeNext(end_point, disp_data);
    }


}
