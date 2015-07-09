/*
 * Created on 2014年12月31日 上午12:28:49
 * $Id$
 */
package uncertain.pipe.base;


public interface IPipeManager {
    
    public IFlowable getElement( String id );

    public IPipe getPipe( String id);

    public IPipe createPipe(String id);

    //public void startAll();

}