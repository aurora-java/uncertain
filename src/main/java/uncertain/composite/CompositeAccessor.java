/*
 * CompositeAccessor.java
 *
 * Created on 2002年1月23日, 下午3:14
 */

package uncertain.composite;

import uncertain.util.StringSplitHandle;
import uncertain.util.StringSplitter;

/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class CompositeAccessor implements ICompositeAccessor {
    
    static CompositeAccessor default_inst = new CompositeAccessor();
    
    public static CompositeAccessor defaultInstance(){ return default_inst; }
    
    char separator_char = DEFAULT_SEPARATOR;
    char attribute_char = DEFAULT_ATTRIB_CHAR;
    
    public static final char DEFAULT_SEPARATOR = '/';
    public static final char DEFAULT_ATTRIB_CHAR = '@';
    
    
    public static CompositeMap putObject( CompositeMap map, String key, boolean is_attrib){
         CompositeMap container = null;
         if( is_attrib) {
                  container = new CompositeMap(key);
                   map.put(key, container);
         }
         else
            container = map.createChild(null,null,key);          
         return container;
    }
    
    private class KeySplitter implements StringSplitHandle{
        
        public CompositeMap 	root;
        public Object 			container;
        public String  		key;
        public boolean 		create;
        int 					null_count = 0;
        boolean 				is_attrib = false;
//        int					char_pos = 0;  
        
        
        public void processString(String _key) {
/*        	
        	if( char_pos == 0){
        		if( 
        	}
        	char_pos++;
*/
            if( root == null) null_count++;
            if( null_count>1) return;
            if( _key == null) return ;
            if( _key.length()==0){
            	root = root.getRoot();
            	return;        
            }
            is_attrib = (_key.charAt(0) == attribute_char);
            
            if( is_attrib ){
                   key = _key.substring(1);
                   container = root.get(key);           
            }
            else{
                   key = _key;
                   if("..".equals(key))
                       container = root.getParent();
                   else
                       container = root.getChild(key);
            }
            
            if( container != null && container instanceof CompositeMap) 
                       root = (CompositeMap)container;
            else{ 
                   if(create){
                       root = putObject( root, key, is_attrib);
                       container = root;
                   }
                   else{
                       root = null;
                       null_count ++;
                   }
            }
        }
        
        public KeySplitter( CompositeMap _root, String _key, boolean _create){
            root = _root;
            key = _key;
            create = _create;
            container = root;
        }
        
        public boolean exists(){
            return null_count <2;
        }
        
        public Object getValue(){
            return container;
        }
        
        public CompositeMap getRoot(){
            return root;
        }
    }

    /** Creates new CompositeRetriever */
    public CompositeAccessor() {
    }
    
    KeySplitter SplitKey(CompositeMap map, String key, boolean create){
        KeySplitter splitter = new KeySplitter( map, key, create);
        StringSplitter.split( key, 0,  separator_char , true, splitter);
        return splitter;
    }
    
    public void put( CompositeMap map, String key, Object value){
        put( map,key,value,true);
    }
    
    /** put an object into the CompositeMap, with a location notation in an XPath like syntax
     * <ul>
     * <li> a '/' followed by an string indicates a child CompositeMap, whose name is the same
     *      as the string.
     * <li> a '@' followed by an string indicates an object stored in current CompositeMap, 
     *      with the string as key.
     * <li> multiple location notation can be concatenated 
     * </ul>
     * for example:
     * <code>
     *  CompositeMap root = new CompositeMap();
     *  CompositeVisitor visitor = CompositeVisitor.defaultInstance();
     *  visitor.createChild(null,null,"ChildONE");
     * </code>
     * @param map the CompositeMap as root to access
     * @param key location notation string
     * @param value object to put
     * @create boolean value to specify whether a new child CompositeMap should be created
     * if one of the location can't be find in the CompositeMap
     * @return true if passed object is successfully put into the CompositeMap
     *         false if can't find corresponding place to put and parameter create is false
     */
    public boolean put( CompositeMap map, String key, Object value, boolean create){
        if( key == null || map == null) return false;
        int last_id = key.lastIndexOf(  separator_char);

        if( last_id<0){ 
            map.putObject(key,value, attribute_char);
            return true;
        }
        else{
        
            String prefix_key= key.substring(0,last_id);
            String last_key = key.substring(last_id+1, key.length());
            KeySplitter splitter = SplitKey( map, prefix_key, create);
            CompositeMap root = splitter.getRoot();
            if( root == null) return false;
            return root.putObject(last_key, value, this.attribute_char) != null;
        }
        
    }
    
    
    public Object get( CompositeMap map, String key){
        if( key == null || map == null) return null;
        //key = key.toLowerCase();        
        if( key.length()==0) return map.get(key);
        int id = key.indexOf(this.separator_char);
        if( id == 0)
            return get(map.getRoot(), key.substring(1));
        else if( id>0){
           KeySplitter splitter = SplitKey( map, key, false); 
           if( !splitter.exists() ) return map.get(key);        
           else  return splitter.getValue();
        }
        else 
            return map.getObject(key, this.attribute_char);
    }   
    
    /**
     * create a child CompositeMap in specified map.
     * if key start with '/', the root map will be taken as starting point
     */
    public CompositeMap createChild( CompositeMap map, String key){
    	if( key == null || map == null) return null;
    	//key = key.toLowerCase();
    	if("/".equals(key))
    	    return map.getRoot();
        int id = key.indexOf(this.separator_char);
        if( id == 0)
            return createChild(map.getRoot(), key.substring(1));
        else if( id>0){
           KeySplitter splitter = SplitKey( map, key, true); 
           return (CompositeMap)splitter.getValue();
        }
        else 
            return map.createChild(null,null,key);
        	
    }

}
