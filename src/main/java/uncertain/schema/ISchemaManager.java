/*
 * Created on 2009-7-16
 */
package uncertain.schema;

import java.util.Collection;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;

public interface ISchemaManager {
    
    public Attribute    getAttribute( QualifiedName qname );
    
    public Element      getElement( QualifiedName qname );
    
    public ComplexType  getComplexType( QualifiedName qname );
    
    public SimpleType   getSimpleType( QualifiedName qname );
    
    public IType        getType( QualifiedName qname );
    
    public Category     getCategory( QualifiedName qname );
    
    public Collection   getAllTypes();    
    
    /**
     * Get Element by CompositeMap's QName
     * @param data
     * @return
     */
    public Element getElement( CompositeMap data );    
    public List getElementsOfType( IType parent_type );
    public Wizard getWizard( QualifiedName qname);
    public Editor getEditor(QualifiedName qname);
}
