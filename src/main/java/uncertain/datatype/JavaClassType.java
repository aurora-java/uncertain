/*
 * Created on 2006-11-25
 */
package uncertain.datatype;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;

public class JavaClassType extends StringType {

    public Class getJavaType() {
        return Class.class;
    }
    
    public Object getObject(CallableStatement stmt, int id)
    throws SQLException {
        Object  cls_name= super.getObject(stmt, id);
        return convert(cls_name);
    }

    public Object convert(Object value) {
        if( value != null)
            if( value instanceof String)
                try
                {
                    return Class.forName((String)value);
                }catch(ClassNotFoundException ex){
                    throw new RuntimeException("Can't find class "+ex.getMessage());
                }
        return null;
    }    
    
    public Object parseObject( String strValue, Format format ) throws ParseException{
        return convert(strValue);
    }    
}
