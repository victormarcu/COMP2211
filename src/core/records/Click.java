package core.records;

public class Click extends CostRecord {

	// ==== Constructor ====

	public Click(String string) {
		this(string.split(","));
	}

	public Click(String[] data) {
		super(data, 3);
	}
	
	public Click(long dateTime, long userID, double cost) {
		super(dateTime, userID, cost);
	}
}
