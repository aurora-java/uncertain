/*
 * MethodNameFilter.java
 *
 * Created on 2002年1月8日, 下午2:34
 */

package uncertain.util.reflect;

import java.util.*;
import java.lang.reflect.*;

/**
 *
 * @author  Administrator
 * @version 
 */
public class MethodNameFilter implements MethodFilter {
    
    Class[] params;
    String   prefix;
    String   postfix;
    //boolean case_sensitive = true;

    /** Creates new MethodNameFilter */
    public MethodNameFilter(String prefix, String postfix, Class[] params) {
        this.prefix = prefix;
        this.postfix = postfix;
        this.params = params;
    }

    /** not implemented yet */
    /*
    public void setCaseSensitive(boolean s){
        case_sensitive = s;
    }
    */

    public boolean accepts(Class owner, Method m) {
        boolean accept = true;
        String name = m.getName();
        Class[] pms = m.getParameterTypes();
        if( prefix != null) accept = accept && name.startsWith(prefix);
        if( postfix != null) accept = accept && name.endsWith(postfix);
        if( params != null){
            if( params.length != pms.length) accept = false;
            else 
                for(int i=0; i<params.length; i++)            
                  if(!params[i].equals(pms[i])) {
                      accept = false;
                      break;
                  }
        }
        return accept;
    }
    
    public static void main(String[] args) throws Exception {
        MethodCache cache = new MethodCache();
        MethodNameFilter filter = new MethodNameFilter("get",null,null);
        cache.addFilter(filter);
        cache.addFilter(new MethodDeclareClassFilter());
        Collection clt = cache.getMethods( Class.forName("javax.servlet.http.HttpServletRequest"));
        Iterator it = clt.iterator();
        while(it.hasNext()) System.out.println( ((Method)it.next()).getName() );
    }
    
}
