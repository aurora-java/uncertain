/*
 * Created on 2009-4-9
 */
package uncertain.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.Date;

public class DefaultFormatter extends Formatter {
    
    static final String LINE_SEPARATOR = System.getProperty("line.separator");
    static final String DEFAULT_MESSAGE_FORMAT = "{0,date} {0,time} {1} [{2}] {3}"; 
    String              mFormatMask = DEFAULT_MESSAGE_FORMAT;
    MessageFormat       mMessageFormat;

    public DefaultFormatter() {
        mMessageFormat = new MessageFormat(mFormatMask);
    }

    public String format(LogRecord record) {
        Object[] args = new Object[4];
        StringBuffer buf = new StringBuffer();
        args[0] = new Date(record.getMillis());
        args[1] = record.getLoggerName()==null? "": "[" + record.getLoggerName() + "]";
        args[2] = record.getLevel().getName();
        args[3] = record.getMessage();
        mMessageFormat.format(args, buf, null);
        buf.append(LINE_SEPARATOR);    
        if(record.getThrown()!=null){
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                buf.append(sw.toString());
            } catch (Exception ex) {
            }            
        }
        return buf.toString();
    }

}
