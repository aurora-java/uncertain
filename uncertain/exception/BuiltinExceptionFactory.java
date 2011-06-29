/*
 * Created on 2011-6-24 下午03:19:36
 * $Id$
 */
package uncertain.exception;

import uncertain.util.resource.ILocatable;

public class BuiltinExceptionFactory {

    public static ConfigurationFileException createAttributeMissing( ILocatable locatable, String attrib_name ){
        return new ConfigurationFileException("uncertain.exception.validation.attribute_missing1", new Object[]{attrib_name}, null, locatable);
    }

    public static ConfigurationFileException createOneAttributeMissing( ILocatable locatable, String attribs ){
        return new ConfigurationFileException("uncertain.exception.validation.attribute_missing2", new Object[]{attribs}, null, locatable);
    }

}
