/*
 * Created on 2009-7-16
 */
package uncertain.schema;

import java.util.Collection;

import uncertain.composite.QualifiedName;

public interface ISchemaManager {
    
    public static final String SCHEMA_NAMESPACE = "http://www.uncertain-framework.org/schema/simple-schema";
    
    public Attribute    getAttribute( QualifiedName qname );
    
    public Element      getElement( QualifiedName qname );
    
    public ComplexType  getComplexType( QualifiedName qname );
    
    public SimpleType   getSimpleType( QualifiedName qname );
    
    public IType        getType( QualifiedName qname );
    
    public Collection   getAllTypes();    

}
