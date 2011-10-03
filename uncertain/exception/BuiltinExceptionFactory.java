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
    
    /** Can't load specified resource file */
    public static ConfigurationFileException createResourceLoadException( ILocatable locatable, String resource_name, Throwable cause ){
        return new ConfigurationFileException("uncertain.exception.cannot_load_resource", new Object[]{resource_name}, cause, locatable);        
    }
    
    /** value not number */
    public static ConfigurationFileException createValueNotNumberException( ILocatable locatable, String value ){
        return new ConfigurationFileException("uncertain.exception.value_not_number", new Object[]{value}, locatable );
    }
    
    public static GeneralException createInstanceNotFoundException( ILocatable locatable, Class required_class, String who_need_this_class ){
        return new GeneralException("uncertain.exception.instance_not_found", new Object[]{required_class.getName(), who_need_this_class}, (Throwable)null, locatable );
    }

    public static GeneralException createInstanceNotFoundException( ILocatable locatable, Class required_class ){
        return new GeneralException("uncertain.exception.instance_not_found", new Object[]{required_class.getName(), locatable.getClass().getName()}, (Throwable)null, locatable );
    }
    
    public static ConfigurationFileException createClassNotFoundException( ILocatable locatable, String cls_name ){
        return new ConfigurationFileException("uncertain.exception.classnotfoundexception", new Object[]{cls_name}, null, locatable);
    }

    public static ConfigurationFileException createConflictAttributesExcepiton( ILocatable locatable, String attribs){
    	return new ConfigurationFileException("uncertain.exception.validation.conflict_attributes", new Object[]{attribs}, null, locatable);
    }
    
    public static ConfigurationFileException createInvalidPathException(ILocatable locatable, String path){
        return new ConfigurationFileException("uncertain.exception.invalid_path", new Object[]{path}, null, locatable);
    }
    
    public static ConfigurationFileException createCannotCreateInstanceFromConfigException(ILocatable locatable, String config_path ){
        return new ConfigurationFileException("uncertain.exception.cannot_create_instance_from_config", new Object[]{config_path}, null, locatable);
    }
    
    public static GeneralException createInstanceDependencyNotMeetException(ILocatable locatable, String object_list ){
        return new GeneralException("uncertain.exception.instance_dependency_not_meet", new Object[]{object_list}, null, locatable);
    }
    
    public static GeneralException createInstanceTypeWrongException( String source, Class expected_type, Class actual_type ){
        return new GeneralException("uncertain.exception.instance_type_wrong", new Object[]{source, expected_type, actual_type}, (Throwable)null );
    }

    public static GeneralException createXmlGrammarException( String source, Throwable cause ){
        return new GeneralException("uncertain.exception.xml_grammar_exception", new Object[]{source}, cause );
    }
    
    public static GeneralException createRequiredFileNotFound( String source ){
        return new GeneralException("uncertain.exception.required_file_not_found", new Object[]{source}, (Throwable)null );
    }
    
    public static GeneralException createInstanceStartError( Object instance, String config_file, Throwable cause ){
        return new GeneralException("uncertain.exception.engine.instance_start_error", new Object[]{instance, config_file}, cause );
    }
    
    
}
