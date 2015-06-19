/*
 * Created on 2009-5-14
 */
package uncertain.util.template;

import uncertain.composite.CompositeMap;

public class ErrorTag implements ITagContent {
    
    /**
     * @param sourceFile source file name of template
     * @param line number of line where error occurs
     * @param column column where error occurs
     * @param errorDesc error description
     */
    public ErrorTag(String sourceFile, int line, int column, String errorDesc) {
        mSourceFile = sourceFile;
        mErrorDesc = errorDesc;
        mLine = line;
        mColumn = column;
    }

    String      mSourceFile;
    String      mErrorDesc;
    int         mLine;
    int         mColumn;

    public String getContent(CompositeMap context) {
        StringBuffer content = new StringBuffer("{error:");
        if(mSourceFile!=null){
            content.append("file:");
            content.append(mSourceFile).append(",");
        }
        content.append("line ").append(mLine);
        content.append(",column ").append(mColumn);
        content.append(":").append(mErrorDesc).append("}");
        return content.toString();
    }

}
