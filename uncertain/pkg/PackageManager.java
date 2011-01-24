/*
 * Created on 2009-5-1
 */
package uncertain.pkg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import uncertain.composite.CompositeLoader;
import uncertain.ocm.OCManager;
import uncertain.schema.SchemaManager;

/**
 * Manages ComponentPackages PackageManager
 */
public class PackageManager {

    CompositeLoader mCompositeLoader;
    OCManager mOCManager;
    HashMap mPackageNameMap = new HashMap();
    SchemaManager mSchemaManager = new SchemaManager();

    public static boolean isPackageDirectory(File dir) {
        if (!dir.isDirectory())
            return false;
        File config_dir = new File(dir, "config");
        if (!config_dir.exists() || !config_dir.isDirectory())
            return false;
        File pkg_xml = new File(config_dir, "package.xml");
        if (!pkg_xml.exists())
            return false;
        return true;
    }

    public PackageManager() {
        mCompositeLoader = CompositeLoader.createInstanceForOCM(null);
        mOCManager = OCManager.getInstance();
    }

    public PackageManager(CompositeLoader loader, OCManager oc_manager) {
        mCompositeLoader = loader;
        mOCManager = oc_manager;
    }

    public CompositeLoader getCompositeLoader() {
        return mCompositeLoader;
    }

    public OCManager getOCManager() {
        return mOCManager;
    }

    public ComponentPackage loadPackage(String path) throws IOException {
        return loadPackage(path, ComponentPackage.class);
    }

    protected void initPackage(ComponentPackage pkg) {
        pkg.setPackageManager(this);
        mPackageNameMap.put(pkg.getName(), pkg);
    }

    public void addPackage(ComponentPackage pkg) {
        // if(pkg.getPackageManager()!=this)
        initPackage(pkg);
        SchemaManager sm = pkg.getSchemaManager();
        if (sm != null)
            mSchemaManager.addAll(sm);
    }

    public ComponentPackage loadPackage(String path, Class implement_cls)
            throws IOException {
        ComponentPackage pkg = null;
        try {
            pkg = (ComponentPackage) implement_cls.newInstance();
        } catch (Exception ex) {
            throw new PackageConfigurationError("Can't create instance of "
                    + implement_cls.getName(), ex);
        }
        initPackage(pkg);
        pkg.load(path);
        addPackage(pkg);
        // System.out.println("loaded "+path);
        return pkg;
    }

    public ComponentPackage getPackage(String name) {
        return (ComponentPackage) mPackageNameMap.get(name);
    }

    public SchemaManager getSchemaManager() {
        return mSchemaManager;
    }

    /**
     * Load all package under specified directory
     * 
     * @param directory
     *            root directory that contains packages to load
     * @throws IOException
     */
    public void loadPackgeDirectory(String directory) throws IOException {
        File path = new File(directory);
        if (!path.isDirectory())
            throw new IllegalArgumentException(directory
                    + " is not a directory");
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory() && isPackageDirectory(file))
                loadPackage(file.getAbsolutePath());
        }
    }

   protected void extractTempZipFile( JarInputStream jis, File baseDir, String file_name) throws IOException {
       File file =  new File(baseDir, file_name);
       file.deleteOnExit();
       FileOutputStream fos = new FileOutputStream(file);
       for (int c = jis.read(); c != -1; c = jis.read()) {
           fos.write(c);
         }
       fos.close();
   }
   
   /**
    * Unzip specified entry in a jar file to temp directory
    * @param jar_path
    * @param pkg_name
    * @return
    * @throws IOException
    */
    protected File createTempPackageDir(String jar_path, String pkg_name)
            throws IOException {
        InputStream is = null;
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        try {
            URL u = new URL(jar_path);
            is = u.openStream();
            JarInputStream jis = new JarInputStream(is);
            ZipEntry ze = null;
            
            File baseDir = tempDir;
            while ((ze = jis.getNextEntry()) != null) {
                String name = ze.getName();
                if (name.startsWith(pkg_name)) {
                    if (ze.isDirectory()) {
                        baseDir = new File(tempDir, name);
                        if (baseDir.exists())
                            if (!baseDir.delete())
                                throw new IOException(
                                        "Can't delete existing dir "
                                                + baseDir.getAbsolutePath());
                        if (!baseDir.mkdirs())
                            throw new IOException("Can't create dir "
                                    + baseDir.getAbsolutePath());
                        baseDir.deleteOnExit();                        
                    } else {
                        extractTempZipFile(jis, tempDir, name);
                        jis.closeEntry();
                    }
                }
            }
            return new File(tempDir, pkg_name);
        } finally {
            if (is != null)
                is.close();
        }
    }
    
    /**
     * Load a package from CLASSPATH, either from directory, or from an entry in jar
     * @param pkg_name name of package, in relative form, such as "uncertain_builtin_package/uncertain.test/"
     * if the package is in a jar file, the name must ends with path separator ( that is, '/' )
     * @throws IOException
     */
    public void loadPackgeFromClassPath(String pkg_name) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                pkg_name);
        if (url == null)
            throw new IOException("Can't load " + pkg_name);
        String file = url.getFile();
        int index = file.indexOf("!");
        if (index > 0) {
            // from jar
            String jar_file = file.substring(0, index);
            File dir = createTempPackageDir(jar_file, pkg_name);
            loadPackage(dir.getAbsolutePath());
        } else {
            // from directory
            try {
                loadPackage(new File(new URI (url.toString())).getAbsolutePath());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        PackageManager pm = new PackageManager();
        pm.loadPackgeFromClassPath("uncertain_builtin_package/uncertain.test/");
        System.out.println(pm.getSchemaManager().getAllTypes());
        //ComponentPackage pkg = pm.getPackage("uncertain.test");
        //System.out.println(pkg);
    }

    
}
