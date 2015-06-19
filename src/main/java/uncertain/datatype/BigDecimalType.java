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
import java.math.BigDecimal;

/**
 * 
 */
public class BigDecimalType extends AbstractDataType implements DataType {

	/**
	 * Constructor for IntegerType.
	 */
	public BigDecimalType() {
	}

	/**
	 * @see uncertain.datatype.DataType#getJavaType()
	 */
	public Class getJavaType() {
		return BigDecimal.class;
	}

	/**
	 * @see uncertain.datatype.DataType#getSqlType()
	 */
	public int getSqlType() {
		return Types.BIGINT;
	}

	/**
	 * @see uncertain.datatype.DataType#getObject(CallableStatement, int)
	 */
	public Object getObject(CallableStatement stmt, int id)
		throws SQLException {
		return stmt.getBigDecimal(id);
	}

	/**
	 * @see uncertain.datatype.DataType#getObject(ResultSet, int)
	 */
	public Object getObject(ResultSet rs, int id) throws SQLException {
		return rs.getBigDecimal(id);
	}

	/**
	 * @see uncertain.datatype.DataType#registerParameter(CallableStatement, int)
	 */
	public void registerParameter(CallableStatement stmt, int id)
		throws SQLException {
		stmt.registerOutParameter(id, Types.BIGINT);
	}

    public void setParameter (PreparedStatement stmt, int id, Object value ) throws SQLException{
        if(value==null)
            stmt.setNull(id, getSqlType());
        else
            stmt.setBigDecimal(id, (BigDecimal)value);
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
    			return new BigDecimal((String)value);
    		}catch(NumberFormatException ex){
    			throw new ConvertionException("Can't convert from string to BigDecimal",ex);
    		}	
        }	
		else if( value instanceof Number)
			return new BigDecimal( ((Number)value).toString());
		else
			return null;
	}

}
