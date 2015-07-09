/*
 * Created on 2005-7-20
 */
package uncertain.ocm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.core.ConfigurationError;
import uncertain.mbean.IMBeanNameProvider;
import uncertain.mbean.IMBeanRegister;
import uncertain.mbean.IMBeanRegistrable;
import uncertain.ocm.mbean.ClassMappingWrapper;
import uncertain.ocm.mbean.FeatureMapping;
import uncertain.ocm.mbean.PackageMappingWrapper;

/**
 * Maitains mapping between XML namespace, element name and Java package, class
 * 
 * @author Zhou Fan
 * 
 */
public class ClassRegistry implements IClassLocator, IMBeanRegistrable,
        ClassRegistryMBean {

    // namespace -> IClassLocator
    HashMap namespace_map;
    // element name -> IClassLocator
    HashMap element_map;
    // ElementIdentifier -> List<Class> , for attached feature
    HashMap feature_map;

    public ClassRegistry() {
        namespace_map = new HashMap();
        element_map = new HashMap();
        feature_map = new HashMap();
    }

    /**
     * Associate a namespace with an IClassLocator
     */
    public void register(String namespace, IClassLocator cl) {
        namespace_map.put(namespace, cl);
    }

    /**
     * Add a package mapping
     * 
     * @param m
     */
    public void addPackageMapping(PackageMapping m) {
        // System.out.println("adding package mapping:"+m);
        String ns = m.getNameSpace();
        if (ns == null)
            ns = m.getPackageName();
        if (ns == null)
            throw new ConfigurationError(
                    "must specify PackageName for package mapping");
        register(ns, m);
    }

    public void addClassMapping(ClassMapping m) {
        element_map.put(m.getElementName(), m);
    }

    public void addClassMapping(String element_name, Class cls) {
        addClassMapping(element_name, cls.getName());
    }

    public void addClassMapping(String element_name, String class_name) {
        ClassMapping m = new ClassMapping();
        m.setElementName(element_name);
        m.setClassName(class_name);
        addClassMapping(m);
    }

    /**
     * Register a package mapping between XML namespace and Java package
     * 
     * @param namespace
     *            XML namespace
     * @param package_name
     *            Mapped Java package name
     */
    public PackageMapping registerPackage(String namespace, String package_name) {
        PackageMapping p = new PackageMapping(namespace, package_name);
        register(namespace, p);
        return p;
    }

    /**
     * Register package mapping, using package name as XML namespace Equals to
     * registerPackage(package_name,package_name);
     * 
     * @param package_name
     */
    public PackageMapping registerPackage(String package_name) {
        return registerPackage(package_name, package_name);
    }

    /**
     * Register a exact mapping between XML namespace, element name to Java
     * package, class name
     * 
     * @param namespace
     *            XML namespace
     * @param element_name
     *            XML element name ( or name of CompositeMap )
     * @param package_name
     *            Mapped Java package name
     * @param class_name
     *            Mapped Java class name
     */
    public ClassMapping registerClass(String namespace,
            String element_name, String package_name, String class_name) {
        PackageMapping pm = (PackageMapping) namespace_map.get(namespace);
        if (pm == null)
            pm = registerPackage(namespace, package_name);
        ClassMapping m = new ClassMapping();
        m.setElementName(element_name);
        m.setClassName(class_name);
        pm.addClassMapping(m);
        return m;
    }

    /**
     * Register a element name with specified Java class. Notice: Once
     * registered, any element with specified element_name, no matter what
     * namespace it is under, will be mapped to the same Java class
     * 
     * @param element_name
     *            Name of element
     * @param package_name
     *            Package name of Java class
     * @param class_name
     *            Name of Java class
     */
    public ClassMapping registerClass(String element_name,
            String package_name, String class_name) {
        ClassMapping m = new ClassMapping();
        m.setElementName(element_name);
        m.setClassName(class_name);
        m.setPackageName(package_name);
        addClassMapping(m);
        return m;
    }

    /**
     * Get IClassLocator by registered namespace
     * 
     * @param namespace
     *            XML Namespace
     * @return IClassLocator instance registered with this namespace or null if
     *         not found
     */
    public IClassLocator getClassLocator(String namespace) {
        if(namespace==null)
            return null;
        IClassLocator cl = (IClassLocator) namespace_map.get(namespace);
        return cl;
    }

    /**
     * Implements IClassLocator
     * 
     * @return mapped class name if a proper registry found, null if can't be
     *         mapped to a class
     */
    public String getClassName(CompositeMap config) {
        String element_name = config.getName();
        if (element_name != null) {
            ClassMapping cm = (ClassMapping) element_map.get(element_name);
            if (cm != null)
                return cm.getClassName(config);
        }
        IClassLocator cl = getClassLocator(config.getNamespaceURI());
        if (cl != null)
            return cl.getClassName(config);
        else
            return null;
    }

    void addMap(Map one, Map two) {
        Iterator it = two.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (!one.containsKey(entry.getKey()))
                one.put(entry.getKey(), entry.getValue());
        }
    }

    public void addAll(ClassRegistry another) {
        if (this == another)
            return;
        addAll(another, true);
    }

    public void addAll(ClassRegistry another, boolean override) {
        if (override) {
            element_map.putAll(another.element_map);
            namespace_map.putAll(another.namespace_map);
            feature_map.putAll(another.feature_map);
        } else {
            addMap(element_map, another.element_map);
            addMap(namespace_map, another.namespace_map);
            addMap(feature_map, another.feature_map);
        }
    }

    public void attachFeature(QualifiedName qname, Class feature_class) {
        List fList = (List) feature_map.get(qname);
        if (fList == null) {
            fList = new LinkedList();
            feature_map.put(qname, fList);
        }
        fList.add(feature_class);
    }

    public void attachFeature(String namespace, String element_name,
            Class feature_class) {
        attachFeature(new QualifiedName(namespace, element_name), feature_class);
    }

    public List getFeatures(QualifiedName eid) {
        List fList = (List) feature_map.get(eid);
        return fList;
    }

    public List getFeatures(String namespace, String element_name) {
        QualifiedName eid = new QualifiedName(namespace, element_name);
        return getFeatures(eid);
    }

    public List getFeatures(CompositeMap config) {
        return getFeatures(config.getQName());
    }

    public Map getFeatureMap() {
        return feature_map;
    }

    public void addFeatureAttach(FeatureAttach f) throws ClassNotFoundException {
        Class fClass = Class.forName(f.getFeatureClass());
        attachFeature(f.getNameSpace(), f.getElementName(), fClass);
    }

    public PackageMapping[] getPackageMappings() {
        Object[] arrays = namespace_map.values().toArray();
        PackageMapping[] pa = new PackageMapping[arrays.length];
        System.arraycopy(arrays, 0, pa, 0, arrays.length);
        return pa;
    }

    public int getPackageMappingCount() {
        return namespace_map.size();
    }

    public int getClassMappingCount() {
        return element_map.size();
    }
    
    public int getFeatureAttachCount(){
        return feature_map.size();
    }

    public ClassMapping[] getClassMappings() {
        Object[] arrays = element_map.values().toArray();
        ClassMapping[] ca = new ClassMapping[arrays.length];
        System.arraycopy(arrays, 0, ca, 0, arrays.length);
        return ca;
    }

    public void registerMBean(IMBeanRegister register,
            IMBeanNameProvider name_provider)
            throws MalformedObjectNameException,
            InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException {

        String reg_name = name_provider.getMBeanName("BuiltinInstances",
                "instanceType=ClassRegistry");
        register.register(reg_name, this);

        PackageMapping[] pms = getPackageMappings();
        for (int i = 0; i < pms.length; i++) {
            String n = name_provider.getMBeanName("BuiltinInstances",
                    "instanceType=ClassRegistry,Array=PackageMappings,packageName="
                            + pms[i].getPackageName());
            register.register(n, new PackageMappingWrapper(pms[i]));
        }
        
        ClassMapping[] cms = getClassMappings();
        for (int i = 0; i < cms.length; i++) {
            String n = name_provider.getMBeanName("BuiltinInstances",
                    "instanceType=ClassRegistry,Array=ClassMappings,className="
                            + cms[i].toString());
            register.register(n, new ClassMappingWrapper(cms[i]));
        }     

        Set local_name_set = new HashSet();
        Iterator it = feature_map.entrySet().iterator();
        int id=0;
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            QualifiedName qname = (QualifiedName)entry.getKey();
            List lst = (List)entry.getValue();
            FeatureMapping fm = new FeatureMapping(qname,lst);
            
            String local_name = qname.getLocalName();
            if(local_name_set.contains(local_name))
                local_name = local_name + "." + id;
            else
                local_name_set.add(local_name);
            String n = name_provider.getMBeanName("BuiltinInstances",
                    "instanceType=ClassRegistry,Array=FeatureAttaches,elementName="
                            + local_name);
            register.register(n, fm);
            id++;
        }
        local_name_set.clear();

    }

}
