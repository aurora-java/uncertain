/*
 * Created on 2011-9-6 下午02:45:42
 * $Id$
 */
package uncertain.composite;

import java.util.HashSet;
import java.util.Set;


public class QNameFilter implements IterationHandle {
    
    Set mQnameSet;
    
    public QNameFilter( QualifiedName[] qnames){
        mQnameSet = new HashSet();
        for(int i=0; i<qnames.length; i++)
            mQnameSet.add(qnames[i].toString());
    }

    public int process(CompositeMap map) {
        QualifiedName qname = map.getQName();
        if(mQnameSet.contains(qname.toString()))
            return IterationHandle.IT_NOCHILD;
        return IterationHandle.IT_CONTINUE;
    }

}
