/*
 * Created on 2009-7-27
 */
package uncertain.schema;

import uncertain.ocm.PackageMapping;

public final class SchemaConstant {

    /** child type in NamedObjectManager */
    public static final int TYPE_ITYPE = 0;
    public static final int TYPE_ATTRIBUTE = 1;
    public static final int TYPE_CATEGORIE = 2;
    public static final int TYPE_EDITOR = 3;
    public static final int TYPE_WIZARD = 4;
    
    public static final String USAGE_REQUIRED = "required";
    public static final String USAGE_OPTIONAL = "optional";
    public static final String USAGE_PROHIBITED = "prohibited";
    
    public static final String OCCUR_UNBOUNDED = "unbounded";
    public static final String NAME_ARRAY = "array";
    public static final String NAME_ELEMENT = "element";
    public static final String NAME_ATTRIBUTE = "attribute";
    public static final String SCHEMA_NAMESPACE = "http://www.uncertain-framework.org/schema/simple-schema";
    static final PackageMapping SCHEMA_NS_PACKAGE_MAPPING 
    = new PackageMapping(SCHEMA_NAMESPACE, SchemaManager.class.getPackage().getName()); 
    
    

}
