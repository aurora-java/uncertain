/*
 * Created on 2014年12月23日 下午4:54:07
 * $Id$
 */
package uncertain.pipe.base;

public class Returnable implements IReturnable {

    Object  data;
    IPipe   returnPipe;
    
    /**
     * @param data
     * @param returnPipe
     */
    public Returnable(Object data, IPipe returnPipe) {
        super();
        this.data = data;
        this.returnPipe = returnPipe;
    }


    public Object getData() {
        return data;
    }
    
    public IPipe getReturnPipe() {
        return returnPipe;
    }
    
    public void setData(Object data) {
        this.data = data;
    }


    public void setReturnPipe(IPipe returnPipe) {
        this.returnPipe = returnPipe;
    }

}
