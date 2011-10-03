/*
 * Created on 2011-9-29 下午05:22:19
 * $Id$
 */
package uncertain.pkg;

import java.io.File;

public interface IInstanceCreationListener {
    
    public void onInstanceCreate( Object instance, File config_file );

}
