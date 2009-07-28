/*
 * CompositeMapHandle.java
 *
 * Created on 2002年1月5日, 上午2:12
 */

package uncertain.composite;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class CompositeMapParser extends DefaultHandler {
	
	/** partly supports W3C XInclude specification 
	 *  <xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="new_document.xml" />
	 */
	public static final String INCLUDE_INSTRUCTION = "include";	
	public static final String XINCLUDE_URI = "http://www.w3.org/2001/XInclude";
	public static final String KEY_HREF = "href";
 
    CompositeMap                   	current_node = null;
    LinkedList                         node_stack = new LinkedList();
    // namespace url -> prefix mapping
    HashMap                          uri_mapping = new HashMap();
	NameProcessor					name_processor;
    CompositeLoader                composite_loader;
    boolean						support_xinclude = false;
    // the default SAXParserFactory instance
    static SAXParserFactory                parser_factory = SAXParserFactory.newInstance();
    static {
        try{
            parser_factory.setNamespaceAware(true);
            parser_factory.setValidating(false);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    void push( CompositeMap node){
        node_stack.addFirst(node);
    }
    
    CompositeMap pop(){
        CompositeMap node = (CompositeMap)node_stack.getFirst();
        node_stack.removeFirst();
        return node;
    }
    
    void addAttribs( CompositeMap node, Attributes attribs){
        //Class nodeCls = node.getClass();
        for( int i=0; i<attribs.getLength(); i++){
        		 String attrib_name = attribs.getQName(i);
        		 /** @todo Add attribute namespace support */  
        		 //String uri = attribs.getURI(i);
        		 if( name_processor!=null) attrib_name = name_processor.getAttributeName(attrib_name);
                 node.put( attrib_name, attribs.getValue(i) );        
        }
    }
    

/** handles for SAX */
 
    public void startDocument(){
       current_node = null;
       node_stack.clear();
       uri_mapping.clear();
    }
    
    public void startElement(String namespaceURI, String localName,
                             String rawName, Attributes atts)
	throws SAXException  {
		
            // test if this is an xinclude instruction
            if( this.support_xinclude)
            	if( localName.equals( INCLUDE_INSTRUCTION ) && namespaceURI !=null)
            		if( namespaceURI.equals(XINCLUDE_URI))
            		{
            			String href_target = atts.getValue(KEY_HREF);
            			if( href_target == null) throw new SAXException("No 'href' attribute set for an XInclude instruction");
            			CompositeMap included;
            			try{
            				included = getCompositeLoader().load(href_target);
            			} catch(IOException ex){
            				throw new SAXException(ex);
            			}

            			if( current_node == null)
            			    current_node = included;
            			else{
 /*
            			    System.out.println(current_node.getClass());
                			System.out.println(current_node.getName());
  */
            			    current_node.addChild(included);     
            			}
            			return;
            		}
            if( name_processor!=null) localName = name_processor.getElementName(localName);
            CompositeMap node = null;
            if(getCompositeLoader()!=null)
                node = getCompositeLoader().createCompositeMap((String)uri_mapping.get(namespaceURI), namespaceURI, localName);
            else    
                node = new CompositeMap((String)uri_mapping.get(namespaceURI), namespaceURI, localName);         
            addAttribs( node, atts);
            
            if( current_node == null){
               current_node = node;
            }else{
               current_node.addChild( node);
               push( current_node);
               current_node = node;
            }
            
    }
    
    public void endElement(String uri, String localName, String qName)
          throws SAXException{   
          	
            // test if this is an xinclude instruction
            if( this.support_xinclude)
            	if( localName.equals( INCLUDE_INSTRUCTION ) && uri !=null)
            		if( uri.equals(XINCLUDE_URI))
            		{
            			return;
            		}
          	
             if( node_stack.size()>0)
              current_node = pop();              
    }

    public void startPrefixMapping(String prefix, String uri)
        throws SAXException {
            uri_mapping.put(uri,prefix);
    }
    
    public void endPrefixMapping(String prefix) throws SAXException {
        uri_mapping.remove(prefix);
    }
    
    public void characters (char ch[], int start, int length)
	throws SAXException
    {
        if(ch==null) return;
        if(start==length) return;
		if( current_node !=null ){
		    String t = current_node.getText();
		    if(t!=null) t+=new String(ch,start,length);
		    else t=new String(ch,start,length);
		    current_node.setText(t);
		}
    }    


/** end handles */


	public CompositeMapParser(){
	}
	
	public CompositeMapParser( CompositeMapParser prototype ){
	    this();
	    copySettings(prototype);
	}
	
	/*
	public CompositeMapParser( CompositeLoader loader){
		if( loader == null) return;
		setCompositeLoader(loader);
	}
	*/
	
	/** set a new INameProcessor */
	public void setNameProcessor(NameProcessor processor){
		this.name_processor = processor;
	}

    /** get root CompositeMap parsed */    
    public CompositeMap getRoot(){
           return current_node;
    }
    
    /** get/set CompositeLoader */
    public void setCompositeLoader( CompositeLoader loader){
    	this.composite_loader =  loader;
		this.support_xinclude =  loader.getSupportXInclude();
    }
    
    public CompositeLoader getCompositeLoader(){
    	return this.composite_loader;
    }

    public CompositeMap parseStream(InputStream stream ) 
        throws SAXException, IOException 
    {
        
        // using SAX parser shipped with JDK
        SAXParser parser = null;
        try{
            parser = parser_factory.newSAXParser();
        } catch(ParserConfigurationException ex){
            throw new SAXException("error when creating SAXParser", ex);
        }
        parser.parse(stream, this);
        
        /*
        // Directly use xerces
        XMLReader xmlreader = new org.apache.xerces.parsers.SAXParser();
	    xmlreader.setContentHandler(this);
	    xmlreader.parse(new InputSource(stream));
        */
        
	    return getRoot();
    }
    
    public void clear(){
        current_node = null;
        if(node_stack!=null)
            node_stack.clear();
        if(uri_mapping!=null)
            uri_mapping.clear();
        name_processor = null;
        composite_loader = null;        
    }
    
    public static CompositeMapParser createInstance(CompositeLoader loader, NameProcessor name_processor){
        CompositeMapParser parser =  new CompositeMapParser();
        if(loader!=null)
            parser.setCompositeLoader(loader);
        if(name_processor!=null)
            parser.setNameProcessor(name_processor);
    	return parser;
    }
    
    public void copySettings( CompositeMapParser next ){
        this.name_processor = next.name_processor;
        this.support_xinclude = next.support_xinclude;
        this.composite_loader = next.composite_loader;
    }

    public static CompositeMap parse( InputStream stream, CompositeLoader loader, NameProcessor processor ) 
    throws SAXException, IOException {
    	CompositeMapParser parser = null;
        try{
            parser = createInstance(loader, processor);
            return parser.parseStream(stream);
        }finally{
            if(parser!=null) parser.clear();
        }        
    }
    
    public static CompositeMap parse( InputStream stream, CompositeLoader loader) 
    throws SAXException, IOException {
		return parse( stream, loader, null);
    }
    
    public static CompositeMap parse( InputStream stream, NameProcessor processor ) 
    throws SAXException, IOException {
    	return parse(stream, null, processor);
    }
    
    public static CompositeMap parse( InputStream stream) throws SAXException, IOException {
    	return parse(stream, null, null);
    }
    
    /**
     * @todo Add element location info
     */
    /*
    public void setDocumentLocator(Locator locator){
        super.setDocumentLocator(locator);
    }
    */


}