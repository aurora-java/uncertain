package uncertain.composite;

import java.util.Comparator;

public class CompositeMapComparator implements Comparator {

	SortField[] sortFields;

	public CompositeMapComparator(String[] fields) {
		sortFields = new SortField[fields.length];
		for (int i = 0; i < sortFields.length; i++) {
			sortFields[i] = new SortField(fields[i]);
		}
	}

	public CompositeMapComparator(SortField[] sortFields) {
		this.sortFields = sortFields;
	}

	public int compare(Object o1, Object o2) {
		if (o1 == null || o2 == null)
			return 0;
		if (!(o1 instanceof CompositeMap) || !(o2 instanceof CompositeMap))
			return 0;
		if (sortFields == null)
			return 0;
		int result = 0;
		CompositeMap c1 = (CompositeMap) o1;
		CompositeMap c2 = (CompositeMap) o2;
		for (int i = 0; i < sortFields.length; i++) {
			String v1 = c1.getString(sortFields[i].getField());
			String v2 = c2.getString(sortFields[i].getField());
			int cs = compareString(v1, v2);
			if (cs != 0) {
				result = sortFields[i].isAsc() ? cs : cs * -1;
				break;
			}
		}
		return result;
	}

	private int compareString(String s1, String s2) {
		if (s1 == null) {
			if (s2 == null)
				return 0;
			else
				return -1;
		} else {
			if (s2 == null)
				return 1;
			else {
				return s1.compareTo(s2);
			}
		}

	}

}
