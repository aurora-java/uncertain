package uncertain.composite;

public class SortField {
	String field;
	boolean asc;
	public SortField(String field){
		this.field = field;
		this.asc = true;
	}
	public SortField(String field,boolean asc){
		this.field = field;
		this.asc = asc;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public boolean isAsc() {
		return asc;
	}
	public void setAsc(boolean asc) {
		this.asc = asc;
	}
}
