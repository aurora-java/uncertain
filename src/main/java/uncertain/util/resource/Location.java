package uncertain.util.resource;

public class Location {

	int[] location = new int[4];

	public void setStartPoint(int line, int column) {
		location[0] = line;
		location[1] = column;
	}

	public void setEndPoint(int line, int column) {
		location[2] = line;
		location[3] = column;
	}

	public int[] getRange() {
		return location;
	}
	
	public int getStartLine(){
	    return location[0];
	}
	
	public int getStartColumn(){
	    return location[1];
	}
	
	public int getEndLine(){
	    return location[2];
	}
	
	public int getEndColumn(){
	    return location[3];
	}
}
