/*
 * ICompositeMap.java
 *
 * Created on 2009-7-27 14:11 PM
 */

package uncertain.composite;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/** A Map with a list of child maps, and a name and namespace
 * for xml creation
 * @author Zhou Fan
 */
public interface ICompositeMap extends Map {
	
    
    public void addChilds( Collection another);
    
	
    public ICompositeMap replaceChild( ICompositeMap child, ICompositeMap new_child);

    public ICompositeMap replaceChild( String child_name, ICompositeMap new_child);
    
    public ICompositeMap copy( ICompositeMap another);
    
    public void setParent( ICompositeMap p);
    
    public ICompositeMap getParent();
    
    public ICompositeMap getRoot();
    
    public Object putObject( String key, Object value);
    
    public void addChild( int index, ICompositeMap child );
    
    public void addChild( ICompositeMap child);

    public boolean removeChild( ICompositeMap child );
    
    public ICompositeMap createChild( String prefix, String uri, String name);
    
    public ICompositeMap createChild( String name);
    
    public ICompositeMap createChildByTag( String access_tag);
    
    public ICompositeMap getChild( ICompositeMap child );
    
    /** get a child CompositeMap with specified name
     * @param name name of CompositeMap to find
     * @return child CompositeMap found or null
     */
    public ICompositeMap getChild(String name);
    
    public List getChilds();

    public List getChildsNotNull();

    public Iterator getChildIterator();
    
    public Object getObject( String key);
    
    public boolean putObject( String key, Object value, boolean create);
    
    public String toXML();
    
    public int iterate( IterationHandle handle, boolean root_first);

	public void clear();	
}
