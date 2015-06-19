/*
 * ParseState.java
 *
 * Created on 2002��1��12��, ����6:48
 */

package uncertain.util;

/**
 *
 * @author  Administrator
 * @version 
 */
public abstract class TagProcessor {
    
    public static final int RESULT_IN_ESCAPE_CHAR = 0;
    
    public static final int RESULT_ESCAPE_END_CHAR = 1;
    
    public static final int RESULT_NORMAL_CHAR = 2;

    public static final int RESULT_WRONG_CHAR = -1;
    
    public static final int INITIAL_STATE = 0;
    
    int state = INITIAL_STATE;
    
    StringBuilder           tag_string;

    char                      escape_char;

    boolean                is_escape = false;
    
    public TagProcessor(char _escape_char){
        escape_char = _escape_char;
//        handle = _handle;
        tag_string = new StringBuilder();
    }
    
    public char getStartingEscapeChar(){
        return escape_char;
    }
    
    public String getTagString(){
        return tag_string.toString();
    }
    
    public  int accept( char ch){
        if( tag_string.length() ==0 && ch==escape_char ) return RESULT_NORMAL_CHAR;
        int acpt = acceptChar(ch);
        if( RESULT_IN_ESCAPE_CHAR==acpt && isTagChar( ch))  tag_string.append(ch);        
        return acpt;
    }
    
    abstract int acceptChar( char ch);
    
    abstract boolean isTagChar( char ch);
    
    public boolean isEscapeState() { return is_escape; }
    
    public void setEscapeState(boolean b){
        is_escape = b;
        if(b){ 
            state = INITIAL_STATE;
            tag_string.setLength(0);
        }
    }
    
    public void clear(){
        if(tag_string!=null){
            tag_string.setLength(0);
            tag_string = null;
        }
    }
 /*   
    public String getProcessedString(){
        String str = tag_string.toString();
        if(str.length()==0) return null;
        else  return handle.ProcessTag(str);
    }
    
    public void setHandle( TagParseHandle h){
        handle = h;
    }
  */

}

