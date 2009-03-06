/*
 * Created on 2007-11-5
 */
package uncertain.util;

import java.io.File;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.ObjectSpace;

/**
 * Init uncertain engine from servlet config
 * ServletHelper
 * @author Zhou Fan
 *
 */
public class ServletHelper {
    
    public static final String  DEFAULT_CONFIG_PATH = "/WEB-INF/uncertain.xml";
    public static final String  KEY_CONFIG_PATH = "config-path";    
    
    
    public static void initUncertain(ServletConfig config, UncertainEngine uncertainEngine) 
    throws Exception{
        
        ServletContext servletContext = config.getServletContext();
        String config_path = config.getInitParameter(KEY_CONFIG_PATH);
        if(config_path==null) config_path = DEFAULT_CONFIG_PATH;

        String config_dir =servletContext.getRealPath("/WEB-INF");
        String config_file="uncertain.xml";
        String pattern = config.getInitParameter("config-pattern");
        if(pattern==null) pattern = ".*\\.config";


        uncertainEngine = new UncertainEngine(new File(config_dir), config_file);
        ObjectSpace os = uncertainEngine.getObjectSpace();
        os.registerParameter(ServletConfig.class,config);
        os.registerParameter(ServletContext.class,servletContext);
         //os.registerParameter(HttpServlet.class, this);
         // os.registerParameter(application);
        CompositeLoader loader = uncertainEngine.getCompositeLoader();
        CompositeMap default_config = loader.loadFromClassPath("org.lwap.application.DefaultClassRegistry");
        ClassRegistry reg = (ClassRegistry)uncertainEngine.getOcManager().createObject(default_config);
        uncertainEngine.addClassRegistry(reg, false);
          //uncertainEngine.getOcManager().populateObject(default_config, uncertainEngine);
          /*
          if(application.data_source!=null){
              os.registerParameter(DataSource.class, application.data_source);
              os.registerParamOnce(TransactionFactory.class, application.transaction_factory);
          } 
          */           
        LoggingUtil.setHandleLevels(uncertainEngine.getLogger().getParent(), Level.INFO);
        //uncertainEngine.getLogger().setLevel(Level.INFO);
        //uncertainEngine.getCompositeLoader().setCaseInsensitive(true);            
        uncertainEngine.scanConfigFiles(pattern);

        
    }  
    

}
