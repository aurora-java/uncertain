/*
 * Created on 2009-12-30 ÏÂÎç01:45:07
 * Author: Zhou Fan
 */
package uncertain.pkg;

import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;

public class PackageConfig extends DynamicObject {

    public static final String KEY_SCHEMA_FILES = "schema-files";

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
}
