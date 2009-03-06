/*
 * Created on 2008-7-10
 */
package uncertain.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class QuickTagParser {

    TagProcessor processor;

    /**
     * @param processor
     */
    public QuickTagParser(TagProcessor processor) {
        this.processor = processor;
    }
    
    public QuickTagParser(){
        processor = new UnixShellTagProcessor();
    }

    void appendString(int index, TagProcessor processor, StringBuffer buf,
            TagParseHandle handle) {
        String str = null;
        String tag = processor.getTagString();
        if (tag != null)
            if (tag.length() > 0)
                str = handle.ProcessTag(index, tag);
        if (str != null)
            buf.append(str);
    }

    void appendChar(int index, TagParseHandle handle, StringBuffer buf, char ch) {
        int n = handle.ProcessCharacter(index, ch);
        if (n >= 0)
            buf.append((char) n);
    }

    public String parse(Reader reader, TagParseHandle handle)
            throws IOException {

        int  index = 0;
        int  tag_begin = 0;
        char tag_chr = processor.getStartingEscapeChar();

        StringBuffer result = new StringBuffer();
        int ch;

        processor.setEscapeState(false);

        while ((ch = reader.read()) != -1) {
            char chr = (char) ch;
            if (!processor.isEscapeState()) {
                if(tag_chr==chr){
                        processor.setEscapeState(true);
                        tag_begin = index;
                }else
                    appendChar(index,handle,result,chr);
            } else {
                if (!processor.accept(chr)) {
                    processor.setEscapeState(false);
                    appendString(tag_begin, processor, result, handle);
                    appendChar(index, handle, result, chr);
                }
            }
            index++;
        }

        if (processor.isEscapeState())
            appendString(tag_begin, processor, result, handle);

        return result.toString();

    }
    
    public void clear(){
        processor.clear();
        processor = null;
    }
/*    
    public static class TestHandle implements TagParseHandle {
        
        public String  ProcessTag(int index, String tag){
            System.out.println("tag:"+tag);
            return "#OK#";
        }
        

        public int ProcessCharacter( int index, char ch){
            return ch;
        }
        
      }
    
    public static void main(String[] args) throws Exception {
        UnixShellTagProcessor pr = new UnixShellTagProcessor();
        QuickTagParser parser = new QuickTagParser(pr);
        String s = parser.parse( new StringReader("${tag1} is ${tag2}"), new TestHandle());
        System.out.println(s);
    }
*/
    public String parse( String str, TagParseHandle handle )  {
        if(str == null) return null;  
        try{
          return parse( new StringReader(str), handle);
        } catch(IOException ex){
          return null;
        }
      }    
}
