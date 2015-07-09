/*
 * Created on 2015年7月6日 下午11:33:30
 * zhoufan
 */
package uncertain.pipe.base;

import uncertain.ocm.AbstractLocatableObject;

public abstract class AbstractFlowable extends AbstractLocatableObject implements IFlowable {
    
    String  id;
    String  outputId;
    IFlowable output;

    public AbstractFlowable() {
    }

    @Override
    public String getId() {
        return id;
    }
    
    public void setId(String id){
        this.id = id;
    }

    @Override
    public IFlowable getOutput() {
        return output;
    }
    
    @Override
    public void setOutput(IFlowable output) {
        this.output = output;
    }
    

    @Override
    public String getOutputId() {
        return outputId;
    }

    @Override
    public void setOutputId(String outputId) {
        this.outputId = outputId;
    }


}
