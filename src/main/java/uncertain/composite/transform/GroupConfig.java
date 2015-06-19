package uncertain.composite.transform;

import java.util.Iterator;

import uncertain.composite.CompositeMap;

public class GroupConfig {
	public static final String KEY_GROUP_KEY_FIELDS = "group_key_fields";
	public static final String KEY_GROUP_ATTRIBUTES = "group_attributes";
	public static final String KEY_RECORD_NAME = "record_name";
	public static final String SPLIT = ",";
	String[] group_key_fields;
	String[] group_attribs;
	String record_name;
	boolean extendParentAttributes = true;
	public GroupConfig(String[] groupKeyFields, String[] groupAttribs, String recordName,boolean extendParentAttributes) {
		super();
		group_key_fields = groupKeyFields;
		group_attribs = groupAttribs;
		record_name = createRecordName(recordName);
		this.extendParentAttributes = extendParentAttributes;
	}
	public GroupConfig(String[] groupKeyFields, String[] groupAttribs, String recordName) {
		super();
		group_key_fields = groupKeyFields;
		group_attribs = groupAttribs;
		record_name = createRecordName(recordName);
	}

	public GroupConfig(String[] groupKeyFields, String recordName) {
		super();
		group_key_fields = groupKeyFields;
		record_name = createRecordName(recordName);
	}

	public GroupConfig(String[] groupKeyFields, String[] groupAttribs) {
		super();
		group_key_fields = groupKeyFields;
		group_attribs = groupAttribs;
		record_name = createRecordName(null);
	}

	public GroupConfig(String[] groupKeyFields) {
		super();
		group_key_fields = groupKeyFields;
		record_name = createRecordName(null);
	}
	public String[] getGroupKeyFields(){
		return group_key_fields;
	}
	public void setGroupKeyFiedls(String[] groupKeyFields){
		this.group_key_fields = groupKeyFields;
	}
	public String[] getGroupAttributes(){
		return group_attribs;
	}
	public void setGroupAttributes(String[] groupAttributes){
		this.group_attribs = groupAttributes;
	}
	public void setRecordName(String recordName){
		this.record_name = recordName;
	}
	public String getRecordName(){
		return record_name;
	}
	public static GroupConfig[] createGroupConfigs(CompositeMap source){
		if(source == null||(source.getChildIterator() == null))
			return null;
		int level = 0;
		GroupConfig[] gourpConfig = new GroupConfig[source.getChilds().size()];
		for(Iterator it = source.getChildIterator();it.hasNext();level++){
			CompositeMap record = (CompositeMap)it.next();
			String groupValue = record.getString(KEY_GROUP_KEY_FIELDS);
			if(groupValue == null)
				continue;
			String groupAttribValue = record.getString(KEY_GROUP_ATTRIBUTES);
			String[] attributes = null;
			if(groupAttribValue != null)
				attributes = groupAttribValue.split(SPLIT);
			if(level != 0)
				gourpConfig[level] = new GroupConfig(groupValue.split(SPLIT), attributes, record.getString(KEY_RECORD_NAME));
			else
				gourpConfig[level] = new GroupConfig(groupValue.split(SPLIT), attributes, record.getString(KEY_RECORD_NAME),false);
		}
		return gourpConfig;
	}
	protected String createRecordName(String recordName){
		if(recordName != null)
			return recordName;
		StringBuffer bf = new StringBuffer("group");
		for(int i=0;i<group_key_fields.length;i++){
			bf.append("_").append(group_key_fields[i]);
		}
		return bf.toString();
	}
	public boolean isExtendParentAttributes() {
		return extendParentAttributes;
	}
	public void setExtendParentAttributes(boolean extendParentAttributes) {
		this.extendParentAttributes = extendParentAttributes;
	}
	
}
