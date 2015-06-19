package uncertain.datatype;

/**
 * Created on: 2004-6-7 19:19:16
 * Author:     zhoufan
 */

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DataType {

	/**
	 * get java class representing this data type
	 * @return java.lang.Class
	 */
	public Class getJavaType();

	/**
	 * get SQL type defined in java.sql.Types
	 * @return 
	 */
	public int getSqlType();
	

	/**
	 * 
	 * @param aName java.lang.String
	 */
	public Object getObject (CallableStatement stmt,int id) throws SQLException;

	/**
	 * 
	 * @param 
	 */
	public Object getObject (ResultSet rs,int id) throws SQLException;
	
	/**
	 * Register a parameter in CallableStatement
	 * @param stmt CallableStatement to register
	 * @param id index number of parameter
	 * @throws SQLException
	 * @see java.sql.CallableStatement
	 */
	 
	public void registerParameter( CallableStatement stmt,int id) throws SQLException;

    
    public void setParameter (PreparedStatement stmt, int id, Object value ) throws SQLException;    
	
	/** try to convert unknown type object to this type 
	 *  @param value object to convert
	 *  @return converted object, or null if can't be converted
	 * */
	public Object convert( Object value) throws ConvertionException;
    
    public Object convert( Object value, String format ) throws ConvertionException;
	

}
