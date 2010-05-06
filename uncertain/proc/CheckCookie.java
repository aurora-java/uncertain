package uncertain.proc;

import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class CheckCookie extends AbstractEntry {
	 
	public void run(ProcedureRunner runner) throws Exception {
		
		
		runner.fireEvent(this.getName(), new Object[] {this,runner.getContext()});
		System.out.println("sss");
	    	
	}
	String name;
	String field;
	String value;
	String url;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
