/*
 * Created on 2009-6-26
 */
package uncertain.schema;

import uncertain.composite.QualifiedName;

/**
 * Define general method for schema object that has a qualified name,
 * such as element, attribute, complexType
 * @author Zhou Fan
 *
 */
public interface IQualifiedNamed extends ISchemaObject, IQualifiedNameAware {
    
    public QualifiedName   getQName();
    
    public void setQName( QualifiedName qname );

}
