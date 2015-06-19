/**
 * Created on: 2004-6-18 15:57:18
 * Author:     zhoufan
 */
package uncertain.datatype;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.Format;
import java.text.ParseException;


/**
 * 
 */
public class StringType extends AbstractDataType implements DataType {


	/**
	 * @see uncertain.datatype.DataType#getJavaType()
	 */
	public Class getJavaType() {
		return String.class;
	}

	/**
	 * @see uncertain.datatype.DataType#getSqlType()
	 */
	public int getSqlType() {
		return Types.VARCHAR;
	}

	/**
	 * @see uncertain.datatype.DataType#getObject(CallableStatement, int)
	 */
	public Object getObject(CallableStatement stmt, int id)
		throws SQLException {
		return stmt.getString(id);
	}

	/**
	 * @see uncertain.datatype.DataType#getObject(ResultSet, int)
	 */
	public Object getObject(ResultSet rs, int id) throws SQLException {
		return rs.getString(id);
	}

	/**
	 * @see uncertain.datatype.DataType#registerParameter(CallableStatement, int)
	 */
	public void registerParameter(CallableStatement stmt, int id)
		throws SQLException {
		stmt.registerOutParameter(id, Types.VARCHAR);
	}

    public void setParameter (PreparedStatement stmt, int id, Object value ) throws SQLException{
        if(value==null)
            stmt.setNull(id, getSqlType());
        else
            stmt.setString(id, value.toString());
    }    
    
	/**
	 * @see uncertain.datatype.DataType#format(Object, String)
	 */
	public String format(Object obj, String format) {
		return obj.toString();
	}

	/**
	 * @see org.uncertain.datatype.DataType#parseObject(String, String)
	 */
	/** parse an object from string by specified format */
	public Object parseObject( String strValue, Format format ) throws ParseException{
		return strValue;
	}

	/**
	 * @see uncertain.datatype.DataType#convert(Object)
	 */
	public Object convert(Object value) {
		if( value != null)
			return value.toString();
		else
			return null;	
	}

}
