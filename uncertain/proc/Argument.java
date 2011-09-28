package uncertain.proc;

import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.datatype.ConvertionException;
import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.ConfigurationFileException;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;

public class Argument extends AbstractLocatableObject {
	private String type;
	private String value;
	private String path;
	private Object objectValue;
	private Class classType;

	public void onInitialize(CompositeMap context,IObjectRegistry registry){
		if(path==null && value ==null){
			throw BuiltinExceptionFactory.createOneAttributeMissing(this, "path,value");
		}
		if(path != null && value !=null){
			throw BuiltinExceptionFactory.createConflictAttributesExcepiton(this, "path,value");
		}
		objectValue = value;
		if(context != null){
			type = TextParser.parse(type, context);
			if(value != null)
				objectValue = TextParser.parse(value, context);
			else
				objectValue = context.getObject(path);
		}
		try {
			classType = (Class)primitiveClazz.get(type);
			if(classType == null)
				classType = Class.forName(type);
			if(DataTypeRegistry.getInstance().getDataType(classType) != null)
				objectValue = DataTypeRegistry.getInstance().convert(objectValue,classType);
			else if("instance".equals(path)){
				if("uncertain.ocm.IObjectRegistry".equals(type)){
					objectValue = registry;
				}else{
					objectValue = registry.getInstanceOfType(classType);
				}
			}
		} catch (ClassNotFoundException e) {
			throw BuiltinExceptionFactory.createClassNotFoundException(this, type);
		}catch (ConvertionException e) {
			throw new ConfigurationFileException("uncertain.exception.convertion_exception", new Object[]{objectValue,classType}, e, this);
		} 
		
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void validConfig(){
		
	}

	public Object getObjectValue() {
		return objectValue;
	}
	public void setObjectValue(Object objectValue) {
		this.objectValue = objectValue;
	}
	public Class getClassType() {
		return classType;
	}
	public void setClassType(Class classType) {
		this.classType = classType;
	}
	private static final Map primitiveClazz; // 基本类型的class

	private static final String INTEGER = "int";
	private static final String BYTE = "byte";
	private static final String CHARACTOR = "char";
	private static final String SHORT = "short";
	private static final String LONG = "long";
	private static final String FLOAT = "float";
	private static final String DOUBLE = "double";
	private static final String BOOLEAN = "boolean";

	static
	{
		primitiveClazz = new HashMap();
		primitiveClazz.put(INTEGER, int.class);
		primitiveClazz.put(BYTE, byte.class);
		primitiveClazz.put(CHARACTOR, char.class);
		primitiveClazz.put(SHORT, short.class);
		primitiveClazz.put(LONG, long.class);
		primitiveClazz.put(FLOAT, float.class);
		primitiveClazz.put(DOUBLE, double.class);
		primitiveClazz.put(BOOLEAN, boolean.class);
	}
}
