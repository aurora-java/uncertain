/*
 * Created on 2010-4-22 ÏÂÎç03:50:07
 * $Id: ProcedureRegistry.java 739 2010-04-28 08:07:18Z seacat.zhou@gmail.com $
 */
package uncertain.proc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.core.ConfigurationError;
import uncertain.core.IGlobalInstance;

/**
 * Maintains mapping between code and actual procedure name or config 
 */
public class ProcedureRegistry implements IProcedureRegistry, IGlobalInstance {

    public static final String KEY_PROCEDURE_MAPPINGS = "procedure-mappings";

    public static final String KEY_PROCEDURES = "procedures";

    public static final QualifiedName PROCEDURE_NAME = new QualifiedName("uncertain.proc",
            "procedure");

    // name -> actual procedure name
    Map mProcedureNameMap = new HashMap();

    // name -> procedure config
    Map mProcedureConfigMap = new HashMap();
    
    IProcedureManager   mProcedureManager;

    public ProcedureRegistry() {
        mProcedureManager = ProcedureManager.getDefaultInstance();
    }

    /**
     * @param procedureManager
     */
    public ProcedureRegistry(IProcedureManager procedureManager) {
        mProcedureManager = procedureManager;
    }
    
    public String getMappedProcedure(String name) {
        return (String) mProcedureNameMap.get(name);
    }

    public CompositeMap getProcedureConfig(String name) {
        return (CompositeMap) mProcedureConfigMap.get(name);
    }

    public void addMappedProcedure(String name, String mapped_proc_name) {
        mProcedureNameMap.put(name, mapped_proc_name);
    }

    public void addProcedureConfig(String name, CompositeMap proc_config) {
        mProcedureConfigMap.put(name, proc_config);
    }

    public void addProcedures(CompositeMap proc_config) {
        Iterator it = proc_config.getChildIterator();
        if (it != null)
            while (it.hasNext()) {
                CompositeMap item = (CompositeMap)it.next();
                if(!PROCEDURE_NAME.equals(item.getQName()))
                    throw new ConfigurationError("not a valid procedure:"+item.toXML());
                String name = item.getString("name");
                if(name==null)
                    throw new ConfigurationError("no name defined in procedure:"+item.toXML());
                addProcedureConfig(name, item);
            }
    }
    
    public void addProcedureMappings( CompositeMap mappings ){
        Iterator it = mappings.getChildIterator();
        if (it != null)
            while (it.hasNext()) {
                CompositeMap item = (CompositeMap)it.next();
                String name = item.getString("name");
                if(name==null)
                    throw new ConfigurationError("'name' property must be set:"+item.toXML());
                String procedure = item.getString("procedure");
                if(procedure==null)
                    throw new ConfigurationError("'procedure' property must be set:"+item.toXML());
                mProcedureNameMap.put(name, procedure);
            }
    }
    
    public Procedure getProcedure( String name ){
        try{
            String proc_name = getMappedProcedure(name);
            if(proc_name!=null)
                return mProcedureManager.loadProcedure(proc_name);
            CompositeMap config = getProcedureConfig(name);
            if(config!=null)
                return mProcedureManager.createProcedure(config);
            return null;
        }catch(Exception ex){
            throw new RuntimeException("Error when loading named procedure:"+name, ex);
        }
    }
    

}
