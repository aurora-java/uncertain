/*
 * Created on 2005-11-17
 */
package uncertain.util;

import java.math.BigDecimal;

/**
 * GroupObjectProcessorImpl
 * @author Zhou Fan
 * 
 */
public class GroupObjectProcessorImpl implements IGroupObjectProcessor {
    
    BigDecimal	value = new BigDecimal((double)0);
    Object  obj   = null;
    int		type  = 0;
    int		count = 0;

    public GroupObjectProcessorImpl(int type) {
        this.type = type;
    }
    
    public void process(Object o){
        if(o!=null){
            BigDecimal ab = null;
            if(o instanceof Number || o instanceof String ) ab = new BigDecimal(o.toString());
            if(type<=IGroupObjectProcessor.COUNT)
	            if(o instanceof String )
	                o = new Double(o.toString());
            switch(type){
	        	case IGroupObjectProcessor.COUNT:
	        	    break;
	        	case IGroupObjectProcessor.SUM:
	        	case IGroupObjectProcessor.AVG:	        	    
	        	    if(o instanceof Number){
	        	        value = value.add(ab);
	        	        //value += ((Number)o).doubleValue();
	        	    }
	        	    break;
	        	case IGroupObjectProcessor.MAX:
	        	    if(o instanceof Number){
	        	        /*
	        	        double v = ((Number)o).doubleValue(); 
	        	        if(value<v) value = v;
	        	        */
	        	        if(count==0) value = ab;
	        	        if(value.compareTo(ab)<0) value = ab;
	        	    }
	        	    break;
	        	case IGroupObjectProcessor.MIN:
/*
	        	    if(o instanceof Number){	        	        
	        	        if(count==0) value = ((Number)o).doubleValue();
	        	        double v = ((Number)o).doubleValue(); 
	        	        if(value>v) value = v;
	        	    }
*/
        	        if(count==0) value = ab;
    	            if(value.compareTo(ab)>0) value = ab;
	        	    break;
	        	case IGroupObjectProcessor.FIRST:
	        	    if(count==0) obj = o;
	        	    break;
	        	case IGroupObjectProcessor.LAST:
	        	    obj = o;
	        	    break;		        
	        	default:
	        	    break;
	        }
        }
        count++;
    }
    
    public Object getObject(){
        if(type==IGroupObjectProcessor.LAST || type==IGroupObjectProcessor.FIRST)
            return obj;
        else if(type==IGroupObjectProcessor.COUNT)
            return new Long(count);
        else if(type==IGroupObjectProcessor.AVG){
            if(count>0)
                return new Double(value.doubleValue()/count);
            else
                return null;
        }
        else
            return value;
            
    }
    
    public double getNumericValue(){
        if(type==IGroupObjectProcessor.COUNT)
            return count;
        else if(type==IGroupObjectProcessor.AVG && count>0)
            return value.doubleValue()/count;
        else
            return value.doubleValue();    
    }
    
    public BigDecimal getBigDecimal(){
        return value;
    }

}
