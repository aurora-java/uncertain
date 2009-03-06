/*
 * UnixShellTagProcessor.java
 *
 * Created on 2002年1月12日, 下午8:41
 */

package uncertain.util;

/**
 * ${tag}
 * @author  Zhou Fan
 * @version 
 */
public class UnixShellTagProcessor extends TagProcessor{
    
    public static final int GET_FIRST_BRACKET = 1;  
    public static final int GET_LAST_BRACKET = 2;

    /** Creates new DefaultTagProcessor */
    public UnixShellTagProcessor(char _escape_char) {
        super(_escape_char);
    }
    
    public UnixShellTagProcessor() {
        super('$');
    }    

    boolean acceptChar(char ch) {
        switch(state){
            case INITIAL_STATE:
                if( ch == '{'){
                    state = GET_FIRST_BRACKET;
                    return true;
                } 
                else return false;
            case GET_FIRST_BRACKET:
                if( ch == '}')
                    state = GET_LAST_BRACKET;
                return true;
            default:
                return false;
        }
    }
    
    boolean isTagChar(char ch) {
        return ch != '{' && ch != '}';
    }
  
    
}
