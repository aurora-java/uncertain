/**
 * Created on: 2004-6-10 15:59:15
 * Author:     zhoufan
 */
package uncertain.ocm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Creates object mapping rule by analyzing class structure.
 * The default rule:
 * 1. All fields with first letter being capital, no matter it's private, protected
 *    or public, as long as it isn't static, will be mapped
 * 2. All methods ..
 */
public class ClassAnalyzer {
	
	HashMap		field_map;
	HashMap		method_map;
	OCManager   oc_manager;
	
	public static class MethodPair{
		
		Method getter;
		Method setter;
		String name;
		boolean from_attribute = true;
		
		public MethodPair(String _name){
			this.name = _name;
		}
		
		public String toString(){
			StringBuffer buf = new StringBuffer();
			buf.append("MethodPair[name:").append(name);
			if( getter != null) buf.append(" getter:").append(getter.getName());
			if( setter != null) buf.append(" setter:").append(setter.getName());
			buf.append(" from attribute:").append(from_attribute).append(']');
			return buf.toString();
		}
	}

	/**
	 * Constructor for ClassAnalyzer.
	 */
	public ClassAnalyzer(OCManager _oc_manager) {
		method_map = new HashMap();
		field_map = new HashMap();
		this.oc_manager = _oc_manager;
	}
	

	void prepare(){
		field_map.clear();		
		method_map.clear();
	}
	
	void addSetterMethod(String name, Method m, boolean from_attribute){
		MethodPair p = (MethodPair)method_map.get(name);
		if( p==null){
			p = new MethodPair(name);
			method_map.put(name, p);
		}
		p.setter = m;
		p.from_attribute = from_attribute;
	}

	void addGetterMethod(String name, Method m){
		MethodPair p = (MethodPair)method_map.get(name);
		if( p==null){
			p = new MethodPair(name);
			method_map.put(name, p);
		}
		p.getter = m;
	}	
	
	
	public MappingRule analyze( Class type ){
		
		prepare();
		MappingRule rule = new MappingRule(type, oc_manager);

		Field[] fields = type.getDeclaredFields();
		for(int i=0; i<fields.length; i++){
			Field f = fields[i];			
			if( acceptField(f)){
				String name = getFieldMappedName(f);
				rule.addMapping(name, f, true);
			}	
		}
		
		Method[] methods = type.getMethods();
		for(int i=0; i<methods.length; i++){
			Method m = methods[i];
			if(!acceptMethod(m)) continue;
			String name = m.getName();
			if( name.length()<=3) continue;
			String prefix = name.substring(0,3);
			String mapped_name = getMethodMappedName(m);
			if(isGetMethod(m)){
				if(prefix.equals("get")) 
					addGetterMethod(mapped_name, m);
			}else if(isSetMethod(m)){
				if(prefix.equals("set"))
					addSetterMethod(mapped_name, m, true);
				else if(prefix.equals("add"))
					addSetterMethod(mapped_name, m, false);
			}
		}
		
		Iterator it = method_map.values().iterator();
		while(it.hasNext()){
			MethodPair p = (MethodPair)it.next();
			if( p.setter != null){
				if( p.getter != null || "add".equals(p.setter.getName().substring(0,3)) )
				try{
					rule.addMapping(p.name, p.setter, p.getter, p.from_attribute);
				}catch(IllegalArgumentException ex){
				}
			}
		}
		
		rule.getReady();
		return rule;
	}
	
	protected String getFieldMappedName(Field f){
		return NamingUtil.toAttribName(f.getName());
	}
	
	protected String getMethodMappedName(Method m){
		return NamingUtil.toAttribName(m.getName().substring(3));
	}
	
	public boolean acceptField( Field field ){
		int m = field.getModifiers();
		if( Modifier.isStatic(m) )
			return false;
		return Character.isUpperCase( field.getName().charAt(0));
	}
	
	public boolean acceptMethod( Method method){
		int m = method.getModifiers();
		return  !Modifier.isStatic(m);
	}
	
	public boolean isGetMethod( Method m){
		return 
		(m.getParameterTypes().length == 0  && !m.getReturnType().equals(void.class));
	}

	public boolean isSetMethod( Method m){
		return
		m.getParameterTypes().length==1 && m.getReturnType().equals(void.class);
	}


}
