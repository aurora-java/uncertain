/*
 * Created on 2009-4-8
 */
package uncertain.logging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.ErrorManager;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IConfigurable;

public class JDKFileHandler extends Handler implements IConfigurable {

    String      pattern = null;
    
    int         limit = 50000;
    
    int         count = 1;
    
    boolean     append = true;

    FileHandler mFileHandler;

    public JDKFileHandler() {

    }

    void createFileHandler() throws IOException

    {
        SimpleFormatter sfm = new SimpleFormatter();
        mFileHandler = new FileHandler(pattern, limit, count, append);
        mFileHandler.setFormatter(sfm);
    }

    public void beginConfigure(CompositeMap config) {
        ;
    }

    public void endConfigure() {
        try {
            createFileHandler();
        } catch (Exception ex) {
            throw new RuntimeException("Can't create log file: "+pattern,ex);
        }
    }

    /* ================= getter/setters ============================= */

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @param pattern
     *            the pattern to set
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @param limit
     *            the limit to set
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count
     *            the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the append
     */
    public boolean isAppend() {
        return append;
    }

    /**
     * @param append
     *            the append to set
     */
    public void setAppend(boolean append) {
        this.append = append;
    }

   /* ================== FileHandler wrapper methods =================================== */
    public void close() throws SecurityException {
        mFileHandler.close();
    }

    public void flush() {
        mFileHandler.flush();
    }

    public String getEncoding() {
        return mFileHandler.getEncoding();
    }

    public ErrorManager getErrorManager() {
        return mFileHandler.getErrorManager();
    }

    public Filter getFilter() {
        return mFileHandler.getFilter();
    }

    public Formatter getFormatter() {
        return mFileHandler.getFormatter();
    }

    public synchronized Level getLevel() {
        return mFileHandler.getLevel();
    }

    public boolean isLoggable(LogRecord record) {
        return mFileHandler.isLoggable(record);
    }

    public void publish(LogRecord record) {
        mFileHandler.publish(record);
    }

    protected void reportError(String msg, Exception ex, int code) {
        super.reportError(msg, ex, code);
    }

    public void setEncoding(String encoding) throws SecurityException, UnsupportedEncodingException {        
        mFileHandler.setEncoding(encoding);
    }

    public void setErrorManager(ErrorManager em) {
        mFileHandler.setErrorManager(em);
    }

    public void setFilter(Filter newFilter) throws SecurityException {
        mFileHandler.setFilter(newFilter);
    }

    public void setFormatter(Formatter newFormatter) throws SecurityException {
        mFileHandler.setFormatter(newFormatter);
    }

    public synchronized void setLevel(Level newLevel) throws SecurityException {
        mFileHandler.setLevel(newLevel);
    }

}
