/*
 * Created on 2011-6-1 ÏÂÎç03:33:31
 * $Id$
 */
package uncertain.util.resource;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SourceFile implements ISourceFile {

    long lastModified;
    String url;
    File sourceFile;
    List listenerList;

    /**
     * @param file
     */
    public SourceFile(File file) {
        this.sourceFile = file;
        this.lastModified = file.lastModified();
        this.sourceFile = file;
        listenerList = new LinkedList();
    }

    /*
     * public SourceFile(long lastModified, String url) { super();
     * this.lastModified = lastModified; this.url = url; }
     */

    public long getLastModified() {
        return lastModified;
    }

    public String getVersion() {
        return Long.toString(lastModified);
    }

    public String getURL() {
        return url;
    }

    public File getFile() {
        return sourceFile;
    }

    public void addUpdateListener(
            ISourceFileUpdateListener listener) {
        listenerList.add(listener);
    }

    public boolean checkModified() {
        long new_modified = sourceFile.lastModified();
        if (new_modified != lastModified) {
            //System.out.println(sourceFile.getPath()+" modified, reloading");
            lastModified = new_modified;
            if (listenerList.size()>0) {
                synchronized(listenerList){
                    Object[] listeners = listenerList.toArray();
                    for (int i = 0; i < listeners.length; i++) {
                        try{
                            ((ISourceFileUpdateListener) listeners[i]).onUpdate(this);
                        }catch(Throwable thr){
                            thr.printStackTrace();
                        }
                    }
                    listenerList.clear();
                }
            }
            
            return true;
        }
        return false;
    }

}
