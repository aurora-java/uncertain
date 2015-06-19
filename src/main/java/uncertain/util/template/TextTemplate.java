/*
 * Created on 2007-8-5
 */
package uncertain.util.template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.composite.CompositeMap;

/**
 * Template based text content. Sequence of static content and dynamic content. 
 */
public class TextTemplate implements Serializable, Cloneable {
    
    LinkedList      mContentList;
    String          mSourceName;
    
    public TextTemplate(){
        mContentList = new LinkedList();
    }
    
    public void setSourceName( String name ){
        mSourceName = name;
    }
    
    /**
     * @return name of source file or URL that the template is parse from  
     */
    public String getSourceName(){
        return mSourceName;
    }

    public void addContent(IStaticContent content){
        mContentList.add(content);        
    }
    
    public void addContent(String content){
        mContentList.add(content);
    }
    
    public void addContent(StringBuffer content){
        mContentList.add(content);
    }
    
    public void addContent(ITagContent content){
        mContentList.add(content);
    }

    public List getContents(){
        return null;
    }
    
    /**
     * Create text output
     * @param writer
     * @param provider
     * @throws IOException
     */
    public void createOutput(Writer writer, CompositeMap  context )
        throws IOException
    {
        Iterator it = mContentList.iterator();
        while(it.hasNext()){
            Object obj = it.next();
            if(obj instanceof String || obj instanceof StringBuffer || obj instanceof StringBuilder){
                writer.write(obj.toString());
            }
            else if( obj instanceof ITagContent){
                String str = ((ITagContent)obj).getContent(context);
                if(str!=null)
                    writer.write( str );                    
            }
            else if( obj instanceof IStaticContent){
                ((IStaticContent)obj).write(writer);
            }
        }
    }
    
    public String toString( CompositeMap context )
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        createOutput( writer, context );
        return baos.toString();
    }
    
    public void clear(){
        mContentList.clear();
    }

    protected Object clone() throws CloneNotSupportedException {
        TextTemplate new_template = new TextTemplate();
        new_template.mSourceName = mSourceName;
        new_template.mContentList = new LinkedList();
        mContentList.addAll(mContentList);
        return new_template;
    }
    
    

}
