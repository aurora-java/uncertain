/*
 * Created on 2007-8-2
 */
package uncertain.document;

import java.io.File;

public interface IDocumentFactory {
    
    public File getDocument( String name );
    
    public File getByClassPath( String class_path );

}
