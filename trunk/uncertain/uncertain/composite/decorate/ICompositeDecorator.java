/*
 * Created on 2008-6-2
 */
package uncertain.composite.decorate;

import uncertain.composite.CompositeMap;

public interface ICompositeDecorator {
    
    public CompositeMap process( CompositeMap source );

}
