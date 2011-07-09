/*
 * Created on 2011-6-24 下午03:19:36
 * $Id$
 */
package uncertain.exception;

import uncertain.composite.CompositeMap;
import uncertain.util.resource.ILocatable;

public class BuiltinExceptionFactory {

    public static ConfigurationFileException createAttributeMissing( ILocatable locatable, String attrib_name ){
        return new ConfigurationFileException("uncertain.exception.validation.attribute_missing", new Object[]{attrib_name}, null, locatable);
    }

    public static ConfigurationFileException createOneAttributeMissing( ILocatable locatable, String attribs ){
        return new ConfigurationFileException("uncertain.exception.validation.one_of_attribute_missing", new Object[]{attribs}, null, locatable);
    }
    
    public static ConfigurationFileException createUnknownChild( CompositeMap config ){
        return new ConfigurationFileException("uncertain.exception.validation.unknown_child", new Object[]{config.toXML()}, null, config.asLocatable());
    }
    
    public static ConfigurationFileException createDataFromXPathIsNull(ILocatable locatable, String path){
        return new ConfigurationFileException("uncertain.exception.data_from_xpath_is_null", new Object[]{path}, null, locatable );
    }
    
    public static ConfigurationFileException createNodeMissing( ILocatable locatable, String node_name ){
        return new ConfigurationFileException("uncertain.exception.validation.node_missing", new Object[]{node_name}, null, locatable);
    }
    
    public static ConfigurationFileException createCDATAMissing( ILocatable locatable, String node_name ){
        return new ConfigurationFileException("uncertain.exception.validation.cdata_missing", new Object[]{node_name}, null, locatable);
    }
    
    public static ConfigurationFileException createDataTypeUnknown( ILocatable locatable, String error_data_type){
        return new ConfigurationFileException("uncertain.exception.unknown_data_type", new Object[]{error_data_type}, null, locatable);
    }
    
    public static ConfigurationFileException createChildDuplicate( ILocatable locatable, String tag_name, String attribute_name, String attribute_value ){
        return new ConfigurationFileException("uncertain.exception.duplicate_named_node", new Object[]{tag_name, attribute_name, attribute_value}, null, locatable);        
    }

    /** Can't find a node with specified attribute value */
    public static ConfigurationFileException createUnknownNodeWithName( ILocatable locatable, String tag_name, String attribute_name, String attribute_value ){
        return new ConfigurationFileException("uncertain.exception.cannot_find_named_node", new Object[]{tag_name, attribute_name, attribute_value}, null, locatable);        
    }
    
}
