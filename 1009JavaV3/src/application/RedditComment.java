package application;

public class RedditComment{
	private String username;
	private int commentScore;
	private String commentDate;
	private String commentText; 
	private String sentiment;
	
	public RedditComment(String username, int commentScore, String commentDate, String commentText) {
		this.username = username;
		this.commentScore = commentScore;
		this.commentDate = commentDate;
		this.commentText = commentText;
		this.sentiment = null;
	}

	public String getUsername() {
		return username;
	}

	public int getCommentScore() {
		return commentScore;
	}

	public String getCommentDate() {
		return commentDate;
	}

	public String getCommentText() {
		return commentText;
	}

	public String getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
}
