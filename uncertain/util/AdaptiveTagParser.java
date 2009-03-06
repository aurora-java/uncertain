/*
 * AdaptiveTagParser.java
 *
 * Created on 2002年1月12日, 下午6:47
 */

package uncertain.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 *
 * @author  Administrator
 * @version 
 */
public class AdaptiveTagParser {
    
    public static final int MAX_ESCAPE_CHAR = 128;
    
    TagProcessor[]   processors;

    /** Creates new AdaptiveTagParser */
    public AdaptiveTagParser() {
        processors = new TagProcessor[ MAX_ESCAPE_CHAR+1];
        for(int i=0; i<processors.length; i++) processors[i] = null;
    }
    
    public void clear(){
        for(int i=0; i<processors.length; i++) processors[i] = null;
        processors = null;
    }
    
    public void setTagProcessor( TagProcessor p){
        processors[p.getStartingEscapeChar()] = p; 
    }
    
    void appendString(int index, TagProcessor processor, StringBuffer buf, TagParseHandle handle){
        String str = null;
        String tag = processor.getTagString();
        if(tag != null)
          if(tag.length() > 0) str =  handle.ProcessTag(index,tag);
        if(str != null) buf.append( str);
    }
    
    void appendChar( int index, TagParseHandle handle, StringBuffer buf, char ch){
    	int n = handle.ProcessCharacter(index,ch);
    	if(n>=0) buf.append((char)n);
    }
    
    public String parse( Reader reader, TagParseHandle handle ) throws IOException {
    	
    	int index = 0;
    	int tag_begin = 0;
        
        StringBuffer result = new StringBuffer();
        TagProcessor processor = null;
        int ch;
        
        while ((ch = reader.read()) != -1){            
            char chr = (char)ch;            
            if( processor == null){
                if(chr<=MAX_ESCAPE_CHAR) processor = processors[ch];
                if( processor == null) appendChar(index,handle,result,chr);
                else{
                    processor.setEscapeState(true);
                    tag_begin = index;
                }
                
            }else{
                if( !processor.accept(chr)){
                    processor.setEscapeState(false);
                    appendString( tag_begin, processor, result, handle);
                    appendChar(index,handle,result,chr);
                    processor = null;
                }
            }
            index++;
        }
        
        if( processor != null)
            if( processor.isEscapeState())
                appendString(tag_begin, processor, result,handle);
        
        return result.toString();

    }
    
   public String parse( String str, TagParseHandle handle )  {
    if(str == null) return null;  
    try{
      return parse( new StringReader(str), handle);
    } catch(IOException ex){
      return null;
    }
  }
  
  public static AdaptiveTagParser newParser( TagProcessor tp){
       AdaptiveTagParser parser = new AdaptiveTagParser();
       parser.setTagProcessor(tp);
       return parser;      
  }
  
   public static AdaptiveTagParser newDefaultParser( char escape){
       DefaultTagProcessor p = new DefaultTagProcessor(escape);
       return newParser(p);
   }
   
   public static AdaptiveTagParser newDefaultParser(){
       return newDefaultParser('%');
   }
   
   public static AdaptiveTagParser newUnixShellParser(char escape){
       UnixShellTagProcessor p = new UnixShellTagProcessor(escape);
       return newParser(p);
   }
   
   public static AdaptiveTagParser newUnixShellParser(){
       return newUnixShellParser('$');
   }

}
