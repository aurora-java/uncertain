/*
 * Created on 2009-6-26
 */
package uncertain.schema;

public class Namespace extends AbstractSchemaObject {

    String prefix;
    String url;

    public Namespace() {
		super();
	}

	public Namespace(String prefix, String url) {
		super();
		this.prefix = prefix;
		this.url = url;
	}

	public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
