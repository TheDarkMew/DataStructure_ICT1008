package application;

public class RedditComment{
	private String username;
	private int commentScore;
	private String commentDate;
	private String commentText;
	
	public RedditComment(String username, int commentScore, String commentDate, String commentText) {
		this.username = username;
		this.commentScore = commentScore;
		this.commentDate = commentDate;
		this.commentText = commentText;
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
	
	

}
