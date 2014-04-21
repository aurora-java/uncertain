/*
 * Created on 2005-10-29
 */
package uncertain.composite;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uncertain.util.GroupObjectProcessorImpl;
import uncertain.util.IGroupObjectProcessor;
import uncertain.util.IRecordFilter;

/**
 * Provides some static utility method
 * 
 * @author Zhou Fan
 * 
 */
public class CompositeUtil {
    
    static final boolean use_uuid = "true".endsWith(System.getProperty("uncertain.composite.use_uuid","false"));

	public static final String ANY_VALUE = "*";
	public static final String NULL_VALUE = "null";

	public static class ChildFinder implements IterationHandle {

		public String element_name;
		public String attrib_name;
		public String attrib_value;

		CompositeMap result;

		public CompositeMap getResult() {
			return result;
		}

		public int process(CompositeMap map) {
			if (element_name != null)
				if (!CompositeUtil.compare(map.getName(), element_name))
					return IterationHandle.IT_CONTINUE;
			if (attrib_name != null) {
				Object vl = map.get(attrib_name);
				if (CompositeUtil.compare(vl, attrib_value)) {
					result = map;
					return IterationHandle.IT_BREAK;
				}
			} else {
				result = map;
				return IterationHandle.IT_BREAK;
			}
			return IterationHandle.IT_CONTINUE;
		}

		/**
		 * @param element_name
		 * @param attrib_name
		 * @param attrib_value
		 */
		public ChildFinder(String element_name, String attrib_name,
				String attrib_value) {
			this.element_name = element_name;
			this.attrib_name = attrib_name;
			this.attrib_value = attrib_value;
		}
	};

	public static class ChildsFinder implements IterationHandle {

		private String element_name;
		private String attrib_name;
		private String attrib_value;

		private LinkedList result = new LinkedList();;

		public LinkedList getResult() {
			return result;
		}

		public int process(CompositeMap map) {

			if (element_name != null)
				if (!CompositeUtil.compare(map.getName(), element_name))
					return IterationHandle.IT_CONTINUE;
			if (attrib_name != null) {
				Object vl = map.get(attrib_name);
				if (CompositeUtil.compare(vl, attrib_value)) {
					result.add(map);
				}
			} else {
				result.add(map);
			}
			return IterationHandle.IT_CONTINUE;
		}

		/**
		 * @param element_name
		 * @param attrib_name
		 * @param attrib_value
		 */
		public ChildsFinder(String element_name, String attrib_name,
				String attrib_value) {
			this.element_name = element_name;
			this.attrib_name = attrib_name;
			this.attrib_value = attrib_value;
		}
	};

	public static class PrefixMappingHolder implements IterationHandle {

		static final String ns_prefix = "ns";
		int sequence = 1;
		Set prefix_set = new HashSet();
		Map prefix_map = new HashMap();

		public String getUniquePrefix() {
			String prefix = ns_prefix + sequence++;
			for (; prefix_set.contains(prefix); prefix = ns_prefix + sequence++)
				;
			return prefix;
		}

		public int process(CompositeMap map) {
			String url = map.getNamespaceURI();
			Map mapping = map.getNamespaceMapping();
			if (mapping != null)
				prefix_map.putAll(mapping);
			if (url != null) {
				if (!prefix_map.containsKey(url)) {
					String prefix = map.getPrefix();
					if (prefix == null)
						prefix = getUniquePrefix();
					if (prefix_set.contains(prefix))
						prefix = getUniquePrefix();
					prefix_set.add(prefix);
					prefix_map.put(url, prefix);
				}
			}
			return IterationHandle.IT_CONTINUE;
		}

		public Map getPrefixMapping() {
			return prefix_map;
		}
	}

	/**
	 * @param map
	 * @return a Map with namespace url as key, prefix as value
	 */
	public static Map getPrefixMapping(CompositeMap map) {
		CompositeUtil.PrefixMappingHolder holder = new CompositeUtil.PrefixMappingHolder();
		map.iterate(holder, true);
		Map prefix_mapping = holder.getPrefixMapping();
		return prefix_mapping;
	}

	static boolean compare(Object field, String value) {
		if (field == null) {
			if (value == null)
				return true;
			if (NULL_VALUE.equals(value))
				return true;
			else
				return false;
		} else {
			if (ANY_VALUE.equals(value))
				return true;
			else
				return field.toString().equals(value);
		}
	}

	public static boolean compareObject(CompositeMap map, String access_path,
			String value) {
		Object obj = map.getObject(access_path);
		String parsed_value = TextParser.parse(value, map);
		return compare(obj, parsed_value);
	}

	public static boolean compareObjectDirect(CompositeMap map,
			String access_path, String value) {
		Object obj = map.getObject(access_path);
		return compare(obj, value);
	}

	/**
	 * Find a child CompositeMap, with specified element name and attribute
	 * value
	 * 
	 * @param root
	 * @param elementName
	 * @param attribName
	 * @param value
	 * @return
	 */
	public static CompositeMap findChild(CompositeMap root, String elementName,
			String attribName, String value) {
		if (root == null)
			return null;
		ChildFinder cf = new ChildFinder(elementName, attribName, value);
		root.iterate(cf, true);
		return cf.getResult();
	}

	public static CompositeMap findChild(CompositeMap root, QualifiedName qName) {
		if (root == null)
			return null;
		List childs = root.getChilds();
		if (childs == null)
			return null;
		Iterator it = childs.iterator();
		while (it.hasNext()) {
			CompositeMap node = (CompositeMap) it.next();
			QualifiedName nm = node.getQName();
			if (nm != null)
				if (nm.equals(qName))
					return node;
		}
		return null;
	}

	public static CompositeMap findChild(CompositeMap root, String elementName) {
		if (root == null)
			return null;
		ChildFinder cf = new ChildFinder(elementName, null, null);
		root.iterate(cf, true);
		return cf.getResult();
	}

	public static List findChilds(CompositeMap root, String elementName,
			String attribName, String value) {
		if (root == null)
			return null;
		ChildsFinder cf = new ChildsFinder(elementName, attribName, value);
		root.iterate(cf, true);
		return cf.getResult();
	}

	public static List findChilds(CompositeMap root, String elementName) {
		if (root == null)
			return null;
		ChildsFinder cf = new ChildsFinder(elementName, null, null);
		root.iterate(cf, true);
		return cf.getResult();
	}

	/**
	 * Find parent with specified name
	 * 
	 * @param map
	 * @param element_name
	 * @return
	 */
	public static CompositeMap findParentWithName(CompositeMap map,
			String element_name) {
		CompositeMap parent = map.getParent();
		while (parent != null) {
			if (element_name != null)
				if (element_name.equals(parent.getName()))
					return parent;
			parent = parent.getParent();
		}
		return null;
	}

	static int getFunctionType(String name) {
		String funs[] = IGroupObjectProcessor.GROUP_FUNCITONS;
		for (int i = 0; i < funs.length; i++) {
			if (funs[i].equalsIgnoreCase(name))
				return i;
		}
		return -1;
	}

	/** return sum for specified field from a collection containing maps */
	static Object getResult(int type, Collection list, String field) {
		if (list == null)
			return null;
		GroupObjectProcessorImpl gp = new GroupObjectProcessorImpl(type);
		Iterator it = list.iterator();
		// double sum = 0;
		while (it.hasNext()) {
			CompositeMap m = (CompositeMap) it.next();
			Object o = m.getObject(field);
			if (o != null)
				gp.process(o);
		}
		return gp.getObject();
	}

	public static Object groupResult(CompositeMap root, String field,
			String function) {
		int type = getFunctionType(function);
		if (type < 0)
			throw new IllegalArgumentException("function " + function
					+ " is not defined");
		Collection childs = root.getChilds();
		return getResult(type, childs, field);
	}

	private static String getKey(CompositeMap map, Object[] keys) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < keys.length; i++)
			buf.append(map.get(keys[i]));
		return buf.toString();
	}

	/**
	 * Join to list of CompositeMap, put all fields in list2 into the one in
	 * list1 that has the same value get from each element of key_fields
	 * 
	 * @param list1
	 *            The target list to be joined
	 * @param list2
	 *            The source list whose items will be examined and joined into
	 *            list1
	 * @param key_fields
	 *            An array of key
	 */
	public static void join(Collection list1, Collection list2,
			Object[] key_fields) {
		HashMap join_index = new HashMap();
		Iterator it = list1.iterator();
		while (it.hasNext()) {
			CompositeMap item = (CompositeMap) it.next();
			String key = getKey(item, key_fields);
			join_index.put(key, item);
		}
		it = list2.iterator();
		while (it.hasNext()) {
			CompositeMap source = (CompositeMap) it.next();
			String key = getKey(source, key_fields);
			CompositeMap target = (CompositeMap) join_index.get(key);
			if (target != null)
				target.putAll(source);
		}
		join_index.clear();
	}

	/**
	 * Make a CompositeMap simple <root> <field1>1</field1> -> <root
	 * field1="1"/> </root>
	 */
	public static CompositeMap collapse(CompositeMap root) {
		List childs = root.getChilds();
		if (childs == null)
			return root;
		ListIterator it = childs.listIterator();
		while (it.hasNext()) {
			CompositeMap child = (CompositeMap) it.next();
			String text = child.getText();
			if (child.size() == 0 && text != null && !"".equals(text.trim())) {
				root.put(child.getName(), text);
				it.remove();
			} else
				collapse(child);
		}
		return root;
	}

	public static CompositeMap expand(CompositeMap root) {

		List childs = root.getChilds();
		if (childs != null) {
			ListIterator it = childs.listIterator();
			while (it.hasNext()) {
				CompositeMap child = (CompositeMap) it.next();

				expand(child);
			}
		}
		Set keyset = root.keySet();
		Iterator it = keyset.iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			CompositeMap keychild = new CompositeMap(key);
			keychild.setText(root.getString(key));
			root.addChild(keychild);
		}
		HashSet hs = new HashSet();
		hs.addAll(keyset);
		Iterator its = hs.iterator();
		while (its.hasNext()) {
			Object key = its.next();
			root.remove(key);
		}

		return root;

	}

	public static int uniqueHashCode(CompositeMap m) {
	    if(use_uuid){
	        if(m.uuid==null)
	            m.uuid = UUID.randomUUID().toString();
	        return m.uuid.hashCode();
	        
	    }
	    else
	        return System.identityHashCode(m);
	}

	/**
	 * Convert into a two dimension array
	 * 
	 * @param m
	 *            the source CompositeMap
	 * @param fields
	 *            name of attribute that will be put into array
	 * @param filter
	 *            a filter to decide whether a record is acceptable
	 * @return converted 2D Object array
	 */
	public static Object[][] toArray(CompositeMap m, String[] fields,
			IRecordFilter filter) {
		List childs = m.getChilds();
		if (childs == null)
			return null;
		Object[][] data = new Object[childs.size()][fields.length];
		Iterator it = childs.iterator();
		int row = 0;
		while (it.hasNext()) {
			CompositeMap record = (CompositeMap) it.next();
			if (filter != null)
				if (!filter.accepts(record))
					continue;
			for (int col = 0; col < fields.length; col++) {
				data[row][col] = record.get(fields[col]);
			}
			row++;
		}
		return data;
	}

	/**
	 * put childs into a Map, using specified field as key in each child item
	 * 
	 * @param target
	 *            Target Map to hold data
	 * @param data
	 *            Source data containing childs that will be processed
	 * @param key_field
	 *            key field that will identify each child
	 */
	public static void fillMap(Map target, CompositeMap data, Object key_field) {
		Iterator it = data.getChildIterator();
		if (it == null)
			return;
		while (it.hasNext()) {
			CompositeMap item = (CompositeMap) it.next();
			Object key = item.get(key_field);
			target.put(key, item);
		}
	}

	/**
	 * put specified field value into a Map
	 * 
	 * @param target
	 *            Target Map to hold data
	 * @param data
	 *            Source data containing childs that will be processed
	 * @param key_field
	 *            key field that will identify each child
	 * @param value_field
	 *            value field that will be put into target Map
	 */
	public static void fillMap(Map target, CompositeMap data, Object key_field,
			Object value_field) {
		Iterator it = data.getChildIterator();
		if (it == null)
			return;
		while (it.hasNext()) {
			CompositeMap item = (CompositeMap) it.next();
			Object key = item.get(key_field);
			Object value = item.get(value_field);
			target.put(key, value);
		}
	}

	public static Object[][] toArray(CompositeMap m, String[] fields) {
		return toArray(m, fields, null);
	}

	/**
	 * connect attribute from all childs into a string, separated by specified
	 * separator string
	 */
	public static String connectAttribute(CompositeMap root,
			String attrib_name, String separator) {
		if (root == null)
			return null;
		Iterator it = root.getChildIterator();
		if (it == null)
			return null;
		StringBuffer result = new StringBuffer();
		int id = 0;
		while (it.hasNext()) {
			CompositeMap child = (CompositeMap) it.next();
			Object value = child.get(attrib_name);
			if (id > 0)
				result.append(separator);
			result.append(value);
			id++;
		}
		return result.toString();
	}

	public static String connectAttribute(CompositeMap root, String attrib_name) {
		return connectAttribute(root, attrib_name, ",");
	}

	/**
	 * Copy all attributes, from source map to destination map. Do not override
	 * existing attribute.
	 */
	public static void copyAttributes(Map source, Map dest) {
		Iterator it = source.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (!dest.containsKey(entry.getKey()))
				dest.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Merge childs from source to destination. Every child is identified by
	 * specified key attribute Copy all childs from source.If a childs exists in
	 * destination map, all attributes that not defined in dest map will be
	 * copied.
	 * 
	 * @param source
	 *            Source CompositeMap containing childs
	 * @param dest
	 *            Desitination CompositeMap containg childs
	 * @param key
	 *            Key attribute to identify each child CompositeMap item, such
	 *            as "name", "id"
	 */
	public static void mergeChildsByOverride(CompositeMap source,
			CompositeMap dest, Object key) {
		// Map source_cache = new HashMap();
		// fillMap( source_cache, source, key);
		Iterator it = source.getChildIterator();
		if (it == null)
			return;
		Map dest_cache = new HashMap();
		fillMap(dest_cache, dest, key);
		while (it.hasNext()) {
			CompositeMap source_item = (CompositeMap) it.next();
			Object value = source_item.get(key);
			CompositeMap dest_item = (CompositeMap) dest_cache.get(value);
			if (dest_item != null)
				copyAttributes(source_item, dest_item);
			else
				dest.addChild((CompositeMap) source_item.clone());
		}
		dest_cache.clear();
	}

	/**
	 * For each child item in destination CompositeMap, if a child item with
	 * same key exists in source CompositeMap, then copy all attributes that not
	 * defined in destination map from source.
	 * 
	 * @param source
	 *            Source CompositeMap containing childs
	 * @param dest
	 *            Desitination CompositeMap containg childs
	 * @param key
	 *            Key attribute to identify each child CompositeMap item, such
	 *            as "name", "id"
	 */
	public static void mergeChildsByReference(CompositeMap source,
			CompositeMap dest, Object key) {
		Iterator it = dest.getChildIterator();
		if (it == null)
			return;
		Map source_cache = new HashMap();
		fillMap(source_cache, source, key);
		while (it.hasNext()) {
			CompositeMap dest_item = (CompositeMap) it.next();
			Object value = dest_item.get(key);
			CompositeMap source_item = (CompositeMap) source_cache.get(value);
			if (source_item != null)
				copyAttributes(source_item, dest_item);
		}
		source_cache.clear();
	}

	public static void getLevelChilds(CompositeMap source, int level, List result) {
		if (source == null)
			return;
		if (level == 0)
			result.add(source);
		if (source.getChilds() == null)
			return;
		if (level == 1) {
			result.addAll(source.getChilds());
		} else if (level > 1) {
			for (Iterator it = source.getChildIterator(); it.hasNext();) {
				getLevelChilds((CompositeMap) it.next(), level - 1, result);
			}
		}
	}

}
