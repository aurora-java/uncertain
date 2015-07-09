/*
 * Created on 2015年7月6日 下午2:54:55
 * zhoufan
 */
package uncertain.pipe.impl;

import uncertain.ocm.AbstractLocatableObject;

public class RoutingRule extends AbstractLocatableObject {
    
    String      result;
    String      target;
    boolean     isDefault = false;

    public RoutingRule() {
    }

    public String getResult() {
        return result;
    }

    public String getTarget() {
        return target;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean getDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

}
