/*
 * Created on 2009-9-4 обнГ10:25:06
 * Author: Zhou Fan
 */
package uncertain.pkg;

import java.net.URL;

public interface IResourcePackage {
    
    public String   getBasePath();
    
    public String getName();
    
    public String getVersion();
    
    public URL getResource( String path );
    

}
