/*
 * Created on 2008-6-2
 */
package uncertain.composite.decorate;

import java.util.Iterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class CompositeDecorator extends DynamicObject implements ICompositeDecorator {
    
    public CompositeMap process( CompositeMap source ){
        Iterator it = getObjectContext().getChildIterator();
        if(it==null)
            return source;
        while(it.hasNext()){
            ElementModifier locator = (ElementModifier)DynamicObject.cast( (CompositeMap)it.next(), ElementModifier.class);
            locator.process(source);
        }
        return source;
        
    }

}
