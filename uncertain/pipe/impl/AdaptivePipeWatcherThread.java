/*
 * Created on 2014年12月27日 下午3:18:02
 * $Id$
 */
package uncertain.pipe.impl;

public class AdaptivePipeWatcherThread extends Thread {

    AdaptivePipe pipe;
    long lastBusyTime;

    public AdaptivePipeWatcherThread(AdaptivePipe pipe) {
        super(pipe.getId() + ".watcher");
        this.pipe = pipe;
        lastBusyTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (!interrupted() && pipe.running && !pipe.shutdownInProcess) {
            int size = pipe.getQueueSize();
/*
            int size = 0;
            try {

            } catch (Throwable thr) {
                Throwable t;
                for (t = thr.getCause(); t.getCause() != null; t = t.getCause())
                    ;
                t.printStackTrace();
                return;
            }
*/            
            if (size < pipe.getReleaseCount()) {
                if (System.currentTimeMillis() - lastBusyTime > pipe
                        .getIdleTime()) {
                    pipe.release();
                    lastBusyTime = System.currentTimeMillis();
                }
            } else {
                lastBusyTime = System.currentTimeMillis();
                if (size > pipe.getExpandCount())
                    pipe.expand();
            }
            pipe.overheat = size > pipe.getMaxTaskCount();

            try {
                sleep(100);
            } catch (InterruptedException ex) {
                continue;
            }
        }
    }

}
