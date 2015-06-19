/*
 * Created on 2009-7-23
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;

/**
 * Get QualifiedName by given prefix and name
 * @author Zhou Fan
 *
 */
public interface IQualifiedNameResolver {
    
    public QualifiedName    getQualifiedName( String prefix, String name );
    
    /**
     * Get qualified name by name with prefix, for example: "xs:element:
     * @param name
     * @return
     */
    public QualifiedName    getQualifiedName( String name );

}
