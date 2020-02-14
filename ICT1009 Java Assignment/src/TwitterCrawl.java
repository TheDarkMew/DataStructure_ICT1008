import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterCrawl extends Crawler{
	private ConfigurationBuilder cb;
	private TwitterFactory tf;
	private Twitter twitter;
	private List<Status> crawledTweets;

	public TwitterCrawl() throws IOException {
		this.crawledTweets = new ArrayList<Status>();
		this.cb = new ConfigurationBuilder();
		this.cb.setDebugEnabled(true).setOAuthConsumerKey("uxP2xkp0iGQP7UKB70PuD4bNe")
				.setOAuthConsumerSecret("nZObs8qkpACDNCt8v9ZsikgXdU22aeU3ChfHZdG6B7FMDQheyc")
				.setOAuthAccessToken("221371528-gyQixF6MxPB79o2FB66E0llBP6rG5UJ31G2EVHZN")
				.setOAuthAccessTokenSecret("rntSl0F6SDTcxu4JxbXa4gEwKfPXy7FrYLrWoatDwmvfF")
				.setTweetModeExtended(true);
		this.tf = new TwitterFactory(cb.build());
		this.twitter = tf.getInstance();
	}

	public void crawlMedia(){
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
		String[] header = {"Username", "Retweets", "Favourites", "Post"};
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
		this.crawledTweets = result.getTweets();
		for (Status status : crawledTweets) {
			String tweetText;
			String favCount;
			String rtCount;
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
			String[] tweet = {status.getUser().getScreenName(), rtCount , favCount, tweetText};
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
}