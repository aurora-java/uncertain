/*
 * Created on 2005-10-29
 */
package uncertain.composite;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import uncertain.util.GroupObjectProcessorImpl;
import uncertain.util.IGroupObjectProcessor;
import uncertain.util.IRecordFilter;

/**
 * Provides some static utility method
 * @author Zhou Fan
 * 
 */
public class CompositeUtil {

    public static final String ANY_VALUE = "*";
    public static final String NULL_VALUE = "null";
    
    public static class ChildFinder implements IterationHandle {
        
        public String element_name;
        public String attrib_name;
        public String attrib_value;
        
        CompositeMap	result;
        
        public CompositeMap getResult(){
            return result; 
        }
        
        public int process( CompositeMap map ){
            if(element_name!=null)
                if(!CompositeUtil.compare(map.getName(), element_name))
                    return IterationHandle.IT_CONTINUE;
            if(attrib_name!=null){
                Object vl = map.get(attrib_name);
                if(CompositeUtil.compare(vl, attrib_value)){
                    result = map;
                    return IterationHandle.IT_BREAK;
                }
            }else{
                result = map;
                return IterationHandle.IT_BREAK;
            }
            return IterationHandle.IT_CONTINUE;
        }        
        
        /**
         * @param element_name
         * @param attrib_name
         * @param attrib_value
         */
        public ChildFinder(String element_name, String attrib_name,
                String attrib_value) {
            this.element_name = element_name;
            this.attrib_name = attrib_name;
            this.attrib_value = attrib_value;
        }
    };
    
    public static class ChildsFinder implements IterationHandle {
    	
        private String element_name;
        private String attrib_name;
        private String attrib_value;
        
        private LinkedList	result = new LinkedList();;
        
        public LinkedList getResult(){
            return result; 
        }
        
        public int process( CompositeMap map ){
        	
            if(element_name!=null)
                if(!CompositeUtil.compare(map.getName(), element_name))
                    return IterationHandle.IT_CONTINUE;
            if(attrib_name!=null){
                Object vl = map.get(attrib_name);
                if(CompositeUtil.compare(vl, attrib_value)){
                    result.add(map);
                }
            }else{
                result.add(map);
            }
            return IterationHandle.IT_CONTINUE;
        }        
        
        /**
         * @param element_name
         * @param attrib_name
         * @param attrib_value
         */
        public ChildsFinder(String element_name, String attrib_name,String attrib_value) {
            this.element_name = element_name;
            this.attrib_name = attrib_name;
            this.attrib_value = attrib_value;
        }
    };
    
    
    static boolean compare(Object field, String value){
        if(field==null){
            if(value==null) return true;
            if(NULL_VALUE.equals(value)) return true;
            else return false;
        }else{
            if(ANY_VALUE.equals(value)) return true;
            else return field.toString().equals(value);
        }
    }

   public static boolean compareObject(CompositeMap map, String access_path, String value){
       Object obj = map.getObject(access_path);
       String parsed_value = TextParser.parse(value, map);
       return compare(obj, parsed_value);
   }
   
   public static boolean compareObjectDirect(CompositeMap map, String access_path, String value){
       Object obj = map.getObject(access_path);
       return compare(obj, value);     
   }
   
   /**
    * Find a child CompositeMap, with specified element name and attribute value
    * @param root
    * @param elementName
    * @param attribName
    * @param value
    * @return
    */
   public static CompositeMap findChild(CompositeMap root, String elementName, String attribName, String value){
       if(root==null) return null;
       ChildFinder cf = new ChildFinder(elementName, attribName, value);
       root.iterate(cf,true);
       return cf.getResult();
   }
   
   public static CompositeMap findChild( CompositeMap root, String elementName ){
       if(root==null) return null;
       ChildFinder cf = new ChildFinder(elementName, null, null);
       root.iterate(cf,true);
       return cf.getResult();
   }
   
   public static List findChilds(CompositeMap root, String elementName, String attribName, String value) {
	   if(root==null) return null;
       ChildsFinder cf = new ChildsFinder(elementName, attribName, value);
       root.iterate(cf,true);
       return cf.getResult();
   }
   
   public static List findChilds( CompositeMap root, String elementName ){
       if(root==null) return null;
       ChildsFinder cf = new ChildsFinder(elementName, null, null);
       root.iterate(cf,true);
       return cf.getResult();
   }
   
   /**
    * Find parent with specified name
    * @param map
    * @param element_name
    * @return
    */
   public static CompositeMap findParentWithName( CompositeMap map, String element_name ){
       CompositeMap parent = map.getParent();
       while(parent!=null){
           if(element_name!=null)
               if(element_name.equals(parent.getName()))
                   return parent;
           parent = parent.getParent();
       }
       return null;
   }
   
   static int getFunctionType(String name){
       String funs[] =IGroupObjectProcessor.GROUP_FUNCITONS; 
       for(int i=0; i<funs.length; i++){
           if(funs[i].equalsIgnoreCase(name))
               return i;
       }
       return -1;
   }
   
   /** return sum for specified field from a collection containing maps */
   static Object getResult(int type, Collection list, String field){
       if(list==null) return null;
       GroupObjectProcessorImpl gp = new GroupObjectProcessorImpl(type);
       Iterator it = list.iterator();
       //double sum = 0;
       while(it.hasNext()){
           CompositeMap	m = (CompositeMap)it.next();
           Object o = m.getObject(field);
           if(o!=null)
           gp.process(o);
       }
       return gp.getObject();
   }
   
   public static Object groupResult(CompositeMap root, String field, String function){
       int type = getFunctionType(function);
       if(type<0) throw new IllegalArgumentException("function "+function+" is not defined");
       Collection childs = root.getChilds();
       return getResult(type,childs,field);
   }
   
   private static String getKey(CompositeMap map, Object[] keys){
       StringBuffer buf = new StringBuffer();
       for(int i=0; i<keys.length; i++) buf.append(map.get(keys[i]));
       return buf.toString();
   }
   
   /**
    * Join to list of CompositeMap, put all fields in list2 into the one in list1
    * that has the same value get from each element of key_fields
    * @param list1 The target list to be joined
    * @param list2 The source list whose items will be examined and joined into list1
    * @param key_fields An array of key
    */
   public static void join(Collection list1, Collection list2, Object[] key_fields){
       HashMap  join_index = new HashMap();
       Iterator it = list1.iterator();
       while(it.hasNext()){
           CompositeMap item = (CompositeMap)it.next();
           String key = getKey(item, key_fields);
           join_index.put(key, item);
       }
       it = list2.iterator();
       while(it.hasNext()){
           CompositeMap source = (CompositeMap)it.next();
           String key = getKey(source, key_fields);
           CompositeMap target = (CompositeMap)join_index.get(key);
           if(target!=null) target.putAll(source);
       }
       join_index.clear();
   }
   
   /**
    * Make a CompositeMap simple
    * <root>
    *  <field1>1</field1>   -> <root field1="1"/>
    * </root> 
    */
   public static CompositeMap  collapse(CompositeMap root){
       List childs = root.getChilds();
       if(childs==null) return root;
       ListIterator it = childs.listIterator();
       while(it.hasNext()){
           CompositeMap child = (CompositeMap) it.next();
           String text = child.getText();
           if(child.size()==0 && text !=null){
               root.put(child.getName(), text);
               it.remove();
           } else
               collapse(child);
       }
       return root;
   }
   
   public static int uniqueHashCode(CompositeMap m){
       return System.identityHashCode(m);     
   }
   
   /**
    * Convert into a two dimension array
    * @param m the source CompositeMap
    * @param fields name of attribute that will be put into array
    * @param filter a filter to decide whether a record is acceptable
    * @return converted 2D Object array 
    */
   public static Object[][] toArray(CompositeMap m, String[] fields, IRecordFilter filter) {
       List childs = m.getChilds();
       if(childs==null)
           return null;
       Object[][]   data = new Object[childs.size()][fields.length];
       Iterator it = childs.iterator();
       int row = 0;
       while(it.hasNext()){
           CompositeMap record = (CompositeMap)it.next();
           if(filter!=null)
               if(!filter.accepts(record))
                   continue;
           for(int col=0; col<fields.length; col++){
               data[row][col] = record.get(fields[col]);
           }
           row++;    
       }       
       return data;
   }
   
   /**
    * put childs into a Map, using specified field as key in each child item
    * @param target Target Map to hold data
    * @param data Source data containing childs that will be processed
    * @param key_field key field that will identify each child
    */
   public static void fillMap( Map target, CompositeMap data, Object key_field ){
       Iterator it = data.getChildIterator();
       if( it==null ) return;
       while(it.hasNext()){
           CompositeMap item = (CompositeMap)it.next();
           Object key = item.get(key_field);
           target.put(key, item);
       }
   }

   /**
    * put specified field value into a Map
    * @param target Target Map to hold data
    * @param data Source data containing childs that will be processed
    * @param key_field key field that will identify each child
    * @param value_field value field that will be put into target Map
    */
   public static void fillMap( Map target, CompositeMap data, Object key_field, Object value_field ){
       Iterator it = data.getChildIterator();
       if( it==null ) return;
       while(it.hasNext()){
           CompositeMap item = (CompositeMap)it.next();
           Object key = item.get(key_field);
           Object value = item.get(value_field);
           target.put(key, value);
       }
   }   
   
   public static Object[][] toArray(CompositeMap m, String[] fields){
       return toArray(m,fields,null);
   }
   /** connect attribute from all childs into a string, separated by specified separator string */
   public static String connectAttribute( CompositeMap root, String attrib_name, String separator){
       if(root==null) return null;
       Iterator it = root.getChildIterator();
       if(it==null) return null;
       StringBuffer result = new StringBuffer();
       int id=0;
       while(it.hasNext()){
           CompositeMap child = (CompositeMap)it.next();
           Object value = child.get(attrib_name);
           if(id>0) result.append(separator);
           result.append(value);
           id++;
       }
       return result.toString();
   }
   
   public static String connectAttribute( CompositeMap root, String attrib_name ){
       return connectAttribute( root, attrib_name, ",");
   }

}
