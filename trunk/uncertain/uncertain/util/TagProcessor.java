/*
 * ParseState.java
 *
 * Created on 2002年1月12日, 下午6:48
 */

package uncertain.util;

/**
 *
 * @author  Administrator
 * @version 
 */
public abstract class TagProcessor {
    
    public static final int INITIAL_STATE = 0;
    
    int state = INITIAL_STATE;
    
    StringBuffer           tag_string;

    char                      escape_char;

    boolean                is_escape = false;
    
    public TagProcessor(char _escape_char){
        escape_char = _escape_char;
//        handle = _handle;
        tag_string = new StringBuffer();
    }
    
    public char getStartingEscapeChar(){
        return escape_char;
    }
    
    public String getTagString(){
        return tag_string.toString();
    }
    
    public  boolean accept( char ch){
        if( tag_string.length() ==0 && ch==escape_char ) return false;
        boolean acpt = acceptChar(ch);
        if( acpt && isTagChar( ch))  tag_string.append(ch);        
        return acpt;
    }
    
    abstract boolean acceptChar( char ch);
    
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

