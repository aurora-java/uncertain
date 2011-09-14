/*
 * DefaultTagProcessor.java
 *
 * Created on 2002��1��12��, ����7:45
 */

package uncertain.util;

import uncertain.composite.TextParser.ParseHandle;

/**
 * Parse tag in "copy $SOURCE_FILE $DEST_DIR" format
 * @author  Administrator
 * @version 
 */
public class DefaultTagProcessor extends TagProcessor{
    
    public static final int GET_LAST_CHAR = 2;
   

    /** Creates new DefaultTagProcessor */
    public DefaultTagProcessor(char _escape_char) {
        super(_escape_char);
    }
    
    public DefaultTagProcessor() {
        super('%');
    }    

    int acceptChar(char ch) {
        switch( state){
            case INITIAL_STATE:
                if( ch == escape_char){
                    state = GET_LAST_CHAR;
                }
                return TagProcessor.RESULT_IN_ESCAPE_CHAR;
            case GET_LAST_CHAR:                
                return TagProcessor.RESULT_NORMAL_CHAR;
        }
        return TagProcessor.RESULT_NORMAL_CHAR;
    }
    
    
    boolean isTagChar(char ch) {
        return ch != escape_char;
    }
    
  

    
}
