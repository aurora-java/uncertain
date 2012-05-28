/**
 * Created on: 2004-6-7 19:19:16
 * Author:     zhoufan
 */
package uncertain.datatype;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Registry to get DataType instance by java class name, or by number defined in java.sql.Types
 * date/number format can be configured 
 */
public class DataTypeRegistry {
    
    static DataTypeRegistry  default_instance = new DataTypeRegistry();
    
    public static DataTypeRegistry getInstance(){
        return default_instance;
    }
	
    HashMap type_map = new HashMap();
    
    HashMap class_map = new HashMap();
    
    public DataTypeRegistry(){
        type_map.put(new Integer(Types.CHAR),new StringType());
        type_map.put(new Integer(Types.VARCHAR),new StringType());
        type_map.put(new Integer(Types.TINYINT),new ByteType());
        type_map.put(new Integer(Types.SMALLINT),new IntegerType());        
        type_map.put(new Integer(Types.INTEGER),new IntegerType());
        type_map.put(new Integer(Types.BIGINT),new LongType());
        type_map.put(new Integer(Types.BOOLEAN),new BooleanType());
        type_map.put(new Integer(Types.FLOAT),new FloatType());
        type_map.put(new Integer(Types.DOUBLE),new DoubleType());
        type_map.put(new Integer(Types.DECIMAL),new LongType());
        type_map.put(new Integer(Types.NUMERIC),new LongType());
        type_map.put(new Integer(Types.DATE), new DateType());
        type_map.put(new Integer(Types.TIMESTAMP), new TimestampType());
        type_map.put(new Integer(Types.CLOB), new ClobType());
        Iterator tit = type_map.values().iterator();
        while (tit.hasNext()) {
            DataType type = (DataType) tit.next();
            class_map.put(type.getJavaType().getName(), type);
        }
        // Java primitive types
        class_map.put(int.class.getName(), class_map.get("java.lang.Integer"));
        class_map.put(boolean.class.getName(), class_map.get("java.lang.Boolean"));
        class_map.put(long.class.getName(), class_map.get("java.lang.Long"));
        class_map.put(float.class.getName(), class_map.get("java.lang.Float"));   
        class_map.put(double.class.getName(), class_map.get("java.lang.Double"));
        class_map.put(BigDecimal.class.getName(), new BigDecimalType());
        class_map.put(Class.class.getName(), new JavaClassType());
        class_map.put(java.sql.Date.class.getName(), new DateType());  
        class_map.put("date", new DateType());
        class_map.put("string", class_map.get("java.lang.String"));
    }
    
	public DataType getDataType(Class cls){
		return (DataType)class_map.get(cls.getName());
	}
	
	public DataType getDataType(String class_name){
		return (DataType)class_map.get(class_name);
	}

    public DataType getType(int id){
        return (DataType)type_map.get( new Integer(id));
    }
    
    public DataType getType( Object obj){
       return  getDataType( obj.getClass());
    }

	public Object convert( Object value, Class prefered_class) throws ConvertionException{
		DataType dt = getDataType(prefered_class);
		if( dt == null) return null;
		else return dt.convert(value);
	}
 

}