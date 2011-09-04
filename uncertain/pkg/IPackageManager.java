/*
 * Created on 2011-8-28 下午10:40:25
 * $Id$
 */
package uncertain.pkg;

import java.io.IOException;

public interface IPackageManager {

    public ComponentPackage loadPackage(String path) throws IOException;

    public void addPackage(ComponentPackage pkg);

    public ComponentPackage getPackage(String name);

}