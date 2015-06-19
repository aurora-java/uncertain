package uncertain.proc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.exception.BuiltinExceptionFactory;

public class SetElement extends AbstractEntry{
	private String target;
	private String name;
	private String namespace;
	private String prefix;
	private int childLevel = 0;;
	@Override
	public void run(ProcedureRunner runner) throws Exception {
		if(target == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this,"target");
		CompositeMap context = runner.getContext();
		Object object = context.getObject(target);
		if (object == null)
			throw BuiltinExceptionFactory.createDataFromXPathIsNull(this, target);
		if(!(object instanceof CompositeMap))
			throw BuiltinExceptionFactory.createInstanceTypeWrongException(target, CompositeMap.class, object.getClass());
		List result = new LinkedList();
		CompositeUtil.getLevelChilds((CompositeMap)object, childLevel, result);
		if (result == null || result.isEmpty())
			return;
		for (Iterator it = result.iterator(); it.hasNext();) {
			CompositeMap record = (CompositeMap)it.next();
			if(name!= null)
				record.setName(name);
			if(namespace!= null)
				record.setNameSpaceURI(namespace);
			if(prefix!=null)
				record.setPrefix(prefix);
		}
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public int getChildLevel() {
		return childLevel;
	}
	public void setChildLevel(int childLevel) {
		this.childLevel = childLevel;
	}
	
}
