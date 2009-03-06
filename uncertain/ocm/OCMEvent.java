/**
 * Created on: 2004-9-9 21:43:04
 * Author:     zhoufan
 */
package uncertain.ocm;

import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.util.LoggingUtil;


/**
 * Class that encapsulates an O/C mapping event
 */
public class OCMEvent {
	
    public int				level = Level.INFO.intValue();
	//public int 			id;
	public String			name;
	public Object			sender;
	public Object			parameter;

	/**
	 * Constructor for OCMEvent.
	 */
	public OCMEvent(String name, Object sender, Object parameter) {
		//this.id = id;
		this.name = name;
		this.sender = sender;
		this.parameter = parameter;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append("Event Name:").append(name);
		buf.append(" sender:"+sender);
		if(parameter !=null){
			buf.append(" Parameter:");
			if(parameter instanceof CompositeMap)
				buf.append(((CompositeMap)parameter).toXML());
			else
				buf.append(parameter);
		}
		return buf.toString();
	}
	
	public Level getLevel(){
	    return LoggingUtil.getLevel(level);	        
	}

}
