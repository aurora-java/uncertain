/*
 * Created on 2006-11-25
 */
package uncertain.datatype;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TimestampType extends AbstractDataType implements DataType {
    
    String mDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    String mDateFormat2 = "yyyy-MM-dd";
    
    public Object convert(Object value)throws ConvertionException  {
    	try {
    		return convert(value, mDateTimeFormat);
    	}catch(ConvertionException e){
    		return convert(value, mDateFormat2);
    	}
    }

    public Object convert(Object value, String fmt ) throws ConvertionException {
        if(value instanceof String){          
            String str = (String)value;
            try{
                SimpleDateFormat    format = new SimpleDateFormat(fmt);                
                return new Timestamp( ((Date)format.parseObject(str)).getTime() );
            }catch(Exception ex){
                throw new ConvertionException("Can't convert from string to Timestamp",ex);
            }
        }
        else if(value instanceof Number)
            return new Timestamp(((Number)value).longValue());
        else if(value instanceof java.util.Date)
            return new Timestamp(((java.util.Date)value).getTime());
        return null;
    }

    public Class getJavaType() {
        return Timestamp.class;
    }

    public Object getObject(CallableStatement stmt, int id) throws SQLException {
        return stmt.getTimestamp(id);
    }

    public Object getObject(ResultSet rs, int id) throws SQLException {
        return rs.getTimestamp(id);
    }
    
    public void setParameter (PreparedStatement stmt, int id, Object value ) throws SQLException{
        if(value==null)
            stmt.setNull(id, getSqlType());
        else
            stmt.setTimestamp(id, (java.sql.Timestamp)value);
    }    
    
    public int getSqlType() {
        return Types.TIMESTAMP;
    }

    public void registerParameter(CallableStatement stmt, int id)
            throws SQLException {
        stmt.registerOutParameter(id, Types.TIMESTAMP);

    }

}
