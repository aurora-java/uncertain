/*
 * Created on 2011-5-30 ÏÂÎç11:04:08
 * $Id$
 */
package uncertain.util.resource;

import java.io.File;

/**
 *  Define interface to manager a source file
 */
public interface ISourceFile {
    
    /** Last modified time of the source file */
    public long getLastModified();
    
    /** A user defined version number of source file */
    public String getVersion();
    
    /** URL of the source file */
    public String getURL();
    
    /** Actual File object of the source file */ 
    public File getFile(); 
    
    /** Add a listener so that can get notify when source file need update */
    public void addUpdateListener( ISourceFileUpdateListener listener );
    
    /** Check if source file is modified. If source file modified, latest file info 
     * will be loaded, and all listener will be notified. 
     * @return true if the source file is modified
     * false if not modified
     */
    public boolean checkModified();

}
