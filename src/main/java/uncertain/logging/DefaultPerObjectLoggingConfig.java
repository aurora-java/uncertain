/*
 * Created on 2011-7-19 下午10:26:05
 * $Id$
 */
package uncertain.logging;

import java.util.HashMap;
import java.util.Map;

public class DefaultPerObjectLoggingConfig implements IPerObjectLoggingConfig {
    
    Map     mConfigMap = new HashMap();
    boolean enabledAll = false;
    private String getTraceFlagKey( String object_name ){
        return object_name +".trace"; 
    }
    
    private String getLoggerProviderKey( String object_name ){
        return object_name + ".logger_provider";
    }

    public boolean getTraceFlag(String object_name) {
    	if(enabledAll)
    		return true;
        Boolean trace = (Boolean)mConfigMap.get(getTraceFlagKey(object_name));
        return trace==null?false:trace.booleanValue();
    }

    public void setTraceFlag(String object_name, boolean flag) {
        mConfigMap.put(getTraceFlagKey(object_name), new Boolean(flag));
    }

    public ILoggerProvider getLoggerProvider(String object_name) {
        ILoggerProvider provider = (ILoggerProvider)mConfigMap.get(getLoggerProviderKey(object_name));
        return provider;
    }

    public void setLoggerProvider(String object_name, ILoggerProvider provider) {
        mConfigMap.put(getLoggerProviderKey(object_name), object_name);
    }
    
    public void clearSettings(){
        mConfigMap.clear();
    }

	public boolean isEnabledAll() {
		return enabledAll;
	}

	public void setEnabledAll(boolean enabledAll) {
		this.enabledAll = enabledAll;
	}

}
