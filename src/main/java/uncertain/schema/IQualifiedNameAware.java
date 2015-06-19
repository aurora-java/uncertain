/*
 * Created on 2009-7-26
 */
package uncertain.schema;

/**
 * Schema object that need to resolve qualified name from prefix + local name
 * @author Zhou Fan
 *
 */
public interface IQualifiedNameAware {

    /** Invoked by owning schema to resolve qualified name */
    public void resolveQName( IQualifiedNameResolver resolver );
}
