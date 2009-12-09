/*
 * Created on 2009-6-26
 */
package uncertain.schema;

import java.util.Iterator;

import uncertain.composite.QualifiedName;

public abstract class AbstractQualifiedNamed extends AbstractSchemaObject implements
        IQualifiedNamed, Comparable {
    
    QualifiedName   mQname;
    String          mName;

    public QualifiedName getQName() {
        // TODO Auto-generated method stub
        return mQname;
    }

    public void setQName(QualifiedName qname) {
        mQname = qname;
    }

    /**
     * Get name with prefix  (such as "ss:element")
     * This method is designed for O/C mapping and shall not be invoked directly. Use getLocalName()/setQName() instead.
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     * Set name with prefix (such as "ss:element"), full QName will be resolved later.
     * This method is designed for O/C mapping and shall not be invoked directly. Use getLocalName()/setQName() instead.
     */    
    public void setName(String name) {
        mName = name;
    }
    
    public String getLocalName(){
        return mQname==null?null:mQname.getLocalName();
    }
    
    /**
     * resolve QName and recursively call each child's resolveQName()
     */
    public void resolveQName( IQualifiedNameResolver resolver ){
        if( mName != null){
            mQname = resolver.getQualifiedName(mName);
            if( mQname == null )
                throw new InvalidQNameError(mName);
        }
        // call resolveQName for each child
        if( super.mChilds!=null){
            for(Iterator it = mChilds.iterator(); it.hasNext(); ){
                ISchemaObject obj = (ISchemaObject)it.next();
                if(obj instanceof IQualifiedNameAware ){
                    ((IQualifiedNameAware)obj).resolveQName(resolver);
                }
            }
        }
    }
    
    
    
    /**
     * Consider as equal if of the same class and both have same QName
     */
    public boolean equals( Object another ){
        if(another==null)
            return false;
        if( another.getClass().equals(getClass()) && mQname!=null){
            AbstractQualifiedNamed named = (AbstractQualifiedNamed)another;
            return mQname.equals(named.mQname);
        }
        return super.equals(another);
    }
    
    /**
     * return QName's hasCode()
     */
    public int hashCode(){
        if( mQname!=null)
            return mQname.hashCode();
        else
            return super.hashCode();
    }

    /**
     * If compare to another IQualifiedNamed, local name part will be compared
     */
    public int compareTo(Object o){
        if( o == null )
            return 1;
        if( o instanceof IQualifiedNamed){
            IQualifiedNamed qn = (IQualifiedNamed)o;
            if(getQName()==null) return -1;
            if(qn.getQName()==null) return 1;
            int result = getQName().getLocalName().compareToIgnoreCase(qn.getQName().getLocalName());
            return result;
        }else
            return hashCode() - o.hashCode();
    }

}
