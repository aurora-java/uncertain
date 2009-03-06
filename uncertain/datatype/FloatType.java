/**
 * Created on: 2004-6-23 10:26:50
 * Author:     zhoufan
 */
package uncertain.datatype;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 
 */
public class FloatType extends AbstractDataType implements DataType {

	/**
	 * Constructor for IntegerType.
	 */
	public FloatType() {
	}

	/**
	 * @see uncertain.datatype.DataType#getJavaType()
	 */
	public Class getJavaType() {
		return Float.class;
	}

	/**
	 * @see uncertain.datatype.DataType#getSqlType()
	 */
	public int getSqlType() {
		return Types.FLOAT;
	}

	/**
	 * @see uncertain.datatype.DataType#getObject(CallableStatement, int)
	 */
	public Object getObject(CallableStatement stmt, int id)
		throws SQLException {
		return new Float(stmt.getFloat(id));
	}

	/**
	 * @see uncertain.datatype.DataType#getObject(ResultSet, int)
	 */
	public Object getObject(ResultSet rs, int id) throws SQLException {
		return new Float(rs.getFloat(id));
	}

	/**
	 * @see uncertain.datatype.DataType#registerParameter(CallableStatement, int)
	 */
	public void registerParameter(CallableStatement stmt, int id)
		throws SQLException {
		stmt.registerOutParameter(id, Types.FLOAT);
	}

    public void setParameter (PreparedStatement stmt, int id, Object value ) throws SQLException{
        if(value==null)
            stmt.setNull(id, getSqlType());
        else
            stmt.setFloat(id, ((Number)value).floatValue());
    }     
	/**
	 * @see uncertain.datatype.DataType#convert(Object)
	 */
	public Object convert(Object value) 
        throws ConvertionException
    {
		if( value instanceof String){
            if(((String)value).length()==0)
                return null;            
    		try{
    			return new Float((String)value);
    		}catch(NumberFormatException ex){
                throw new ConvertionException("Can't convert from string to float",ex);
    		}	
        }
		else if( value instanceof Number)
			return new Float(((Number)value).floatValue());
		else
			return null;
	}

}
