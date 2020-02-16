
public class Main {
	public static void main(String[] args) {
		//start program
		//prompts user if he wishes to crawl for data -> yes, will crawl (will take a while), no, will go straight to gui.
		//gui will be empty at first. click generate, will run all the analyzer and populate the gui
		
		Crawler c = new Crawler();
		for (RedditPost post : c.getRedditPosts()) {
			System.out.println(post.getPostContent());
			for(RedditComment comment : post.getCommentList()) {
				System.out.println(comment.getCommentText());
			}
		}
	}
}
