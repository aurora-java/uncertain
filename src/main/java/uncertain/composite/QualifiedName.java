/*
 * Created on 2009-6-26
 */
package uncertain.composite;

/**
 * Encapsulates qualified name of an schema object. The qualified name contains a name and an optional
 * namespace.
 * @author Zhou Fan
 *
 */
public class QualifiedName {
    
    static boolean isEqual( String a, String b ){
        if(a==null||b==null)
            return a==null && b==null;
        return a.equals(b);
    }

    /**
     * @param nameSpace
     * @param name
     */
    public QualifiedName(String nameSpace, String name) {
        this(null,nameSpace,name);
    }
    
    public QualifiedName( String name ){
        this(null,null,name);
    }

    /**
     * @param namespace
     * @param prefix
     * @param local_name
     */
    public QualifiedName(String prefix, String namespace, String local_name) {
        assert local_name != null;
        this.mNameSpace = namespace;
        this.mPrefix = prefix;
        this.mLocalName = local_name;
        if(namespace==null)
            mInternalName = local_name;
        else
            mInternalName = namespace + local_name;
    }

    String mNameSpace;
    String mPrefix;
    String mLocalName;
    String mInternalName;

    public String getNameSpace() {
        return mNameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.mNameSpace = nameSpace;
    }

    public String getPrefix() {
        return mPrefix;
    }

    public void setPrefix(String prefix) {
        this.mPrefix = prefix;
    }

    public String getLocalName() {
        return mLocalName;
    }

    public void setLocalName(String name) {
        this.mLocalName = name;
    }
    
    public String getFullName(){
        StringBuffer buf = new StringBuffer();
        if(mPrefix!=null)
            buf.append(mPrefix).append(":");
        buf.append(mLocalName);
        return buf.toString();
    }
    
    /** @return hashCode of namespace + name */
    public int hashCode() {        
        return mInternalName.hashCode();
    }
    
    /** when compare with QualifiedName, requires both namespace and name is equal */
    public boolean equals(Object obj) {        
        if(obj instanceof QualifiedName){
            QualifiedName q = (QualifiedName)obj;
            return isEqual(mLocalName, q.mLocalName) && isEqual( mNameSpace, q.mNameSpace );
        }else{
            return false;
        }
    }
    
    public String toString(){
        if( mNameSpace==null)
            return mLocalName;
        else
            return "{"+mNameSpace+"}"+mLocalName;
    }

}
