package application;

public class TwitterPost extends Post{
	private int RTCount;

	public TwitterPost(String title, int points, String username, String date, int rt) {
		super(title, points, username, date);
		this.RTCount = rt;
	}

	public int getRTCount() {
		return RTCount;
	}
	
	
}
