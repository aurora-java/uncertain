package uncertain.composite.transform;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.composite.CompositeMap;

public class GroupCompositeMapWithFields {
	private static GroupCompositeMapWithFields default_instance;
	
	private GroupCompositeMapWithFields(){
	}
	public static GroupCompositeMapWithFields getInstance(){
		if(default_instance == null)
			default_instance = new GroupCompositeMapWithFields();
		return default_instance;
	}
	
	
	public List groupCompositeMap(CompositeMap data, String[] groupfields) {
		if (data == null || groupfields == null
				|| data.getChildsNotNull().size() < 2) {
			return null;
		}
		Iterator childs = data.getChildsNotNull().iterator();
		CompositeMap preRecord = null;
		int rowNum = 0;
		BreakPoints bp = new BreakPoints(data.getChildsNotNull().size(),
				groupfields.length);
		while (childs.hasNext()) {
			CompositeMap nowRecord = (CompositeMap) childs.next();
			if (preRecord != null) {
				boolean breakbegin = false;
				for (int y = 0; y < groupfields.length; y++) {
					if (breakbegin) {
						bp.addPoint(rowNum, y);
						continue;
					}
					String key = groupfields[y];
					Object nowObject = nowRecord.get(key);
					Object preObject = preRecord.get(key);
					if(nowObject ==null){
						if(preObject ==null)continue;
						else breakbegin = true;
					}
					else if (!nowRecord.get(key).equals(preRecord.get(key))){
						breakbegin = true;
					}
					if (breakbegin) {
						bp.addPoint(rowNum, y);
					}
				}
			}
			preRecord = nowRecord;
			rowNum++;
		}

		return bp.getMergeRange();
	}

	class BreakPoints {
		int[][] matric;
		int[] columnIndex;

		BreakPoints(int rowCount, int columnCount) {
			matric = new int[columnCount][rowCount];
			columnIndex = new int[columnCount];
		}

		void addPoint(int row, int column) {
			matric[column][columnIndex[column]] = row;
			columnIndex[column]++;
		}

		List getMergeRange() {
			LinkedList list = new LinkedList();
			for (int x = 0; x < matric.length; x++) {
				int preValue = 0;
				for (int y = 0; y < matric[x].length; y++) {
					int value = matric[x][y];
					if (value == 0){
						if(matric[x].length-preValue>2){
							list.add(new int[] { preValue, x, matric[x].length-1, x });
						}
						break;
					}	
					if (value - preValue > 1) {
						list.add(new int[] { preValue, x, value - 1, x });
					}
					preValue = value;
				}
			}
			return list;
		}
	}

	public static void main(String[] args) {
		CompositeMap target = new CompositeMap();
		String[][] rows = new String[][] {
				{ "a1", "a2", "a3", "a4", "a5", "a6" },
				{ "a1", "a2", "a4", "a5", "a7", "a9" },
				{ "a2", "a3", "a4", "a9", "a2", "a6" },
				{ "a3", "a2", "a4", "a6", "a3", "a7" },
				{ "a3", "a8", "a1", "a4", "a4", "a8" },
				{ "a3", "a7", "a2", "a3", "a5", "a9" } };
		String[] allFields = new String[] { "B1", "B2", "B3", "B4", "B5", "B6" };
		for (int x = 0; x < rows.length; x++) {
			CompositeMap child = new CompositeMap();
			for (int y = 0; y < rows[x].length; y++) {
				child.put(allFields[y], rows[x][y]);
			}
			target.addChild(child);
		}

		String[] groupfields = new String[] { "B1", "B2", "B3" };
		GroupCompositeMapWithFields test = new GroupCompositeMapWithFields();
		System.out.println(target.toXML());
		List mergeRange = test.groupCompositeMap(target, groupfields);

		Iterator it = mergeRange.iterator();
		while (it.hasNext()) {
			System.out.println();
			int[] range = (int[]) it.next();
			for (int i = 0; i < range.length; i++) {
				System.out.print(range[i] + ",");
			}
		}

	}
}
