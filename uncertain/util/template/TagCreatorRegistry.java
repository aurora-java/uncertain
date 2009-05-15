/*
 * Created on 2009-5-14
 */
package uncertain.util.template;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implement of ITagCreatorRegistry, using HashMap to save
 * namespace to ITagCreator instance
 * TagCreatorRegistry
 * @author Zhou Fan
 *
 */
public class TagCreatorRegistry implements ITagCreatorRegistry {
    
    static final TagCreatorRegistry DEFAULT_INSTANCE = new TagCreatorRegistry();
    
    public static TagCreatorRegistry getInstance(){
        return DEFAULT_INSTANCE;
    }
    
    ITagCreatorRegistry     mParent;
    Map                     mCreatorMap;
    ITagCreator             mDefaultCreator = DefaultTagCreator.DEFAULT_INSTANCE;
    
    
    public TagCreatorRegistry(){
        mCreatorMap = new HashMap();
    }
    
    public void setParent( ITagCreatorRegistry parent ){
        mParent = parent;
    }
    
    public ITagCreator getTagCreator( String name_space ){
        if(name_space==null)
            return mDefaultCreator;
        ITagCreator creator = (ITagCreator)mCreatorMap.get(name_space);
        if(creator!=null)
            return creator;
        else
            if(mParent!=null)
                return mParent.getTagCreator(name_space);
        return null;
    }
    
    public void setDefaultCreator( ITagCreator creator ){
        mDefaultCreator = creator;
    }
    
    public void registerTagCreator( String name_space, ITagCreator creator ){
        mCreatorMap.put(name_space, creator);
    }

}
