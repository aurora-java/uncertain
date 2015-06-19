/*
 * Created on 2011-6-1 ����09:58:48
 * $Id$
 */
package uncertain.util.resource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import uncertain.core.ILifeCycle;

public class SourceFileManager implements ISourceFileManager , ILifeCycle {

    static SourceFileManager DEFAULT_INSTANCE = new SourceFileManager();

    public static SourceFileManager getInstance() {
        return DEFAULT_INSTANCE;
    }

    public static final int INITIAL_SIZE = 6000;

    // File.getAbsolutePath() -> ISourceFile
    Map                 mSourceFileMap = new HashMap(INITIAL_SIZE);
    FileCheckThread     mCheckThread;
    long                mCheckInterval = 1000;
    
    boolean isContinue = true;
    boolean isStarted = false;

    public SourceFileManager() {
        startup();
    }

    public ISourceFile getSourceFile(String resource_url) {
        File file = new File(resource_url);
        if (!file.exists())
            return null;
        return getSourceFile(file);
    }

    public ISourceFile getSourceFile(File file) {
        ISourceFile source = (ISourceFile) mSourceFileMap.get(file
                .getAbsolutePath());
        return source;
    }

    public synchronized ISourceFile addSourceFile(File file) {
        ISourceFile source = getSourceFile(file);
        if (source != null)
            return source;
        source = new SourceFile(file);
        mSourceFileMap.put(file.getAbsolutePath(), source);
        return source;
    }

    public class FileCheckThread extends Thread {

        public FileCheckThread(String name){
            super(name);
        }

        public void run() {
            while (isContinue) {
                long time = System.currentTimeMillis();
                if (mSourceFileMap.size() > 0) {
                    Object[] entries = null;
                    
                    synchronized (mSourceFileMap) {
                        entries = mSourceFileMap.values().toArray();
                    }
                    for (int i = 0; i < entries.length && isContinue; i++) {
                        ISourceFile source = (ISourceFile) entries[i];
                        source.checkModified();
                    }

                } 
                time = System.currentTimeMillis() - time;
                if(mCheckInterval>time)
                try {
                    sleep(mCheckInterval-time);
                } catch (InterruptedException ex) {
                    return;
                }
           }

        }
        
        

    };
    
    public boolean startup(){
        if(isStarted)
            return true;
        isContinue = true;
        mCheckThread = new FileCheckThread("SourceFileManager.FileCheckThread");
        if(!mCheckThread.isAlive())
            mCheckThread.start();
        isStarted = true;
        return true;
    }
    
    public void shutdown(){
        isContinue = false;
        mCheckThread.interrupt();
        isStarted = false;
    }

}
