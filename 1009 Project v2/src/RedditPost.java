import java.util.ArrayList;
import java.util.List;

public class RedditPost extends Post{
	private String PostSubreddit;
	private String PostLink;
	private int PostCommentCount;
	private List<RedditComment> commentList;
	
	public RedditPost(String title, int points, String username, String date, String subreddit, String link, int commentCount) {
		super(title, points, username, date);
		this.PostSubreddit = subreddit;
		this.PostLink = link;
		this.PostCommentCount = commentCount;
		this.commentList = new ArrayList<RedditComment>();
	}

	public String getPostSubreddit() {
		return PostSubreddit;
	}

	public String getPostLink() {
		return PostLink;
	}

	public int getPostCommentCount() {
		return PostCommentCount;
	}
	
	public List<RedditComment> getCommentList() {
		return commentList;
	}
	
	public void addComment(RedditComment comment) {
		//create the comment
		
		//add it to the list
		this.commentList.add(comment);
	}
	
}
