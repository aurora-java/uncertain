/**
 * Created on: 2002-11-15 16:58:18
 * Author:     zhoufan
 */
package uncertain.composite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

/**
 *  load Composite from xml files stored in specified path
 * 
 */
public class CompositeLoader {
    
    /**
     * Create a CompositeLoader that use lower attribute case
     * @param extension
     * @return
     */
    public static CompositeLoader createInstanceForOCM( String extension ){
        CompositeLoader loader = new CompositeLoader();
        loader.ignoreAttributeCase();
        loader.setDefaultExt(extension==null?DEFAULT_EXT:extension);
        return loader;        
    }
	
    public static CompositeLoader createInstanceForOCM(){
        return createInstanceForOCM(DEFAULT_EXT);
    }
    
    public static CompositeLoader createInstanceWithExt( String default_file_ext ){
        CompositeLoader loader = new CompositeLoader();
        loader.setDefaultExt(default_file_ext);
        return loader;
    }
    
    public static CompositeLoader createInstanceWithBaseDir( String base_dir ){
        CompositeLoader loader = new CompositeLoader();
        loader.setBaseDir(base_dir);
        return loader;
    }
    
	public static final String DEFAULT_EXT = "xml";
	
	String          	mBaseDir;
	String				mDefaultExt;
	boolean				mSupportXinclude = true;
	boolean				mCaseInsensitive = false;
	//boolean             mCreateLocator = false;
	NameProcessor       mNameProcessor = null;
	ClassLoader         mClassLoader = Thread.currentThread().getContextClassLoader();
	
	LinkedList			extra_path_list = null;
	//CompositeMapParser  parser;
	
	// cache feature
	HashMap				composite_map_cache = null;
	boolean				cache_enabled = false;
	
	
	CompositeMap parse( InputStream stream) throws IOException, SAXException {
	    CompositeMapParser p = new CompositeMapParser(this);
		return p.parseStream(stream);
	}
	
	public CompositeMap getCachedMap(Object key){
	    return composite_map_cache == null?null:(CompositeMap)composite_map_cache.get(key);
	}
	
	public void clearCache(){
	    if(composite_map_cache!=null)
	        composite_map_cache.clear();
	}
	
	protected Map getCache(){
	    return composite_map_cache;
	}
	
	public void saveCachedMap(Object key, CompositeMap map){
	    if(composite_map_cache!=null&&map!=null)
	        composite_map_cache.put(key,map);
	}

	public CompositeLoader(){
	    mBaseDir = null;
	    mDefaultExt = DEFAULT_EXT;
	}
	
	public CompositeLoader( String base_dir, String default_ext){
		setBaseDir(base_dir);
		setDefaultExt(default_ext);
	}

	public void addExtraLoader( CompositeLoader loader ){
		if( extra_path_list == null) extra_path_list = new LinkedList();
		extra_path_list.add(loader);
	}
	
	public void addDocumentPath( CompositeLoader loader ){
	    addExtraLoader(loader);
	}
	
	public List getExtraLoader(){
		return this.extra_path_list;
	}
	
	
	/** convert path from class style to file style */
	public String convertResourcePath( String path ){
		return path.replace('.', '/') +'.' + mDefaultExt;
	}

    public String convertResourcePath( String path, String file_ext ){
        return path.replace('.', '/') +'.' + file_ext;
    }
	
	public CompositeMap loadFromString( String str) throws IOException, SAXException {
		return parse( new ByteArrayInputStream(str.getBytes()));
	}
	
	
	public CompositeMap loadFromStream( InputStream stream) throws IOException, SAXException {
		return parse(stream);
	}
		
	
	protected CompositeMap loadByURL_NC( String url) throws IOException, SAXException {
		InputStream stream = null;	
		try{
			URL the_url = new URL(url);
			stream = the_url.openStream();
			return parse(stream);			
		} catch(Throwable thr){
			throw new IOException( thr.getMessage());
		} finally{
			if( stream != null) stream.close();
		}
		
	}

	public CompositeMap loadByURL( String url) throws IOException, SAXException {
	
	    if(!getCacheEnabled())
	        return loadByURL_NC(url);
	    CompositeMap m = getCachedMap(url);
	    if(m==null) {
	        m = loadByURL_NC(url);
	        saveCachedMap(url, m);
	    }
	    return m;
	}

	public CompositeMap loadByFullFilePath( String file_name) throws IOException, SAXException {
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(file_name);
			CompositeMap map =  parse(fis);
			map.setSourceFile(file_name);
			return map;
		} finally{
			if( fis != null) fis.close();
		}
			
	}

	String getFullPath( String file_name){
		String full_name = file_name;;
		if( file_name == null) return null;
		// attach default extension and file path if nessesary
		if( this.mDefaultExt != null && file_name.indexOf('.')<0)
			full_name = full_name + '.' + this.mDefaultExt;
		if( this.mBaseDir != null)
			full_name = this.mBaseDir + full_name;
		return full_name;		
	}
	
	public File getFile(String file_name){
		File file = new File(getFullPath(file_name));
		if( file.exists()) return file;
		else{
			if( this.extra_path_list == null) return null;
			else{
				Iterator it = this.extra_path_list.iterator();
				while( it.hasNext()){
					CompositeLoader ld = (CompositeLoader) it.next();
					file =  ld.getFile(file_name);
					if( file != null) return file;
				}
				return null;
			}			
		}
	}
	
	
	public CompositeMap loadByFile( String file_name) throws IOException, SAXException {
		String full_name = getFullPath(file_name); 
			
		try{
			return loadByFullFilePath( full_name);	
		} catch(IOException ex){
			if( this.extra_path_list == null) throw ex;
			else{
				Iterator it = this.extra_path_list.iterator();
				while( it.hasNext()){
					CompositeLoader ld = (CompositeLoader) it.next();					
					try{
					    CompositeMap m =ld.loadByFile(file_name);
					    return m;
						/*
					    if(m!=null){
						    if(getCacheEnabled())
						        composite_map_cache.put(file_name,m);
						    return m; 
						}
						*/
					} catch(IOException nex){
					}
				}
				throw ex;
			}
		}
	}
	
	protected CompositeMap loadNC( String resource_name) throws IOException, SAXException {
		if( resource_name == null) return null;
		// First try to load by URL
		if( resource_name.indexOf(':')>0)
			return loadByURL( resource_name);
		return loadByFile( resource_name);	
		
	}
	

	public CompositeMap load( String resource_name) throws IOException, SAXException {
	    if(!getCacheEnabled())
	        return loadNC(resource_name);
	    CompositeMap m = getCachedMap(resource_name);
	    if(m==null) {
	        m = loadNC(resource_name);
	        if(m!=null){
//	            System.out.println("caching "+resource_name);
	            saveCachedMap(resource_name, m);
	        }
	    }
	    return m==null?null:(CompositeMap)m.clone();	
	}
	
    public CompositeMap loadFromClassPath( String full_name) throws IOException, SAXException {
        return loadFromClassPath(full_name, mDefaultExt, false);
    }
    
    public CompositeMap loadFromClassPath( String full_name, String file_ext ) throws IOException, SAXException {
        return loadFromClassPath(full_name, file_ext, false);
    }
	
	private CompositeMap loadFromClassPath( String full_name, String file_ext, boolean cache ) throws IOException, SAXException {
        if(full_name==null) throw new IllegalArgumentException("path to load CompositeMap is null");
		InputStream stream = null;
        String path = convertResourcePath(full_name, file_ext);
		try{
            if(cache){
                stream = mClassLoader.getResourceAsStream(path);
                return parse(stream);
            }else{
                URL url = mClassLoader.getResource(path);                
                if(url==null) throw new IOException("Can't get resource from "+path);
                String file = url.getFile(); 
                return loadByFullFilePath(file);    
            }
		}finally{
			if(stream != null) stream.close();
		}
	}
	

	/**
	 * Returns the base_dir.
	 * @return String
	 */
	public String getBaseDir() {
		return mBaseDir;
	}

	/**
	 * Returns the default_ext.
	 * @return String
	 */
	public String getDefaultExt() {
		return mDefaultExt;
	}

	/**
	 * Sets the base_dir.
	 * @param base_dir The base_dir to set
	 */
	public void setBaseDir(String base_dir) {
	    File f= new File(base_dir);
	    if(!f.exists()) throw new IllegalArgumentException("Directory not exists:"+base_dir);
		int len = base_dir.length();
		if( base_dir.charAt(len-1) != '\\' && base_dir.charAt(len-1) != '/')
		this.mBaseDir = base_dir + File.separatorChar;
		else this.mBaseDir = base_dir;
	}

	/**
	 * Sets the default_ext.
	 * @param default_ext The default_ext to set
	 */
	public void setDefaultExt(String default_ext) {
		this.mDefaultExt = default_ext;
	}

	/**
	 * Returns the support_xinclude.
	 * @return boolean
	 */
	public boolean getSupportXInclude() {
		return mSupportXinclude;
	}

	/**
	 * Sets the support_xinclude.
	 * @param support_xinclude The support_xinclude to set
	 */
	public void setSupportXInclude(boolean support_xinclude) {
		this.mSupportXinclude = support_xinclude;
	}
	
	public CompositeMap createCompositeMap(String _prefix, String _uri, String _name) {
	    if(mCaseInsensitive)
	        return new CaseInsensitiveMap(_prefix, _uri, _name);
	    else
	        return new CompositeMap(_prefix, _uri, _name);
	}

    /**
     * @return Returns the caseInsensitive.
     */
    public boolean getCaseInsensitive() {
        return mCaseInsensitive;
    }
    /**
     * @param caseInsensitive The caseInsensitive to set.
     */
    public void setCaseInsensitive(boolean caseInsensitive) {
        this.mCaseInsensitive = caseInsensitive;
    }
    /**
     * @return Returns the cache_enabled.
     */
    public boolean getCacheEnabled() {
        return cache_enabled;
    }
    /**
     * @param cache_enabled The cache_enabled to set.
     */
    public void setCacheEnabled(boolean cache_enabled) {
        this.cache_enabled = cache_enabled;
        if(cache_enabled && composite_map_cache==null) 
            composite_map_cache = new HashMap();
        if(extra_path_list!=null){
            Iterator it = extra_path_list.iterator();
            while(it.hasNext()){
                CompositeLoader l = (CompositeLoader)it.next();
                l.setCacheEnabled(cache_enabled);
            }
        }
    }

    public NameProcessor getNameProcessor() {
        return mNameProcessor;
    }

    public void setNameProcessor(NameProcessor name_processor) {
        this.mNameProcessor = name_processor;
    }
    
    public void ignoreAttributeCase(){
        NameProcessor p = new CharCaseProcessor(CharCaseProcessor.CASE_LOWER, CharCaseProcessor.CASE_UNCHANGED);
        setNameProcessor(p);
    }

    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        mClassLoader = classLoader;
    }
/*
    public boolean getCreateLocator() {
        return mCreateLocator;
    }

    public void setCreateLocator(boolean createLocator) {
        mCreateLocator = createLocator;
    }
*/    
}
