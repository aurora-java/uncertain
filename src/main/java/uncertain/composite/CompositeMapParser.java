/*
 * CompositeMapHandle.java
 *
 * Created on 2002��1��5��, ����2:12
 */

package uncertain.composite;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Zhou Fan
 * @version
 */
public class CompositeMapParser extends DefaultHandler {

    /**
     * @param composite_loader
     */
    public CompositeMapParser(CompositeLoader composite_loader) {
        super();
        this.composite_loader = composite_loader;
    }

    /**
     * partly supports W3C XInclude specification <xi:include
     * xmlns:xi="http://www.w3.org/2001/XInclude" href="new_document.xml" />
     */
    public static final String INCLUDE_INSTRUCTION = "include";
    public static final String XINCLUDE_URI = "http://www.w3.org/2001/XInclude";
    public static final String KEY_HREF = "href";

    CompositeMap current_node = null;
    
    LinkedList node_stack = new LinkedList();
    
    // namespace url -> prefix mapping
    Map uri_mapping = new HashMap();
    
    // save all namespace url -> prefix mapping
    Map saved_uri_mapping;
    
    // prefix -> namespace mapping
    Map namespace_mapping = new HashMap();
    
    NameProcessor name_processor;
    
    CompositeLoader composite_loader;
    
    //Locator         last_locator;
    
    //boolean support_xinclude = false;
    
    // the default SAXParserFactory instance
	Locator locator;
    static SAXParserFactory parser_factory = SAXParserFactory.newInstance();
    static {
        try {
            parser_factory.setNamespaceAware(true);
            parser_factory.setValidating(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void push(CompositeMap node) {
        node_stack.addFirst(node);
    }

    CompositeMap pop() {
        CompositeMap node = (CompositeMap) node_stack.getFirst();
        node_stack.removeFirst();
        return node;
    }

    void addAttribs(CompositeMap node, Attributes attribs) {
        // Class nodeCls = node.getClass();
        for (int i = 0; i < attribs.getLength(); i++) {
            String attrib_name = attribs.getQName(i);
            /** @todo Add attribute namespace support */
            // String uri = attribs.getURI(i);
            if (name_processor != null)
                attrib_name = name_processor.getAttributeName(attrib_name);
            node.put(attrib_name, attribs.getValue(i));
        }
    }

    /** handles for SAX */

    public void startDocument() {
        current_node = null;
        //last_locator = null;
        node_stack.clear();
        uri_mapping.clear();
        name_processor = getCompositeLoader().getNameProcessor();
    }

    public void startElement(String namespaceURI, String localName,
            String rawName, Attributes atts) throws SAXException {

        // test if this is an xinclude instruction
        if ( composite_loader.getSupportXInclude() )
            if (localName.equals(INCLUDE_INSTRUCTION) && namespaceURI != null)
                if (namespaceURI.equals(XINCLUDE_URI)) {
                    String href_target = atts.getValue(KEY_HREF);
                    if (href_target == null)
                        throw new SAXException(
                                "No 'href' attribute set for an XInclude instruction");
                    CompositeMap included;
                    try {
                        included = getCompositeLoader().load(href_target);
                    } catch (IOException ex) {
                        throw new SAXException(ex);
                    }

                    if (current_node == null)
                        current_node = included;
                    else {
                        /*
                         * System.out.println(current_node.getClass());
                         * System.out.println(current_node.getName());
                         */
                        current_node.addChild(included);
                    }
                    return;
                }
        if (name_processor != null)
            localName = name_processor.getElementName(localName);
        CompositeMap node = null;
        if (getCompositeLoader() != null)
            node = getCompositeLoader().createCompositeMap(
                    (String) uri_mapping.get(namespaceURI), namespaceURI,
                    localName);
        else
            node = new CompositeMap((String) uri_mapping.get(namespaceURI),
                    namespaceURI, localName);
        node.getLocationNotNull().setStartPoint(locator.getLineNumber(), locator.getColumnNumber());
        addAttribs(node, atts);
        /*
        if(last_locator!=null)
            node.setLocator(last_locator);
        */
        if (current_node == null) {
            current_node = node;
        } else {
            current_node.addChild(node);
            push(current_node);
            current_node = node;
        }

    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException 
    {
        //last_locator = null;

        // test if this is an xinclude instruction
    	current_node.getLocationNotNull().setEndPoint(locator.getLineNumber(), locator.getColumnNumber());
        if ( getCompositeLoader().getSupportXInclude() )
            if (localName.equals(INCLUDE_INSTRUCTION) && uri != null)
                if (uri.equals(XINCLUDE_URI)) {
                    return;
                }

        if (node_stack.size() > 0)
            current_node = pop();
    }

    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        // do not save empty prefix mapping
        if(prefix==null)
            return;
        if(prefix.length()==0)
            return;
        uri_mapping.put(uri, prefix);
        namespace_mapping.put(prefix, uri);
        if( getCompositeLoader().getSaveNamespaceMapping()){
            if(saved_uri_mapping==null)
                saved_uri_mapping = new HashMap();
            saved_uri_mapping.put(uri, prefix);
        }
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        uri_mapping.remove(prefix);
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        if (ch == null)
            return;
        if (length == 0)
            return;
        if (current_node != null) {
            String t = current_node.getText();
            if (t != null)
                t += new String(ch, start, length);
            else
                t = new String(ch, start, length);
            current_node.setText(t);
        }
    }

    /** get root CompositeMap parsed */
    public CompositeMap getRoot() {
        return current_node;
    }

    /** get/set CompositeLoader */
    public void setCompositeLoader(CompositeLoader loader) {
        this.composite_loader = loader;
       // this.support_xinclude = loader.getSupportXInclude();
    }

    public CompositeLoader getCompositeLoader() {
        return this.composite_loader;
    }

    public CompositeMap parseStream(InputStream stream) throws SAXException,
            IOException {

        // using SAX parser shipped with JDK
        SAXParser parser = null;
        try {
            parser = parser_factory.newSAXParser();
        } catch (ParserConfigurationException ex) {
            throw new SAXException("error when creating SAXParser", ex);
        }
        parser.parse(stream, this);

        CompositeMap root = getRoot();
        if( getCompositeLoader().getSaveNamespaceMapping())
            root.setNamespaceMapping(saved_uri_mapping);
        return root;
    }

    public void clear() {
        current_node = null;
        if (node_stack != null)
            node_stack.clear();
        if (uri_mapping != null)
            uri_mapping.clear();
        name_processor = null;
        composite_loader = null;
    }
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
		super.setDocumentLocator(locator);
	}

}