/*
 * Created on 2006-11-25
 */
package uncertain.datatype;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateType extends AbstractDataType implements DataType {
    
    String mDateFormat = "yyyy-MM-dd";
    
    public Object convert(Object value)throws ConvertionException  {
        return convert(value, mDateFormat);
    }    

    public Object convert(Object value, String fmt ) throws ConvertionException {
        if(value instanceof String){          
            String str = (String)value;
            try{
                SimpleDateFormat    format = new SimpleDateFormat(fmt);              
                java.util.Date d = (Date)format.parseObject(str);
                return new java.sql.Date(d.getTime());
            }catch(Exception ex){
                throw new ConvertionException("Can't convert from string to Date:"+ex.getMessage(),ex);
            }
        }
        else if(value instanceof Number)
            return new Date(((Number)value).longValue());
        return null;
    }

    public Class getJavaType() {
        return Date.class;
    }

    public Object getObject(CallableStatement stmt, int id) throws SQLException {
        return stmt.getDate(id);
    }

    public Object getObject(ResultSet rs, int id) throws SQLException {
        return rs.getDate(id);
    }
    
    public void setParameter (PreparedStatement stmt, int id, Object value ) throws SQLException{
        if(value==null)
            stmt.setNull(id, getSqlType());
        else{
            if( value instanceof java.util.Date)
                stmt.setDate(id, new java.sql.Date(((java.util.Date)value).getTime()));
            else if( value instanceof java.sql.Date)
                stmt.setDate(id, (java.sql.Date)value);
            else
                throw new IllegalArgumentException("Parameter No."+id+" is not instance or derived type of java.util.Date");
        }
    }

    public int getSqlType() {
        return Types.DATE;
    }

    public void registerParameter(CallableStatement stmt, int id)
            throws SQLException {
        stmt.registerOutParameter(id, Types.DATE);

    }


}
