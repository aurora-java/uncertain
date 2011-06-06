/*
 * Created on 2011-6-1 обнГ03:38:50
 * $Id$
 */
package uncertain.util.resource;

import java.io.File;

public interface ISourceFileManager {
    
    public ISourceFile    getSourceFile( String resource_url );
    
    /** Get ISourceFile from a File instance */
    public ISourceFile    getSourceFile( File file );
    
    /** Check if there is an ISourceFile instance associated with specified File.
     *  If not found, create a new one
     * @param file
     * @return
     */
    public ISourceFile addSourceFile( File file);
    

}
