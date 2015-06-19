/*
 * TagParser.java
 *
 * Created on 2001年12月13日, 下午3:38
 */

package uncertain.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

/** 
 *
 * @author  Zhou Fan
 * @version 
 */
public class TagParser {

  TagParseHandle tag_handle;
  
  public TagParser( TagParseHandle th ) {
    tag_handle = th;
  }
  
  public String parse(String str, char escape)  {
    if(str == null) return null;  
    try{
      return parse( new StringReader(str), escape);
    } catch(IOException ex){
      return null;
    }
  }
  
  public String parse(Reader in, char escape) throws IOException{ 
        
        int ch;
        boolean in_escape = false;  //boolean flag to decide
        String var = "";
        StringBuffer buf = new StringBuffer();
        int index = 0;
        
        /* read all contents into string buffer, replace parameter tag with those found in request query string */
        while ((ch = in.read()) != -1){
          char chr = (char)ch;
          if( in_escape ){
            if( Character.isLetterOrDigit(chr) || ch=='_') var += chr;
            else{
              in_escape = false;
              if( var.length()>0) {
                String vl = tag_handle.ProcessTag(index,var); 
                if(vl != null) buf.append(vl);
              }
              else if( chr==escape){
                buf.append(chr);
                continue;
              }
              var = "";
            }
          }
          if( chr == escape) in_escape = true;
          else if(!in_escape ) buf.append(chr);
          index++;
        } 
        if( in_escape){
          String vl = tag_handle.ProcessTag(index,var); 
          if(vl != null) buf.append(vl);
        } 
        return buf.toString();
   }
   
   public static String parse( Reader in, char escape, Map props) throws IOException {

       final Map p = props;

       return new TagParser( new TagParseHandle(){
           
           public String  ProcessTag(int index , String tag){
                return (String)p.get(tag);
           }
  		   
  		   public int ProcessCharacter( int index, char ch){
  				return ch;            
  		   }
           
       }
       ).parse(in, escape);   
   }
   
   public static String parse( String text, char escape, Map props) {
       try{
            return parse( new StringReader(text), escape, props);
       } catch (IOException ex){
           return null;
       }
   }
  
}