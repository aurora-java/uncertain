/*
 * Created on 2009-4-20
 */
package uncertain.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


import uncertain.composite.CompositeMap;
import uncertain.ocm.IConfigurable;
import uncertain.util.FilePatternFilter;

public class BasicFileHandler extends Handler implements ILogPathSettable, IConfigurable {
    
    FileWriter          mWriter;    
    String              mBasePath;
    String              mLogFilePrefix = "logfile";
    String              mLogFilePostfix = "log";
    String              mDateFormat = "yyyy-MM-dd";
    SimpleDateFormat    mSimpleDateFormat;
    File                mBasePathFile;
    boolean             mSeparateByDate = true;
    boolean             mAppend = true;
    String              mCurrentLogFileName;
        
    public void beginConfigure(CompositeMap config){
        
    }
    
    /**
     * This method is called after this instance has been populated from container
     */
    public void endConfigure(){
        if( mSeparateByDate ){
            if( mSimpleDateFormat == null )
                mSimpleDateFormat = new SimpleDateFormat(mDateFormat);
        }
    }
    
    void handleException( Exception thrown ){
        thrown.printStackTrace();
        ErrorManager em = getErrorManager();
        if(em!=null)
            em.error("Error when writting log file", thrown, -1);
        else
            thrown.printStackTrace();
    }
    
    String getLogFileName(){

        StringBuffer buf = new StringBuffer();
        buf.append(mLogFilePrefix);
        if(mSeparateByDate){
            if(mSimpleDateFormat==null) mSimpleDateFormat = new SimpleDateFormat(mDateFormat);
            buf.append(mSimpleDateFormat.format(new Date()));
        }
        buf.append('.');
        buf.append(mLogFilePostfix);
        return buf.toString();
    }

    public void close() throws SecurityException {
        if(mWriter!=null)
            try{
                mWriter.close();
            }catch(IOException ex){
                handleException(ex);
            }
    }

    public void flush() {
        if(mWriter!=null)
            try{
                mWriter.flush();
            }catch(IOException ex){
                handleException(ex);
            }
    }

    public synchronized void publish(LogRecord record) 
    {
       if( !super.isLoggable(record)) return;
       Formatter f = getFormatter();
       String content = f.format(record);
       try{
           checkLogFile();           
           mWriter.write(content);
           flush();
       }catch(IOException ex){
           handleException(ex);
       }
    }
    
    public void setLogPath( String path ){
        //System.out.println("Setting path to "+path);
        mBasePath = path;
        mBasePathFile = new File(path);
        if( !mBasePathFile.exists())
            throw new IllegalArgumentException("Invalid base file path:"+mBasePath);
    }
    
    public String getLogPath(){
        return mBasePath;
    }
    
    public File getCurrentLogFile(){
        if(mBasePathFile==null)
            return null;
        File logFile = new File( mBasePathFile, getLogFileName() );
        return logFile;
    }
    
    public String[] getFileList(){
        String pattern = mLogFilePrefix + ".*."+mLogFilePostfix;
        FilePatternFilter fpf = new FilePatternFilter(pattern); 
        return mBasePathFile.list(fpf);
    }
    
    public void prepareLogFile()
        throws IOException
    {
        close();
        mCurrentLogFileName = getLogFileName();
        File logFile = new File( mBasePathFile, mCurrentLogFileName );
        if(!logFile.exists() && mCurrentLogFileName.lastIndexOf("/") != -1){
        	String path = mCurrentLogFileName.substring(0,mCurrentLogFileName.lastIndexOf("/"));
        	File pathFile = new File( mBasePathFile, path);
        	if(!pathFile.exists())pathFile.mkdirs();
        }
        mWriter = new FileWriter(logFile, mAppend);
    }
    
    void checkLogFile() throws IOException {
        if( mWriter==null)
            prepareLogFile();
        if( !mSeparateByDate) return;
        String file = getLogFileName();
        if(!file.equals(mCurrentLogFileName))
           prepareLogFile();
    }

    /**
     * @return the logFilePrefix
     */
    public String getLogFilePrefix() {
        return mLogFilePrefix;
    }

    /**
     * @param logFilePrefix the logFilePrefix to set
     */
    public void setLogFilePrefix(String logFilePrefix) {
        mLogFilePrefix = logFilePrefix;
    }

    /**
     * @return the separateByDate
     */
    public boolean getSeparateByDate() {
        return mSeparateByDate;
    }

    /**
     * @param separateByDate the separateByDate to set
     */
    public void setSeparateByDate(boolean separateByDate) {
        mSeparateByDate = separateByDate;
    }

    /**
     * @return the logFilePostfix
     */
    public String getLogFilePostfix() {
        return mLogFilePostfix;
    }

    /**
     * @param logFilePostfix the logFilePostfix to set
     */
    public void setLogFilePostfix(String logFilePostfix) {
        mLogFilePostfix = logFilePostfix;
    }

    /**
     * @return the dateFormat
     */
    public String getDateFormat() {
        return mDateFormat;
    }

    /**
     * @param dateFormat the dateFormat to set
     */
    public void setDateFormat(String dateFormat) {
        mDateFormat = dateFormat;
    }

    /**
     * @return the append
     */
    public boolean getAppend() {
        return mAppend;
    }

    /**
     * @param append the append to set
     */
    public void setAppend(boolean append) {
        mAppend = append;
    }

}
