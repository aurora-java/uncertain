/**
 * Created on: 2002-11-29 15:24:48
 * Author:     zhoufan
 */
package uncertain.composite.transform;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;

/**
 * Some static method to transform CompositeMap
 */
public class Transformer {

	static Map transformer_instance = new HashMap();
	
	public static CompositeMap createTransformConfig( String cls_name){
		CompositeMap config = new CompositeMap(10);
		config.setName(CompositeTransformer.KEY_TRANSFORM);
		config.put(CompositeTransformer.KEY_CLASS, cls_name);
		return config;		
	}
	
	public static CompositeMap doTransform( CompositeMap source, CompositeMap config){
		if( source == null || config == null) return source;
		String cls_name = config.getString(CompositeTransformer.KEY_CLASS);
		
		try{
			CompositeTransformer t = (CompositeTransformer)transformer_instance.get(cls_name);
			if( t == null || (t instanceof Serializable)){
				t = (CompositeTransformer)Class.forName(cls_name).newInstance();
				if(!(t instanceof Serializable))transformer_instance.put(cls_name,t);
			}
			return t.transform(source,config);
		}catch(Throwable thr){
		    thr.printStackTrace();
			return source;
		}
	}
	
	public static CompositeMap doBatchTransform( CompositeMap source, Collection config_list){
		if( source == null || config_list == null) return source;
		Iterator it = config_list.iterator();
		while( it.hasNext()){
			CompositeMap conf = (CompositeMap)it.next();
			source = doTransform(source,conf);
		}
		return source;
	}
    
	

}
