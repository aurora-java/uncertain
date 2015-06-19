/*
 * Created on 2005-5-18
 */
package uncertain.proc;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IConfigurable;
import uncertain.ocm.OCManager;

/**
 * Population of entry class is deferred to run() method, so other feature
 * class can modify config before procedure actual runs
 * @author Zhou Fan
 * 
 */
public abstract class AbstractDeferredEntry extends AbstractEntry implements IConfigurable {
    
    protected CompositeMap    mEntryConfig;
    protected OCManager       mOCManager;
    
    public AbstractDeferredEntry( OCManager ocManager ){
        this.mOCManager = ocManager;
    }
    
    /**
     * This method is provided for sub class to invoke
     * in run() method, so population can be done before
     * entry runs
     *
     */
    public void doPopulate(){
        if(mEntryConfig!=null)
            mOCManager.populateObject(mEntryConfig, this);
    }
    
    public void beginConfigure(CompositeMap config){
        super.beginConfigure(config);
        this.mEntryConfig = config;
    }
    

    public void endConfigure(){
        ;    
    }
    

}
