import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.opencsv.CSVWriter;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Crawler {
	private Document doc; //jsoup document for reddit search page
	//building the session for Twitter
	private ConfigurationBuilder cb;
	private TwitterFactory tf;
	private Twitter twitter; 
	private String[] redditSearchTerms = {"kobe bryant"};
	private List<RedditPost> redditPosts; //stores all crawled reddit posts
	private List<TwitterPost> twitterPosts; //stores all the crawled tweets
	
	public Crawler() {
		this.redditPosts = new ArrayList<RedditPost>();
		this.twitterPosts = new ArrayList<TwitterPost>();
		crawlReddit();
		crawlTwitter();
	}
	
	public void crawlReddit(){
		FileWriter fileWriter = null;
		FileWriter wordCloudFile = null;
		
		try {
			wordCloudFile = new FileWriter("wordcloud.txt", true);
			fileWriter = new FileWriter("reddit.csv", true);
		} catch (IOException e) {
			System.out.println("Unable to crawl data! Please ensure that the CSV files are not open!");
			System.exit(-1);
		}
		
		CSVWriter writer = new CSVWriter(fileWriter);
		PrintWriter wcWriter = new PrintWriter(wordCloudFile);
		System.out.println("==========CRAWLING REDDIT SEARCH==========");
		
		//headers to the csv to mark start and stop
		String[] csvHeader = {"Title", "Score", "Date", "Author", "Subreddit"};
		writer.writeNext(csvHeader);
		String[] commentHeader = {"Username", "Score", "Date", "Comment"};
		String [] startPost = {"START POST"};
		String [] endPost = {"END POST"};
		for (String query : redditSearchTerms) {
			try {
				this.doc = Jsoup.connect("https://old.reddit.com/search/?q=" + query + "&t=month")
						.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
						.get();
			} catch (IOException e) {
				System.out.println("Unable to crawl Reddit for data!");
				System.exit(-1);
			}
			//pulling the post details from the Document
			List<String> postTitles = this.doc.select("div.search-result-link header.search-result-header > a[href]").eachText();
			List<String> postLinks = this.doc.select("div.search-result-link header.search-result-header > a[href]").eachAttr("href");
			List<String> postTimeStamps = this.doc.select("span.search-time > time[title]").eachAttr("title");
			List<String> postPoints = this.doc.select("div.search-result-link div.search-result-meta > span.search-score").eachText();
			List<String> postAuthors = this.doc.select("div.search-result-link div.search-result-meta > span.search-author > a.author").eachText();
			List<String> postSubs = this.doc.select("div.search-result-meta > span > a.search-subreddit-link").eachText();
			List<String> postCommentCount = this.doc.select("div.search-result-link div.search-result-meta > a.search-comments").eachText();
			
			//writing to CSV
			for (int i = 0; i < postTitles.size(); i++) {
				String points = postPoints.get(i).split(" ")[0].replace(",", "");
				String commentCount = postCommentCount.get(i).split(" ")[0].replace(",", "");
				RedditPost post = new RedditPost(postTitles.get(i), Integer.parseInt(points), postAuthors.get(i), postTimeStamps.get(i), postSubs.get(i), postLinks.get(i), Integer.parseInt(commentCount));
				//prints post header
				writer.writeNext(startPost);
				//prints post to csv
				String[] postContent = {postTitles.get(i), postPoints.get(i), postTimeStamps.get(i), postAuthors.get(i), postSubs.get(i)};
				writer.writeNext(postContent);
				//writes header for comments
				writer.writeNext(commentHeader);
				//crawl comments to print comments if there are comments
				if (!postCommentCount.get(i).equals("0 comments")) {
					System.out.println("====CRAWLING " + postTitles.get(i) + "======");
					try {
						crawlRedditComments(postLinks.get(i)+"?sort=confidence&limit=500", writer, wcWriter, post);
					} catch (IOException e) {
						System.out.println("Unable to write to file!");
					}
				}
				redditPosts.add(post);
				writer.writeNext((endPost));
			}
		}
		
		//closes file output
		
		try {
			wordCloudFile.close();
			writer.close();
			fileWriter.close();
			wcWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to close file!");
		}
	}

	public void crawlRedditComments(String url, CSVWriter writer, PrintWriter wcwriter, RedditPost postname) throws IOException {
		Document commentsPage = Jsoup.connect(url).userAgent(
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
				.get();
		
		//headers to mark start and stop of comment section
		String [] startComment = {"START COMMENTS"};
		writer.writeNext(startComment);
		String [] endComment = {"END COMMENTS"};
		
		//extracting comments from comments page
		List<String> commentAuthor = commentsPage.select("div.commentarea div.comment p.tagline > a.author, div.commentarea div.comment p.tagline > span.author").not("div.commentarea div.comment div.deleted").eachText();
		List<String> commentScore = commentsPage.select("div.commentarea div.comment p.tagline > span.unvoted[title], div.commentarea div.comment p.tagline > span.score-hidden").not("div.commentarea div.comment div.deleted, span.likes, span.dislikes").eachAttr("title");
		List<String> commentDate = commentsPage.select("div.commentarea div.comment p.tagline > time[title]").not("time.edited-timestamp, div.commentarea div.comment div.deleted").eachAttr("title");
		List<String> commentText = commentsPage.select("div.commentarea div.comment div.usertext-body div.md").not("div.commentarea div.comment div.deleted").eachText();
		
		int lsize = Math.min(commentAuthor.size(), commentText.size());
		for (int i = 0; i < lsize; i++) {
			
			int points;
			try {
				points = Integer.parseInt(commentScore.get(i).split(" ")[0].replace(",", ""));
			}
			catch(Exception e) {
				points = 0;
			}
			RedditComment commentObj = new RedditComment(commentAuthor.get(i), points, commentDate.get(i), commentText.get(i));
			
			String[] comment = {commentAuthor.get(i), commentScore.get(i), commentDate.get(i), commentText.get(i)};
			//adds comment to post
			postname.addComment(commentObj);
			writer.writeNext(comment);
			wcwriter.println(commentText.get(i));
		}
		writer.writeNext(endComment);
	}
	
	public void crawlTwitter(){
		this.cb = new ConfigurationBuilder();
		this.cb.setDebugEnabled(true).setOAuthConsumerKey("uxP2xkp0iGQP7UKB70PuD4bNe")
				.setOAuthConsumerSecret("nZObs8qkpACDNCt8v9ZsikgXdU22aeU3ChfHZdG6B7FMDQheyc")
				.setOAuthAccessToken("221371528-gyQixF6MxPB79o2FB66E0llBP6rG5UJ31G2EVHZN")
				.setOAuthAccessTokenSecret("rntSl0F6SDTcxu4JxbXa4gEwKfPXy7FrYLrWoatDwmvfF")
				.setTweetModeExtended(true);
		this.tf = new TwitterFactory(cb.build());
		this.twitter = tf.getInstance();
		
		// opens file
		FileWriter fileWriter = null;
		FileWriter wordCloudFile = null;
		try {
			fileWriter = new FileWriter("twitter.csv", true);
			wordCloudFile = new FileWriter("wordcloud.txt", true);
		} catch (IOException e1) {
			System.out.println("Unable to crawl data! Please ensure that the CSV files are not open!");
			System.exit(-1);
		}
		CSVWriter writer = new CSVWriter(fileWriter);
		PrintWriter wcWriter = new PrintWriter(wordCloudFile);
		//writes the csv header text
		String[] header = {"Username", "Retweets", "Favourites", "Post", "Date Posted"};
		writer.writeNext(header);
		// creates the query to search for
		Query userSearchQuery = new Query("(rip kobe bryant) OR (rip black mamba) OR (black mamba forever) OR (black mamba) OR (kobe bryant) -filter:retweets"); //+ " +exclude:retweets"
		userSearchQuery.setCount(600);
		userSearchQuery.setSince("2020-01-25");
		//searches
		QueryResult result = null;
		try {
			result = twitter.search(userSearchQuery);
		} catch (TwitterException e) {
			System.out.println(e);
			System.out.println("Failed to search tweets!");
			System.exit(-1);
		}
		for (Status status : result.getTweets()) {	
			String tweetText;
			String favCount;
			String rtCount;
			String postDate;
			if (status.getRetweetedStatus() != null) {
				tweetText = status.getRetweetedStatus().getText();
		        favCount = String.valueOf(status.getRetweetedStatus().getFavoriteCount());
		        rtCount = String.valueOf(status.getRetweetedStatus().getRetweetCount());
		    } 
			else {
		      	tweetText = status.getText();
		       	favCount = String.valueOf(status.getFavoriteCount());
		       	rtCount = String.valueOf(status.getRetweetCount());
		    }
			postDate = status.getCreatedAt().toString();
			TwitterPost post = new TwitterPost(tweetText, Integer.parseInt(favCount), status.getUser().getScreenName(), postDate, Integer.parseInt(rtCount));
			String[] tweet = {status.getUser().getScreenName(), rtCount , favCount, tweetText, postDate};
			twitterPosts.add(post);
			writer.writeNext(tweet);
			wcWriter.println(tweetText);
			
		}
		try {
			writer.close();
			wcWriter.close();
			wordCloudFile.close();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to close twitter.csv!");
		}
	}

	public List<RedditPost> getRedditPosts() {
		return this.redditPosts;
	}

	public List<TwitterPost> getTwitterPosts() {
		return this.twitterPosts;
	}
	
	
}
