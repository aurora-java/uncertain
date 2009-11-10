/*
 * XMLOutputter.java
 *
 * Created on 2002��1��23��, ����1:23
 */

package uncertain.composite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uncertain.util.XMLWritter;

/**
 *
 * @author  Administrator
 * @version 
 */
public class XMLOutputter {
    
    public static final String CDATA_END = "]]>";
    public static final String CDATA_BEGIN = "<![CDATA[";
    public static final String DEFAULT_INDENT = "    ";
    boolean new_line;
    String    indent;
    
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
 
    public static XMLOutputter default_inst = new XMLOutputter(  DEFAULT_INDENT, true);
    
    public static XMLOutputter defaultInstance(){
        return default_inst;
    }
   
    static class PrefixMappingHolder implements IterationHandle {
        
        static final String  ns_prefix = "ns";
        int     sequence = 1;
        Set     prefix_set = new HashSet();
        Map     prefix_map = new HashMap();
        
        public String getUniquePrefix(){
            String prefix = ns_prefix + sequence++;
            for( ; prefix_set.contains(prefix); prefix = ns_prefix + sequence++);
            return prefix;
        }
        
        public int process( CompositeMap map){
            String url = map.getNamespaceURI();
            if(url!=null){
                if(!prefix_map.containsKey(url)){
                    String prefix = map.getPrefix();
                    if(prefix==null)
                        prefix = getUniquePrefix();
                    if(prefix_set.contains(prefix))
                        prefix = getUniquePrefix();
                    prefix_set.add(prefix);
                    prefix_map.put(url, prefix);
                }
            }
            return IterationHandle.IT_CONTINUE;
        }
        
        public Map getPrefixMapping(){
            return prefix_map;
        }
    };
    
    /** Creates new XMLOutputter */
    public XMLOutputter(String _indent, boolean _new_line) {
        indent = _indent ;
        new_line = _new_line;
    }
    
    String getIndentString( int level){
        StringBuffer pre_indent = new StringBuffer();
        if( indent != null) for (int i=0; i<level; i++) pre_indent.append(indent);
        return pre_indent.toString();      
    }
    
    static void getAttributeXML( Map map, StringBuffer attribs){
        
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if( value != null)
                attribs.append(" ").append(XMLWritter.getAttrib(key.toString(), value.toString() ) );
        }
    }
    
    void getChildXML( int level, List childs, StringBuffer buf, Map namespaces, Map prefix_mapping){
        if( childs == null) return;
        Iterator it = childs.iterator();
        while(it.hasNext()){
            CompositeMap map = (CompositeMap) it.next();
            buf.append( toXMLWithPrefixMapping(level, map, namespaces, prefix_mapping));
        }
    }
    
    static Map addRef(Map namespaces, String uri, CompositeMap map){
        if( uri == null) return namespaces;
        if( namespaces == null) namespaces = new HashMap();
        Integer new_count;
        Integer count = (Integer)namespaces.get(map.getNamespaceURI() );
        if( count != null){
            new_count = new Integer( count.intValue() + 1);
        }
        else new_count = new Integer(1);
        namespaces.put(map.namespace_uri, new_count);
        return namespaces;
    }
    
    static void subRef( Map map, String uri){
        if(uri == null) return;
        Integer count = (Integer)map.get(uri);
        if( count != null){
            int value = count.intValue()-1;
            if( value<=0) map.remove(uri);
            else map.put(uri, new Integer( value));
        }
    }
    
    /** return XML form of map CompositeMap, object stored in Map will be added as attributes
     * by calling Object.toString(), childs will be added as sub elements
     *
     * @return string of XML
     */
    
    public String toXML( CompositeMap map ){
        return toXML( map, false);
    }
    
    public String toXML(CompositeMap map, boolean namespace_in_root ){
        if(namespace_in_root){
            PrefixMappingHolder holder = new PrefixMappingHolder();
            map.iterate(holder, true);
            Map prefix_mapping = holder.getPrefixMapping();
            return toXMLWithPrefixMapping(0, map, null, prefix_mapping);
        }else
            return toXMLWithPrefixMapping( 0, map, null, null );
    }
    
    /**
     * Append xml namespace declare to StringBuffer
     * @param buf Target StringBuffer
     * @param prefix_mapping namespace url -> prefix
     * @return processed buf 
     */
    static StringBuffer appendNamespace( StringBuffer buf, Map prefix_mapping ){
        Iterator it = prefix_mapping.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            String namespace = (String)entry.getKey();
            String prefix = (String)entry.getValue();
            buf.append(" xmlns:").append(prefix).append("=\"").append(namespace).append("\"");
        }
        return buf;
    }
    
    /** internal method
     * @param namespaces a Map of existing namespace: namespace -> Integer of ref count
     * @param prefix_mapping a Map of namespace -> prefix mapping
     * @return string of XML
     */
    String toXMLWithPrefixMapping( int level, CompositeMap map, Map namespaces, Map prefix_mapping ){
        
        
        StringBuffer attribs = new StringBuffer();
        StringBuffer childs = new StringBuffer();
        StringBuffer xml = new StringBuffer();
        String indent_str = getIndentString(level);
        String namespace_uri = map.getNamespaceURI();
        StringBuffer xmlns_declare = null;
        
        boolean need_new_line_local = new_line;
        
        if(prefix_mapping==null){
            if(namespace_uri != null ){
                boolean uri_exists  = false;
                if( namespaces != null){
                    uri_exists = (namespaces.get( namespace_uri) != null);
                }
                if( !uri_exists) {
                    String xmlns = "xmlns";
                    if(map.getPrefix() != null) xmlns = "xmlns:"+map.getPrefix();
                    attribs.append(" ").append( XMLWritter.getAttrib(xmlns, namespace_uri));
                }
                namespaces = addRef( namespaces, namespace_uri, map);
            }
        }
        
        getAttributeXML( map,attribs);
        if(map.getChilds()==null){
            if(map.getText()!=null){
                need_new_line_local = false;
                childs.append(CDATA_BEGIN).append(map.getText()).append(CDATA_END);
            }
        }
        else
            getChildXML( level+1, map.getChilds(), childs, namespaces, prefix_mapping);
        
        if(prefix_mapping==null){
            subRef(namespaces, namespace_uri);
        }
        
        String elm = null;
        if(prefix_mapping==null){
            elm = map.getRawName();
        }else{
            elm = map.getName();
            if(namespace_uri!=null){
                String prefix = (String)prefix_mapping.get(namespace_uri);
                elm = prefix + ":" + elm;
            }
            if(level==0){
                xmlns_declare = new StringBuffer();
                appendNamespace(xmlns_declare, prefix_mapping);
            }
        }
        
        xml.append(indent_str).append('<').append(elm);
        if(xmlns_declare!=null)
            xml.append(xmlns_declare);
        xml.append(attribs);
        if( childs.length()>0){ 
            xml.append('>');
            if( need_new_line_local) xml.append( LINE_SEPARATOR);
            xml.append(childs);
            if( need_new_line_local)
                xml.append(indent_str);
            xml.append(XMLWritter.endTag(elm));
        }
        else xml.append("/>");
        if( new_line) xml.append( LINE_SEPARATOR);
        return xml.toString();        
    }
    
    public static void saveToFile( File target_file, CompositeMap map, String encoding )
        throws IOException
    {
        FileOutputStream os = null;
        try{
            os = new FileOutputStream(target_file);
            String xml_decl = "<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\n";
            os.write(xml_decl.getBytes());
            os.write(map.toXML().getBytes(encoding));
            os.flush();
        }finally{
            if(os!=null)
                os.close();
        }
    }

}
