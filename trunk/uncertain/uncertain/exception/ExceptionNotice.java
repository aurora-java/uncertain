package uncertain.exception;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.core.UncertainEngine;

public class ExceptionNotice {
	UncertainEngine  uncertainEngine;
	List exceptionListener;
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public ExceptionNotice(UncertainEngine uncertainEngine){
		exceptionListener = new LinkedList();
		this.uncertainEngine = uncertainEngine;
	}
	public void addListener(IExceptionListener listener){
		if(listener != null)
			exceptionListener.add(listener);
	}
	public void removeListener(IExceptionListener listener){
		if(listener != null){
			if(exceptionListener.contains(listener)){
				exceptionListener.remove(listener);
			}
		}
	}
	public List getListeners(){
		return exceptionListener;
	}
	public void notice(Throwable e){
		if(e == null)
			return;
		IExceptionListener listener = null;
		for(Iterator it = exceptionListener.iterator();it.hasNext();){
			try{
				listener = ((IExceptionListener)it.next());
				listener.onException(e);
			}catch (Exception ex) {
				uncertainEngine.logException("Error when notice exception:"+ listener.getClass().getName(), ex);
			}
		}
	}
	public static void getPrintStackTrace(StringBuffer s,Throwable ex) {
			s.append(ex).append(LINE_SEPARATOR);
            StackTraceElement[] trace = ex.getStackTrace();
            for (int i=0; i < trace.length; i++)
                s.append("\tat " + trace[i]).append(LINE_SEPARATOR);

            Throwable ourCause = ex.getCause();
            if (ourCause != null)
                printStackTraceAsCause(s,ourCause,trace);
    }

    /**
     * Print our stack trace as a cause for the specified stack trace.
     */
    private static void printStackTraceAsCause(StringBuffer s,Throwable cause,
                                        StackTraceElement[] causedTrace)
    {
        // assert Thread.holdsLock(s);

        // Compute number of frames in common between this and caused
        StackTraceElement[] trace = cause.getStackTrace();
        int m = trace.length-1, n = causedTrace.length-1;
        while (m >= 0 && n >=0 && trace[m].equals(causedTrace[n])) {
            m--; n--;
        }
        int framesInCommon = trace.length - 1 - m;

        s.append("Caused by: " + cause).append(LINE_SEPARATOR);
        for (int i=0; i <= m; i++)
            s.append("\tat " + trace[i]).append(LINE_SEPARATOR);
        if (framesInCommon != 0)
            s.append("\t... " + framesInCommon + " more").append(LINE_SEPARATOR);

        // Recurse if we have a cause
        Throwable ourCause = cause.getCause();
        if (ourCause != null)
            printStackTraceAsCause(s,ourCause,trace);
    }
}
