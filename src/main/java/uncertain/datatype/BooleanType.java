/**
 * Created on: 2004-6-23 10:52:31
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
public class BooleanType extends AbstractDataType implements DataType {

	/**
	 * Constructor for BooleanType.
	 */
	public BooleanType() {
	}

	/**
	 * @see uncertain.datatype.DataType#getJavaType()
	 */
	public Class getJavaType() {
		return Boolean.class;
	}

	/**
	 * @see uncertain.datatype.DataType#getSqlType()
	 */
	public int getSqlType() {
		return Types.BOOLEAN;
	}

	/**
	 * @see uncertain.datatype.DataType#getObject(CallableStatement, int)
	 */
	public Object getObject(CallableStatement stmt, int id)
		throws SQLException {
		return Boolean.valueOf(stmt.getBoolean(id));
	}

	/**
	 * @see uncertain.datatype.DataType#getObject(ResultSet, int)
	 */
	public Object getObject(ResultSet rs, int id) throws SQLException {
		return Boolean.valueOf(rs.getBoolean(id));
	}

	/**
	 * @see uncertain.datatype.DataType#registerParameter(CallableStatement, int)
	 */
	public void registerParameter(CallableStatement stmt, int id)
		throws SQLException {
		stmt.registerOutParameter(id, Types.BOOLEAN);
	}

    public void setParameter (PreparedStatement stmt, int id, Object value ) throws SQLException{
        Boolean b = (Boolean)value;
        if(b!=null)
            stmt.setBoolean(id, b.booleanValue());
        else
            stmt.setNull(id, Types.BOOLEAN);
    }
	/**
	 * @see uncertain.datatype.DataType#convert(Object)
	 */
	public Object convert(Object value) {
		if( value instanceof String)
			return Boolean.valueOf((String)value);
		else if (value instanceof Number)
			return Boolean.valueOf(((Number)value).intValue()!=0);
        else if (value instanceof Boolean)
            return (Boolean)value;
		else	
			return null;
	}

}
