/*
 * DefaultTagProcessor.java
 *
 * Created on 2002年1月12日, 下午7:45
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

    boolean acceptChar(char ch) {
        switch( state){
            case INITIAL_STATE:
                if( ch == escape_char){
                    state = GET_LAST_CHAR;
                }
                return true;
            case GET_LAST_CHAR:                
                return false;
        }
        return false;
    }
    
    
    boolean isTagChar(char ch) {
        return ch != escape_char;
    }
    
  

    
}
