/*
 * XMLWritter.java
 *
 * Created on 2002年1月5日, 下午4:19
 */

package uncertain.util;

/**
 *
 * @author  Administrator
 * @version 
 */
public class XMLWritter {


  public static String DEFAULT_ENCODING = "utf-8";

  public static String getXMLDecl(String encoding){
   if (encoding == null) encoding = DEFAULT_ENCODING;
   return "<?xml version=\"1.0\" encoding = \"" + encoding + "\"?>\r\n";
  } 

  public static String startTag(String element){
    return "<" + element + ">";
  }
  
  public static String endTag(String element){
    return "</" + element + ">";
  }
  
  public static String cdata(String value){
    return  "<![CDATA[" + value + "]]>";
  }
  
  public static String getAttrib(String key, String value){
    return key+'='+'"'+escape(value) + '"';
  }
  
  public static String escape(String value){
    
     StringBuffer dom  = new StringBuffer();
     for(int i=0; i<value.length(); i++){
       	char ch = value.charAt(i); 
       	if( ch=='<') dom.append("&lt;");
       	else if(ch=='>') dom.append("&gt;");
       	else if(ch=='&') dom.append("&amp;");
        else if(ch=='"') dom.append("&quot;");
        else if(ch=='\'') dom.append("&apos;");
       	else dom.append(ch);
       }
    return dom.toString();
  }
  
}
