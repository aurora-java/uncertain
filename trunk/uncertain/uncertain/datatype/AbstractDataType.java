/*
 * Created on 2008-7-28
 */
package uncertain.datatype;

public abstract class AbstractDataType implements DataType {

    public Object convert(Object value, String format) throws ConvertionException {           
        return convert(value);
    }


}
