/*
 * Created on 2009-5-12
 */
package uncertain.pkg;

import uncertain.ocm.AbstractLocatableObject;

public class PackagePath extends AbstractLocatableObject{
    
    String      mPath;
    String      mClassPath;
    String      mRootClassPath;

    /**
     * @return the path
     */
    public String getPath() {
        return mPath;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        mPath = path;
    }

    public String getClassPath() {
        return mClassPath;
    }

    public void setClassPath(String classPath) {
        mClassPath = classPath;
    }

    public String getRootClassPath() {
        return mRootClassPath;
    }

    public void setRootClassPath(String rootClassPath) {
        mRootClassPath = rootClassPath;
    }

}
