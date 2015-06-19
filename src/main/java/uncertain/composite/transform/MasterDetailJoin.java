package uncertain.composite.transform;

import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class MasterDetailJoin extends AbstractEntry {
	String model;
	String detailModel;
	String joinField;
	String detailPath;
	String[] joinFieldArray;

	@Override
	public void run(ProcedureRunner runner) {
		if (joinFieldArray.length == 0)
			return;
		CompositeMap contextMap = runner.getContext();
		CompositeMap modelMap = (CompositeMap) contextMap.getObject(this.model);
		CompositeMap childModelMap = (CompositeMap) contextMap
				.getObject(this.detailModel);
		Iterator it = modelMap.getChildIterator();
		List childList = childModelMap.getChildsNotNull();
		if (it == null)
			return;
		if (childList == null)
			return;
		CompositeMap record;
		CompositeMap childRecord;
		String value1;
		String value2;
		boolean isChild = false;
		while (it.hasNext()) {
			record = (CompositeMap) it.next();
			for (int i = 0, l = childList.size(); i < l; i++) {
				childRecord = (CompositeMap) childList.get(i);
				isChild = true;
				for (String joinKey : joinFieldArray) {
					value1 = record.getString(joinKey);
					value2 = childRecord.getString(joinKey);
					if (value1 == null || !value1.equalsIgnoreCase(value2)) {
						isChild = false;
						break;
					}
				}
				if (isChild) {
					childRecord = ((CompositeMap) childRecord.clone());
					if(this.detailPath!=null)
						record.createChildByTag(this.detailPath).addChild(childRecord);
					else
						record.addChild(childRecord);					
				}
			}
		}		
	}

	public String getJoinField() {
		return joinField;
	}

	public void setJoinField(String joinField) {
		this.joinField = joinField;
		this.joinFieldArray = joinField.split(",");
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDetailModel() {
		return detailModel;
	}

	public void setDetailModel(String detailModel) {
		this.detailModel = detailModel;
	}

	public String getDetailPath() {
		return detailPath;
	}

	public void setDetailPath(String detailPath) {
		this.detailPath = detailPath;
	}

}
