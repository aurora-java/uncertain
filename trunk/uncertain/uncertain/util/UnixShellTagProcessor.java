/*
 * UnixShellTagProcessor.java
 *
 * Created on 2002��1��12��, ����8:41
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

    int acceptChar(char ch) {
        switch(state){
            case INITIAL_STATE:
                if( ch == '{'){
                    state = GET_FIRST_BRACKET;
                    return TagProcessor.RESULT_IN_ESCAPE_CHAR;
                } 
                else return TagProcessor.RESULT_WRONG_CHAR;
            case GET_FIRST_BRACKET:
                if( ch == '}'){
                    return TagProcessor.RESULT_ESCAPE_END_CHAR;
                }else{
                    return TagProcessor.RESULT_IN_ESCAPE_CHAR;
                }
            default:
                return TagProcessor.RESULT_NORMAL_CHAR;
        }
    }
    
    boolean isTagChar(char ch) {
        return ch != '{' && ch != '}';
    }
  
    
}
