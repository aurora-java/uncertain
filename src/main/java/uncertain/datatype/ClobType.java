/**
 * Created on: 2009-11-09 14:56:50
 * Author:     zhoufan
 */
package uncertain.datatype;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;

/**
 * 
 */
public class ClobType extends AbstractDataType implements DataType {

	/**
	 * Constructor for IntegerType.
	 */
	public ClobType() {
	}

	/**
	 * @see uncertain.datatype.DataType#getJavaType()
	 */
	public Class getJavaType() {
	    return Clob.class;
	}

	/**
	 * @see uncertain.datatype.DataType#getSqlType()
	 */
	public int getSqlType() {
	    return Types.CLOB;
	}

	/**
	 * @see uncertain.datatype.DataType#getObject(CallableStatement, int)
	 */
	public Object getObject(CallableStatement stmt, int id)
		throws SQLException {
	    return stmt.getClob(id);
	}

	/**
	 * @see uncertain.datatype.DataType#getObject(ResultSet, int)
	 */
	public Object getObject(ResultSet rs, int id) throws SQLException {
	    Clob aClob = rs.getClob(id);
	    if( aClob == null) return null;
	    try{
	        StringBuffer buf = new StringBuffer();
	        Reader reader = aClob.getCharacterStream();
	        if( reader == null) return null;
	        int n;
	        while( ( n = reader.read()) != -1) buf.append((char)n);
	        return buf.toString();
	    } catch(IOException ex){
	        ex.printStackTrace();
	        return null;
	    }
	}

	/**
	 * @see uncertain.datatype.DataType#registerParameter(CallableStatement, int)
	 */
	public void registerParameter(CallableStatement stmt, int id)
		throws SQLException {
		stmt.registerOutParameter(id, Types.CLOB);
	}

    public void setParameter (PreparedStatement stmt, int id, Object value ) throws SQLException{
        if(value==null)
            stmt.setNull(id, getSqlType());
        else{
            if(value instanceof Clob)
                stmt.setClob(id, (Clob)value);
            else
                stmt.setString(id, value.toString());
        }
    }     
	/**
	 * @see uncertain.datatype.DataType#convert(Object)
	 */
	public Object convert(Object value)
        throws ConvertionException
    {
		return value;
	}

}
