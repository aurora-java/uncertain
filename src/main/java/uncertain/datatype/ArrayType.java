/*
 * Created on 2007-11-1
 */
package uncertain.datatype;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ArrayType extends AbstractDataType implements DataType {

    /**
     * don't do convertion
     */
    public Object convert(Object value) throws ConvertionException {
        return value;
    }

    /**
     * 
     */
    public Class getJavaType() {
        return Array.class;
    }

    public Object getObject(CallableStatement stmt, int id) throws SQLException {
         return stmt.getArray(id);
    }

    public Object getObject(ResultSet rs, int id) throws SQLException {
        return rs.getArray(id);
    }

    public int getSqlType() {
        return Types.ARRAY;
    }

    public void registerParameter(CallableStatement stmt, int id)
            throws SQLException {
        stmt.registerOutParameter(id, Types.ARRAY);

    }

    public void setParameter(PreparedStatement stmt, int id, Object value)
            throws SQLException {
        if(value==null)
            stmt.setNull(id, Types.ARRAY);
        else
            stmt.setArray(id, (Array)value);
    }

}
