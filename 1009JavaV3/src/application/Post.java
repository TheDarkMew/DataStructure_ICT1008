package application;
public abstract class Post {
	private String postContent; //also used for reddit post title
	private int LikeCount; //also used for reddit upvotes
	private String username;
	private String PostDate;
	private String sentiment;
	

	public Post(String title, int points, String username, String date, String sentiment) {
		this.postContent = title;
		this.LikeCount = points;
		this.username = username;
		this.PostDate = date;
		this.sentiment = sentiment;
	}

	public String getPostContent() {
		return postContent;
	}

	public int getLikeCount() {
		return LikeCount;
	}

	public String getUsername() {
		return username;
	}

	public String getPostDate() {
		return PostDate;
	}
	
	public String getSentiment() {
		return sentiment;
	}
}
