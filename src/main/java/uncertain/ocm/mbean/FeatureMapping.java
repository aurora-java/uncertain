/*
 * Created on 2011-9-1 下午11:15:38
 * $Id$
 */
package uncertain.ocm.mbean;

import java.util.List;

import uncertain.composite.QualifiedName;

/**
 * Class used to wrap FeatureAttache as MBean
 * FeatureMapping
 */
public class FeatureMapping implements FeatureMappingMBean {
    
    QualifiedName   mQname;
    List            mClasses;
    
    public FeatureMapping( QualifiedName qname, List classes ){
        mQname = qname;
        mClasses = classes;
    }
    

    public String getQualifiedNameString(){
        return mQname.toString();
    }

    public String getMappedClass(){
        StringBuffer buf = new StringBuffer();
        Class[] str = new Class[mClasses.size()];
        mClasses.toArray(str);
        //Object[] obj = mClasses.toArray();
        //System.arraycopy(obj, 0, str, 0, obj.length);
        for(int i=0; i<str.length; i++){
            if(i>0)buf.append(",");
            buf.append(str[i].getName());
        }
        return buf.toString();
    }
    
    public String getNameSpace(){
        return mQname.getNameSpace();
    }
    
    public String getElementName(){
        return mQname.getLocalName();
    }
    
    

}
