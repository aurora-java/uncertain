/*
 * XMLOutputter.java
 *
 * Created on 2002年1月23日, 下午1:23
 */

package uncertain.composite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    /*
    public static String toXML( CompositeMap map){
        return default_inst.toXML(null, map);
    }
     */

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
    
    void getAttributeXML( Map map, StringBuffer attribs){
        
        Iterator it = map.keySet().iterator();
        while(it.hasNext()){
            Object key = it.next();
            Object value = map.get(key);
            if( value != null)
                attribs.append(" ").append(XMLWritter.getAttrib(key.toString(), value.toString() ) );
        }
    }
    
    void getChildXML( int level, List childs, StringBuffer buf, Map namespaces){
        if( childs == null) return;
        Iterator it = childs.iterator();
        while(it.hasNext()){
            CompositeMap map = (CompositeMap) it.next();
            buf.append( toXML(level, namespaces,map));
        }
    }
    
    Map addRef(Map namespaces, String uri, CompositeMap map){
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
    
    void subRef( Map map, String uri){
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
    public String toXML(CompositeMap map){
        return toXML( 0, null, map );
    }
    
    /** internal method
     * @param namespaces a Map of existing namespace
     * @return string of XML
     */
    String toXML( int level, Map namespaces, CompositeMap map ){
        
        
        StringBuffer attribs = new StringBuffer();
        StringBuffer childs = new StringBuffer();
        StringBuffer xml = new StringBuffer();
        String indent_str = getIndentString(level);
        boolean need_new_line_local = new_line;
        
        if(map.namespace_uri != null ){
            boolean uri_exists  = false;
            //Integer new_count;
            if( namespaces != null){
                uri_exists = (namespaces.get( map.namespace_uri) != null);
            }
            if( !uri_exists) {
                String xmlns = "xmlns";
                if(map.getPrefix() != null) xmlns = "xmlns:"+map.getPrefix();
                attribs.append(" ").append( XMLWritter.getAttrib(xmlns, map.namespace_uri));
            }
            namespaces = addRef( namespaces, map.namespace_uri, map);
        }
        
        getAttributeXML( map,attribs);
        if(map.getChilds()==null){
            if(map.getText()!=null){
                need_new_line_local = false;
                childs.append(CDATA_BEGIN).append(map.getText()).append(CDATA_END);
            }
        }
        else
            getChildXML( level+1, map.getChilds(), childs, namespaces);
        
        subRef(namespaces, map.namespace_uri);
        
        String elm = map.getRawName();
        
        xml.append(indent_str).append('<').append(elm).append(attribs);
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
