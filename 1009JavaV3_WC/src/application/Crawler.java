package application;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.opencsv.CSVWriter;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/*
 * Web Crawler done by Juve Wong
 * Crawls Reddit using JSoup and Twitter using Twitter4j
 */

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
		//Creates new lists
		this.redditPosts = new ArrayList<RedditPost>();
		this.twitterPosts = new ArrayList<TwitterPost>();
		//crawls for data from Reddit and Twitter
		crawlReddit();
		crawlTwitter();
	}
	
	public void crawlReddit(){
		//creates files
		FileWriter fileWriter = null;
		FileWriter wordCloudFile = null;
		CSVWriter writer = null;
		PrintWriter wcWriter = null;
		
		try {
			wordCloudFile = new FileWriter("wordcloud.txt");
			fileWriter = new FileWriter("reddit.csv");
			writer = new CSVWriter(fileWriter);
			wcWriter = new PrintWriter(wordCloudFile);
		} catch (IOException e) {
			System.out.println("Please ensure that the CSV files are not open and try again!");
			System.exit(-1);
		}
		
		
		System.out.println("==========CRAWLING REDDIT SEARCH==========");
		
		//headers to the csv to mark start and stop
		String [] startPost = {"START POST"};
		String [] endPost = {"END POST"};
		
		//crawls for data based on the search terms defined for reddit.
		for (String query : redditSearchTerms) {
			try {
				//creates JSoup document of the crawled website
				this.doc = Jsoup.connect("https://old.reddit.com/search/?q=" + query + "&t=month")
						.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
						.get();
			} catch (IOException e) {
				//if Document cannot be created, throw exception and show warning that data cannot be crawled.
				System.out.println("Unable to crawl Reddit for data!");
				redditPosts = null;
				try {
					wordCloudFile.close();
					writer.close();
					fileWriter.close();
					wcWriter.close();
				} catch (IOException ee) {
					// TODO Auto-generated catch block
					System.out.println("Unable to close file!");
				}
				System.exit(-1);
			}
			//pulling the post details from the Document into various lists
			List<String> postTitles = this.doc.select("div.search-result-link header.search-result-header > a[href]").eachText();
			List<String> postLinks = this.doc.select("div.search-result-link header.search-result-header > a[href]").eachAttr("href");
			List<String> postTimeStamps = this.doc.select("span.search-time > time[title]").eachAttr("title");
			List<String> postPoints = this.doc.select("div.search-result-link div.search-result-meta > span.search-score").eachText();
			List<String> postAuthors = this.doc.select("div.search-result-link div.search-result-meta > span.search-author > a.author").eachText();
			List<String> postSubs = this.doc.select("div.search-result-meta > span > a.search-subreddit-link").eachText();
			List<String> postCommentCount = this.doc.select("div.search-result-link div.search-result-meta > a.search-comments").eachText();
			
			//writing each REDDIT POST to CSV
			for (int i = 0; i < 10; i++) {
				//sanitises upvotes and commentcount fields which can contain commas for large values and the word "comments/points" in order to save the numerical value as an integer for data processing
				String points = postPoints.get(i).split(" ")[0].replace(",", "");
				String commentCount = postCommentCount.get(i).split(" ")[0].replace(",", "");
				//rest of the items
				String title = postTitles.get(i);
				String author = postAuthors.get(i);
				String dateposted = postTimeStamps.get(i);
				String subreddit = postSubs.get(i);
				String link = postLinks.get(i);
				String sentiment = genSentiment(title);
				//saves the post to a RedditPost object
				RedditPost post = new RedditPost(title, Integer.parseInt(points), author, dateposted, subreddit, link, Integer.parseInt(commentCount), sentiment);
				//prints post header to csv
				writer.writeNext(startPost);
				//prints post to csv
				String[] postContent = {title, points, dateposted, author, subreddit, sentiment, link};
				writer.writeNext(postContent);
				//crawl comments to print comments if there are comments present in the post
				if (!commentCount.equals("0")) {
					System.out.println("====CRAWLING " + title + "======");
					try {
						crawlRedditComments(link+"?sort=top", writer, wcWriter, post, subreddit);
					} catch (IOException e) {
						System.out.println("Unable to crawl Reddit comments for data!");
						redditPosts = null;
						try {
							wordCloudFile.close();
							writer.close();
							fileWriter.close();
							wcWriter.close();
						} catch (IOException ee) {
							// TODO Auto-generated catch block
							System.out.println("Unable to close file!");
						}
						System.exit(-1);
					}
				}
				//adds post object to the list of posts
				redditPosts.add(post);
				//writes end of post indicator to CSV
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

	public void crawlRedditComments(String url, CSVWriter writer, PrintWriter wcwriter, RedditPost postname, String subreddit) throws IOException {
		//sets JSoup Documentfor the comments page
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
		
//		int csize1 = Math.min(commentAuthor.size(), commentText.size());
//		int csize2 = Math.min(commentScore.size(), commentDate.size());
//		int lsize = Math.min(csize1, csize2);
		
		for (int i = 0; i < 10; i++) {
			//check for specific instances where comment score is hidden (moderator announcements, deleted accounts etc), in which they are set to 0 points.
			int points;
			String author = commentAuthor.get(i);
			String commentPoints = commentScore.get(i);
			String date = commentDate.get(i);
			String commenttxt = commentText.get(i);
			String sentiment = genSentiment(commenttxt);
			try {
				points = Integer.parseInt(commentPoints.split(" ")[0].replace(",", ""));
			}
			catch(Exception e) {
				points = 0;
				commentPoints = "0";
			}
			
			//creates a comment object that will be stored in the post as a list of comments.
			RedditComment commentObj = new RedditComment(author, points, date, commenttxt, sentiment);
			//adds to CSV
			String[] comment = {author, commentPoints, date, commenttxt, subreddit, sentiment};
			//adds comment to post
			postname.addComment(commentObj);
			writer.writeNext(comment);
			wcwriter.println(commenttxt);
		}
		writer.writeNext(endComment);
	}
	
	public void crawlTwitter(){
		//creates the session using the Twitter API Key
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
		CSVWriter writer = null;
		PrintWriter wcWriter = null;
		try {
			fileWriter = new FileWriter("twitter.csv");
			wordCloudFile = new FileWriter("wordcloud.txt", true);
			writer = new CSVWriter(fileWriter);
			wcWriter = new PrintWriter(wordCloudFile);
		} catch (IOException e1) {
			//display error message
			System.out.println("Please ensure that the CSV files are not open and try again!");
			twitterPosts = null;
			System.exit(-1);
		}
		
		// creates the query to search for
		Query userSearchQuery = new Query("(rip kobe bryant) OR (rip black mamba) OR (black mamba forever) OR (black mamba) OR (kobe bryant)");
		userSearchQuery.setCount(25);
		userSearchQuery.setSince("2020-01-25");
		//searches twitter using the query
		QueryResult result = null;
		try {
			result = twitter.search(userSearchQuery);
		} catch (TwitterException e) {
			System.out.println(e);
			System.out.println("Failed to search tweets!");
			twitterPosts = null;
			try {
				wordCloudFile.close();
				writer.close();
				fileWriter.close();
				wcWriter.close();
			} catch (IOException ee) {
				// TODO Auto-generated catch block
				System.out.println("Unable to close file!");
			}
			System.exit(-1);
		}
		for (Status status : result.getTweets()) {	
			String tweetText;
			String favCount;
			String rtCount;
			String postDate;
			String user = status.getUser().getScreenName();
			//if retweeted, gets original tweet text, favourites and retweets
			if (status.getRetweetedStatus() != null) {
				tweetText = status.getRetweetedStatus().getText();
		        favCount = String.valueOf(status.getRetweetedStatus().getFavoriteCount());
		        rtCount = String.valueOf(status.getRetweetedStatus().getRetweetCount());
		    } 
			else {
				//gets tweet text, favourites and retweets
		      	tweetText = status.getText();
		       	favCount = String.valueOf(status.getFavoriteCount());
		       	rtCount = String.valueOf(status.getRetweetCount());
		    }
			String sentiment = genSentiment(tweetText);
			postDate = status.getCreatedAt().toString();
			//saves information to twitterpost object
			TwitterPost post = new TwitterPost(tweetText, Integer.parseInt(favCount), user, postDate, Integer.parseInt(rtCount), sentiment);
			//writes to csv file
			String[] tweet = {user, rtCount , favCount, tweetText, postDate, sentiment};
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
	
	public static String genSentiment(String text) {
		
		RedwoodConfiguration.current().clear().apply(); 
		
		String sentiment = "";
		Properties props = new Properties();
		 props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		 StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		 
		 
		 CoreDocument coreDocument = new CoreDocument(text);
		 pipeline.annotate(coreDocument);
		 
		 
		 List<CoreSentence> sentences = coreDocument.sentences();
		 
		 
		 for(CoreSentence sentence : sentences) {
			 sentiment = sentence.sentiment();
			
			
		 }
		 return sentiment;
	}
	
	//returns the generated list of reddit posts
	public List<RedditPost> getRedditPosts() {
		return this.redditPosts;
	}
	
	//returns the generated list of twitter posts
	public List<TwitterPost> getTwitterPosts() {
		return this.twitterPosts;
	}
	
	
}
