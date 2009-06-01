/*
 * Created on 2009-5-29
 */
package uncertain.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xml.sax.SAXException;

import uncertain.composite.CharCaseProcessor;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;

public class DocumentFactory {
    
    String          mExtension;
    ClassLoader     mClassLoader;
    boolean         mCacheEnabled;
    CompositeLoader mCompositeLoader;
    
    public DocumentFactory(){
        mExtension = "xml";
        mClassLoader = Thread.currentThread().getContextClassLoader();
        mCacheEnabled = false;
        mCompositeLoader = new CompositeLoader();
        mCompositeLoader.setParserPrototype( CompositeMapParser.createInstance(
                mCompositeLoader, new CharCaseProcessor(CharCaseProcessor.CASE_LOWER, CharCaseProcessor.CASE_UNCHANGED)
                ));
    }
    
    public DocumentFactory( String extension ){
        this();
        setExtension(extension);
    }
    
    public File getByClassPath( String name ){
        return getByClassPath( name, mExtension );
    }
    
    public File getByClassPath( String name, String extension ){
        StringBuffer path = new StringBuffer(name.replace('.', '/'));
        path.append('.').append(extension);
        return new File(mClassLoader.getResource(path.toString()).getFile());
    }
    
    public CompositeMap loadCompositeMap( String name )        
        throws IOException, SAXException
    {
        return loadCompositeMap( name, mExtension );
    }
    
    public CompositeMap loadCompositeMap( String name, String extension )
        throws IOException, SAXException
    {
        File file = getByClassPath(name, extension );
        if(!file.exists())
            throw new FileNotFoundException(file.getPath());
        return mCompositeLoader.loadByFullFilePath(file.getPath());
    }
    
    /**
     * @return the mExtension
     */
    public String getExtension() {
        return mExtension;
    }

    /**
     * @param extension the mExtension to set
     */
    public void setExtension(String extension) {
        mExtension = extension;
    }

    /**
     * @return the mClassLoader
     */
    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    /**
     * @param classLoader the mClassLoader to set
     */
    public void setClassLoader(ClassLoader classLoader) {
        mClassLoader = classLoader;
    }

    /**
     * @return the mCacheEnabled
     */
    public boolean isCacheEnabled() {
        return mCacheEnabled;
    }

    /**
     * @param cacheEnabled the mCacheEnabled to set
     */
    public void setCacheEnabled(boolean cacheEnabled) {
        mCacheEnabled = cacheEnabled;
    }

    /**
     * @return the mCompositeLoader
     */
    public CompositeLoader getCompositeLoader() {
        return mCompositeLoader;
    }

    /**
     * @param compositeLoader the mCompositeLoader to set
     */
    public void setCompositeLoader(CompositeLoader compositeLoader) {
        mCompositeLoader = compositeLoader;
    }

}
