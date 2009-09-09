/*
 * Created on 2009-6-3
 */
package uncertain.core.admin;

import java.util.Collections;

import uncertain.composite.CompositeComparator;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class GlobalInstanceList extends AbstractEntry  {
    
    UncertainEngine     mEngine;
    String              mPath;
    
    public GlobalInstanceList( UncertainEngine engine){
        mEngine = engine;
    }

    public void run(ProcedureRunner runner) throws Exception {
        ObjectRegistryImpl registry =  (ObjectRegistryImpl)mEngine.getObjectRegistry();
        CompositeMap target = runner.getContext().createChildByTag(mPath);
        registry.dumpInstanceList(target);        
        Collections.sort(target.getChilds(), new CompositeComparator("type"));
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

}
