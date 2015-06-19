

package uncertain.composite;

import java.io.IOException;
import java.io.Reader;

import uncertain.util.QuickTagParser;
import uncertain.util.TagParseHandle;

public class TextParser {
	
	//static AdaptiveTagParser parser   = AdaptiveTagParser.newUnixShellParser();
	
	public static class ParseHandle implements TagParseHandle{
	   
	   CompositeMap     map;
	   
	   public ParseHandle( CompositeMap mp){
	   	map = mp;
	   }

           public String  ProcessTag(int index, String tag){
           	Object obj = map.getObject( tag);
           	if( obj== null) return null;
           	else return obj.toString();
           }
           
  			public int ProcessCharacter( int index, char ch){
  				return ch;            
  			}           
	   
	}
	
    /*
	public static String parse( String text, CompositeMap map ){
        AdaptiveTagParser parser = null;
        try{
            parser = AdaptiveTagParser.newUnixShellParser();
            return parser.parse(text, new ParseHandle(map));
        }finally{
            if(parser!=null) parser.clear();
        }
	}
	
	public static String parse( Reader reader, CompositeMap map ) throws IOException {
        AdaptiveTagParser parser = null;
        try{
            parser = AdaptiveTagParser.newUnixShellParser();
            return parser.parse(reader, new ParseHandle(map));
        }finally{
            if(parser!=null) parser.clear();
        }
	}
    */
    
    public static String parse( String text, CompositeMap map ){
        QuickTagParser parser = null;
        try{
            parser = new QuickTagParser();
            return parser.parse(text, new ParseHandle(map));
        }finally{
            if(parser!=null) parser.clear();
        }
    }
    
    public static String parse( Reader reader, CompositeMap map ) throws IOException {
        QuickTagParser parser = null;
        try{
            parser = new QuickTagParser();
            return parser.parse(reader, new ParseHandle(map));
        }finally{
            if(parser!=null) parser.clear();
        }
    }    
	
}