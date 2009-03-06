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
	
	public static final String DEFAULT_EXT = "xml";
	
	String          	base_dir;
	String				default_ext;
	boolean				support_xinclude = true;
	boolean				caseInsensitive = false;
	
	LinkedList			extra_path_list = null;
	CompositeMapParser  parser;
	
	// cache feature
	HashMap				composite_map_cache = null;
	boolean				cache_enabled = false;
	
	
	CompositeMap parse( InputStream stream) throws IOException, SAXException {
		if(parser==null)
		    return CompositeMapParser.parse(stream,this);
		else
		    return parser.parseStream(stream);
	}
	
	public CompositeMap getCachedMap(Object key){
	    return composite_map_cache == null?null:(CompositeMap)composite_map_cache.get(key);
	}
	
	public void clearCache(){
	    if(composite_map_cache!=null)
	        composite_map_cache.clear();
	}
	
	public Map getCache(){
	    return composite_map_cache;
	}
	
	public void saveCachedMap(Object key, CompositeMap map){
	    if(composite_map_cache!=null&&map!=null)
	        composite_map_cache.put(key,map);
	}

	public CompositeLoader(){
	    base_dir = null;
	    default_ext = DEFAULT_EXT;
	}
	
	public CompositeLoader( String dir, String ext){
		setBaseDir(dir);
		setDefaultExt(ext);
	}
	
	public CompositeLoader( String dir){
		this(dir, CompositeLoader.DEFAULT_EXT);
	}
	
	public void setCompositeParser(CompositeMapParser p){
	    parser = p;
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
	public String ConvertResourcePath( String path ){
		return path.replace('.', '/') +'.' + default_ext;
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
//	    System.out.println("loading "+file_name);
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(file_name);
			return parse(fis);
		} finally{
			if( fis != null) fis.close();
		}
			
	}

	String getFullPath( String file_name){
		String full_name = file_name;;
		if( file_name == null) return null;
		// attach default extension and file path if nessesary
		if( this.default_ext != null && file_name.indexOf('.')<0)
			full_name = full_name + '.' + this.default_ext;
		if( this.base_dir != null)
			full_name = this.base_dir + full_name;
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
        return loadFromClassPath(full_name, true);
    }
	
	public CompositeMap loadFromClassPath( String full_name, boolean cache ) throws IOException, SAXException {
        if(full_name==null) throw new IllegalArgumentException("path to load CompositeMap is null");
		InputStream stream = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String path = ConvertResourcePath(full_name);
		try{
            if(cache){
                stream = loader.getResourceAsStream(path);
            }else{
                URL url = loader.getResource(path);
                if(url==null) throw new IOException("Can't get resource from "+path);
                stream = url.openStream();    
            }
		    //stream = CompositeLoader.class.getClassLoader().getResourceAsStream(path);
		    //if(stream==null) System.out.println("Can't load "+path);
		    return parse(stream);
		}finally{
			if(stream != null) stream.close();
		}
		
	}
	

	/**
	 * Returns the base_dir.
	 * @return String
	 */
	public String getBaseDir() {
		return base_dir;
	}

	/**
	 * Returns the default_ext.
	 * @return String
	 */
	public String getDefaultExt() {
		return default_ext;
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
		this.base_dir = base_dir + File.separatorChar;
		else this.base_dir = base_dir;
	}

	/**
	 * Sets the default_ext.
	 * @param default_ext The default_ext to set
	 */
	public void setDefaultExt(String default_ext) {
		this.default_ext = default_ext;
	}

	/**
	 * Returns the support_xinclude.
	 * @return boolean
	 */
	public boolean getSupportXInclude() {
		return support_xinclude;
	}

	/**
	 * Sets the support_xinclude.
	 * @param support_xinclude The support_xinclude to set
	 */
	public void setSupportXInclude(boolean support_xinclude) {
		this.support_xinclude = support_xinclude;
	}
	
	public CompositeMap createCompositeMap(String _prefix, String _uri, String _name) {
	    if(caseInsensitive)
	        return new CaseInsensitiveMap(_prefix, _uri, _name);
	    else
	        return new CompositeMap(_prefix, _uri, _name);
	}

    /**
     * @return Returns the caseInsensitive.
     */
    public boolean getCaseInsensitive() {
        return caseInsensitive;
    }
    /**
     * @param caseInsensitive The caseInsensitive to set.
     */
    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
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
}
