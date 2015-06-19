/*
 * Created on 2007-6-8
 */
package uncertain.composite;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONAdaptor {

    public static final String DEFAULT_ROOT_ELEMENT_NAME = "object";
    
    public static final String DEFAULT_RECORD_NAME = "record";
    
    public static final String DEFAULT_ARRAY_NAME = "record";


    private static Object convert_internal(Object obj, String key) 
        throws JSONException
   {
        if(obj instanceof JSONObject){
            CompositeMap child = toMap((JSONObject)obj, key);
            return child;
        }
        else if(obj instanceof JSONArray){
            JSONArray array = (JSONArray)obj;
            CompositeMap item = new CompositeMap(key);
            for(int i=0; i<array.length(); i++){
                Object element = array.get(i);
                if(element instanceof JSONObject || element instanceof JSONArray){
                    add_internal(item, DEFAULT_RECORD_NAME, element );
                }else{
                    CompositeMap map = new CompositeMap(2);
                    map.setName(DEFAULT_RECORD_NAME);
                    map.put("value", element);
                    item.addChild(map);
                }
            }
            return item;
        }
        else
            return obj;        
    }
    
    private static CompositeMap  add_internal( CompositeMap map, final String key, final Object value)
        throws JSONException
    {
        if(JSONObject.NULL.equals(value)){
            map.put(key, null);
        }else if(value instanceof JSONObject || value instanceof JSONArray){
            final CompositeMap child = (CompositeMap)convert_internal(value, key);
            map.addChild(child);
        }else{
            map.put(key, value);
        }        
        return map;
    }
    
    public static CompositeMap toMap(JSONObject obj, CompositeMap map) {
        Iterator it = obj.keys();
        while(it.hasNext()){
            String key = (String)it.next();
            Object value = null;
            try{
                value = obj.get(key);
                add_internal(map, key, value);
            } catch(JSONException ex){
                continue;
            }
        }
        return map;
        
    }
    
    public static CompositeMap  toMap( JSONObject obj, String root_element_name) {
        CompositeMap    root = new CompositeMap(root_element_name);
        return toMap(obj, root);
    }
    
    public static CompositeMap toMap(JSONObject obj){
        return toMap(obj, DEFAULT_ROOT_ELEMENT_NAME);
    }
    
    static Object toJSONObjectInternal( Object value ){
        if(value instanceof CompositeMap)
            return toJSONObject((CompositeMap)value);
        else if(value instanceof Collection){
            Collection lst = (Collection)value; 
            JSONArray array = new JSONArray();
            Iterator it = lst.iterator();
            while(it.hasNext()){
                Object item = it.next();
                if(item instanceof Collection || item instanceof CompositeMap){
                    array.put(toJSONObjectInternal(item));                    
                }else
                    array.put(item);
            }
            return array;
        }
        else
            return value;
    }
    
    public static JSONObject toJSONObject(CompositeMap map){
        return toJSONObject(map, null);
    }
    
    static void copyToJSON( CompositeMap map, JSONObject obj ){
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            Object value = entry.getValue();
            value = toJSONObjectInternal(value);
            try{
                Object key = entry.getKey();
                if(key!=null)
                    obj.put(key.toString(), value);
            }catch(JSONException ex){
                continue;
            }
        }        
    }
    
    public static JSONObject toJSONObject(CompositeMap map, Set<String> array_names){
        //JSONObject obj = new JSONObject(map);
        JSONObject obj = new JSONObject();
        copyToJSON(map, obj);        
        Iterator it = map.getChildIterator();
        if(it!=null)
            while(it.hasNext()){
                CompositeMap    item = (CompositeMap)it.next();
                //Convert to JSONArray, if doesn't contain attribute
                String name = item.getName();
                boolean as_array = false;
                if(array_names!=null){
                    if(array_names.contains(name))
                        as_array = true;
                }else{
                    if(item.size()==0 && item.getChilds()!=null)
                        as_array = true;
                }
                try{                    
                    if(as_array){
                        JSONArray array = new JSONArray();
                        
                        for(Iterator cit = item.getChildIterator(); cit!=null && cit.hasNext();){
                            CompositeMap child = (CompositeMap)cit.next();
                            array.put(toJSONObject(child));
                        }
                        // Modified 2008-8-4
                        /*
                        if(item.size()==0)
                            obj.put(name, array);
                        else{
                        
                            JSONObject child_obj = new JSONObject();
                            copyToJSON(item, child_obj);
                            child_obj.put(DEFAULT_ARRAY_NAME, array);
                            obj.put(name, child_obj);
                        }
                        */
                        // Always put default array name
                        JSONObject child_obj = new JSONObject();
                        copyToJSON(item, child_obj);
                        child_obj.put(DEFAULT_ARRAY_NAME, array);
                        obj.put(name, child_obj);
                        
                    }else{
                        JSONObject child = toJSONObject(item);    
                        obj.accumulate(name, child);
                    }
                }catch(JSONException ex){
                    continue;
                }
                
            }
        return obj;
    }
    
    public static JSONObject createJSONObject(InputStream is) throws IOException, JSONException {
        StringBuffer buf = new StringBuffer();
        int ch = is.read();
        while(ch!=-1){
            buf.append((char)ch);
            ch = is.read();
        }
        return new JSONObject(buf.toString());
    }

    
    public static void main(String[] args) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("name", "ABC");
        obj.put("address", "SH");
        
        JSONArray a = new JSONArray();
        a.put(1);
        a.put(true);
        obj.put("test", a);
        
        JSONObject o1 = new JSONObject();
        JSONObject o2 = new JSONObject();
        o1.put("valid", true);

        JSONArray b= new JSONArray();
        b.put("CONFIRM");
        b.put(o1);

        o2.put("result", 4.5);
        o2.put("result-list", b);
        
        obj.put("detail", o2);
        
        CompositeMap map = toMap(obj);        
        System.out.println(map.toXML());
        System.out.println(toJSONObject(map));
    }
    

}
