/*
 * Created on 2009-7-26
 */
package uncertain.schema;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uncertain.composite.QualifiedName;

class NamedObjectManager {

    // QName -> ISchemaObject
    Map[]               mChildMapArray = new Map[10]; 
    // List of IReference
    List                mReference = new LinkedList();
    
    public NamedObjectManager(){
        for( int i=0; i<mChildMapArray.length; i++){
            mChildMapArray[i] = new HashMap();
        }
    }
    
    public void addNamedObject( int type, IQualifiedNamed obj ){
        assert type>=0 && type<mChildMapArray.length && obj!=null;
        if(obj instanceof IHasReference){
            IHasReference ref = (IHasReference)obj;
            if( ref.isRef() ){
                mReference.add(obj);                
                mChildMapArray[type].put(ref.getRefQName(), obj );
                return;
            }
        }        
        if(obj.getQName()==null)
            throw new SchemaError("Qualified name is not set for object "+obj);
        mChildMapArray[type].put(obj.getQName(), obj );        
    }
    
    public Map getObjectMap( int type ){
        assert type>=0 && type<mChildMapArray.length;
        return mChildMapArray[type];
    }
    
    public void putAll( NamedObjectManager another ){
        assert another != null;
        for(int i=0; i<mChildMapArray.length; i++){
            mChildMapArray[i].putAll(another.mChildMapArray[i]);
        }
    }
    
    public Attribute    getAttribute( QualifiedName qname ){
        return (Attribute)mChildMapArray[SchemaConstant.TYPE_ATTRIBUTE].get(qname);
    }
    
    public Element      getElement( QualifiedName qname ){
        IType type = getType(qname);
        if(type==null)
            return null;
        if(type instanceof Element)
            return (Element)type;
        else
            throw new IllegalArgumentException("Specified QName {"+qname+"} is not Element but "+type.getClass().getName());
    }
    
    public Array getArray( QualifiedName qname ){
        Element elm = getElement(qname);
        if(elm.isArray())
            return (Array)elm;
        else
            throw new IllegalArgumentException("Specified QName {"+qname+"} is not Array but ");
    }
    
    public ComplexType  getComplexType( QualifiedName qname ){
        IType type = getType(qname);
        if(type==null)
            return null;
        if(type instanceof ComplexType)
            return (ComplexType)type;
        else
            throw new IllegalArgumentException("Specified QName {"+qname+"} is not ComplexType but "+type.getClass().getName());
    }
    
    public SimpleType   getSimpleType( QualifiedName qname ){
        IType type = getType(qname);
        if(type==null)
            return null;
        if(type instanceof SimpleType)
            return (SimpleType)type;
        else
            throw new IllegalArgumentException("Specified QName {"+qname+"} is not SimpleType but "+type.getClass().getName());
    }
    
    public IType        getType( QualifiedName qname ){
        IType type = (IType)mChildMapArray[SchemaConstant.TYPE_ITYPE].get(qname);
        return type;
    }
    
    public Category getCategory( QualifiedName qname ){
        return (Category)mChildMapArray[SchemaConstant.TYPE_CATEGORIE].get(qname);
    }
    
    public void addAttributes( Attribute[] attribs ){
        if(attribs!=null)
            for(int i=0; i<attribs.length; i++){
                this.addNamedObject(SchemaConstant.TYPE_ATTRIBUTE, attribs[i]);
            }
    }
    
    public void addElements( Element[] elements ){
        if(elements!=null)
            for(int i=0; i<elements.length; i++){
                this.addNamedObject(SchemaConstant.TYPE_ITYPE, elements[i]);
            }
    }
    
    public void addAll( NamedObjectManager another ){
        for(int i=0; i<mChildMapArray.length; i++){
            mChildMapArray[i].putAll(another.mChildMapArray[i]);
        }
    }
    
    public void resolveReference( ISchemaManager manager ){
        for(Iterator it = mReference.iterator(); it.hasNext(); ){            
            IHasReference ref = (IHasReference)it.next();
            ref.resolveReference( manager );
        }
        Collection types = mChildMapArray[ SchemaConstant.TYPE_ITYPE ].values();
        for( Iterator it = types.iterator(); it.hasNext(); ){
            Object o = it.next();
            if( o instanceof ComplexType ){
                ComplexType type = (ComplexType)o;
                type.resolveReference(manager);
            }
        }
    }

}
