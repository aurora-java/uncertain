/*
 * Created on 2009-12-30 ����01:45:07
 * Author: Zhou Fan
 */
package uncertain.pkg;

import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;
import uncertain.exception.BuiltinExceptionFactory;

public class PackageConfig extends DynamicObject {

    public static final String KEY_PATH = "path";
    public static final String KEY_SCHEMA_FILES = "schema-files";
    public static final String KEY_RESOURCE_FILES = "resource-files";
    
    private String[] getFiles(String array_name, String attrib_for_path){
        CompositeMap schema_file_section = getObjectContext().getChild(array_name);
        if(schema_file_section==null)
            return null;
        Iterator it = schema_file_section.getChildIterator();
        if(it==null)
            return null;
        String[] paths = new String[schema_file_section.getChilds().size()];
        int i=0;
        while(it.hasNext()){
            CompositeMap item = (CompositeMap)it.next();
            String path = item.getString(attrib_for_path);
            if(path==null)
                throw BuiltinExceptionFactory.createAttributeMissing(item.asLocatable(), attrib_for_path);
            paths[i++] = path;
        }
        return paths;
    }

    public String[] getSchemaFiles(){
        return getFiles(KEY_SCHEMA_FILES, KEY_PATH);
    }
    
    public String[] getResourceFiles(){
        return getFiles(KEY_RESOURCE_FILES, KEY_PATH);
    }
    /*
    public String[] getSchemaFiles(){
        CompositeMap schema_file_section = getObjectContext().getChild(KEY_SCHEMA_FILES);
        if(schema_file_section==null)
            return null;
        Iterator it = schema_file_section.getChildIterator();
        if(it==null)
            return null;
        String[] paths = new String[schema_file_section.getChilds().size()];
        int i=0;
        while(it.hasNext()){
            CompositeMap item = (CompositeMap)it.next();
            String path = item.getString("path");
            if(path==null)
                throw new ConfigurationError("Must set 'path' property in <schema-file> section:"+item.toXML());
            paths[i++] = path;
        }
        return paths;
    }
    */
}
