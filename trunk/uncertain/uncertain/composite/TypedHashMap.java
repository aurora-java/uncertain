/**
 * Created on: 2002-11-28 10:00:25
 * Author:     zhoufan
 */
package uncertain.composite;

import java.util.HashMap;
import java.util.Map;

/**
 * Sub class of HashMap with helper method to get data with type information
 */
public class TypedHashMap extends HashMap {


	/**
	 * Constructor for TypedHashMap.
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public TypedHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructor for TypedHashMap.
	 * @param initialCapacity
	 */
	public TypedHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructor for TypedHashMap.
	 */
	public TypedHashMap() {
		super();
	}

	/**
	 * Constructor for TypedHashMap.
	 * @param m
	 */
	public TypedHashMap(Map m) {
		super(m);
	}


	public String getString( Object key){
		Object obj = get(key);
		return obj==null?null:obj.toString();		
	}
	
	public String getString( Object key, String default_value){
		String str = getString(key);
		return str == null? default_value: str;
	}    
    
    public void putString( Object key, String value){
        put(key,value);
    }  
    
	public Boolean getBoolean( Object key){
		Object obj = get(key);
		if( obj == null) return null;
		if( obj instanceof Boolean) return (Boolean)obj;
		if( obj instanceof String){
			Boolean bl = Boolean.valueOf((String)obj);
			put(key,bl);
			return bl;
		}
		return (Boolean)null;
	}
	
	public boolean getBoolean( Object key, boolean default_value){
		Boolean bl = getBoolean(key);
		return bl==null? default_value: bl.booleanValue();
	}
	
    
    public void putBoolean( Object key, boolean value){
        put(key,Boolean.valueOf(value));
    }
    
	public Integer getInt( Object key){
		Object obj = get(key);
		if( obj == null) return null;
		if( obj instanceof Integer) return (Integer)obj;
		if( obj instanceof Number) return new  Integer(((Number)obj).intValue());
		if( obj instanceof String){
			Integer parsed_object = new Integer((String)obj);
			put(key,parsed_object);
			return parsed_object;
		}
		return null;
	}
	
	
	public int getInt( Object key, int default_value){
		Integer parsed_object = getInt(key);
		return parsed_object == null?  default_value: ((Integer)parsed_object).intValue();
	}
    
    public void putInt( Object key, int value){
        put(key,new Integer(value));
    }
    
    
	public Long getLong( Object key){
		Object obj = get(key);
		if( obj == null) return null;
		if( obj instanceof Long) return (Long)obj;
		if( obj instanceof Number) return new  Long(((Number)obj).longValue());
		if( obj instanceof String){
			Long parsed_object = new Long((String)obj);
			put(key,parsed_object);
			return parsed_object;
		}
		return null;
	}
	
	public long getLong( Object key, long default_value){
		Long parsed_object = getLong(key);
		return parsed_object == null?  default_value: ((Long)parsed_object).longValue();
	}    

    
    public void putLong( Object key, long value){
        put(key,new Long(value));
    }
    
    
    public Short getShort( Object key ){
        Object obj = get(key);
        if( obj == null) return null;
        if( obj instanceof Short) return (Short)obj;
        if( obj instanceof Number) return new  Short(((Number)obj).shortValue());
        if( obj instanceof String){
            Short parsed_object = new Short((String)obj);
            put(key,parsed_object);
            return parsed_object;
        }
        return null;

    }

    public short getShort( Object key, short default_value){
        Short parsed_object = getShort(key);
        return parsed_object == null?  default_value: ((Short)parsed_object).shortValue();
    }
    
    public void putShort( Object key, short value){
        put(key, new Short(value));
    }	
    
    public Double getDouble( Object key ){
        Object obj = get(key);
        if( obj == null) return null;
        if( obj instanceof Double) return (Double)obj;
        if( obj instanceof Number) return new  Double(((Number)obj).doubleValue());
        if( obj instanceof String){
            Double parsed_object = new Double((String)obj);
            put(key,parsed_object);
            return parsed_object;
        }
        return null;

    }

    public double getDouble( Object key, double default_value){
        Double parsed_object = getDouble(key);
        return parsed_object == null?  default_value: ((Double)parsed_object).doubleValue();
    }

    public void putDouble( Object key, double value){
        put(key, new Double(value));
    }   

    public Float getFloat( Object key ){
        Object obj = get(key);
        if( obj == null) return null;
        if( obj instanceof Float) return (Float)obj;
        if( obj instanceof Number) return new  Float(((Number)obj).floatValue());
        if( obj instanceof String){
            Float parsed_object = new Float((String)obj);
            put(key,parsed_object);
            return parsed_object;
        }
        return null;

    }

    public float getFloat( Object key, float default_value){
        Float parsed_object = getFloat(key);
        return parsed_object == null?  default_value: ((Float)parsed_object).floatValue();
    }

    public void putFloat( Object key, float value){
        put(key, new Float(value));
    }   

    public Byte getByte( Object key ){
        Object obj = get(key);
        if( obj == null) return null;
        if( obj instanceof Byte) return (Byte)obj;
        if( obj instanceof Number) return new  Byte(((Number)obj).byteValue());
        if( obj instanceof String){
            Byte parsed_object = new Byte((String)obj);
            put(key,parsed_object);
            return parsed_object;
        }
        return null;

    }

    public byte getByte( Object key, byte default_value){
        Byte parsed_object = getByte(key);
        return parsed_object == null?  default_value: ((Byte)parsed_object).byteValue();
    }
    
    
    public int hashCode() {
    	return System.identityHashCode(this);
    }
}
