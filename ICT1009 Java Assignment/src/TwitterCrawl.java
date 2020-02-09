import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterCrawl {
	private ConfigurationBuilder cb;
	private TwitterFactory tf;
	private Twitter twitter;
	private String query;
	private List<Status> crawledTweets;

	public TwitterCrawl(String query) throws IOException {
		this.query = query;
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

	public void crawlTwitter() throws TwitterException, IOException {
		// opens file
		FileWriter fileWriter = new FileWriter("twitter.csv");
		CSVWriter writer = new CSVWriter(fileWriter);
		//writes the csv header text
		String[] header = {"Username", "Retweets", "Favourites", "Post"};
		writer.writeNext(header);
		//PrintWriter printWriter = new PrintWriter(fileWriter);
		// creates the query to search for
		Query userSearchQuery = new Query(this.query); //+ " +exclude:retweets"
		userSearchQuery.setCount(100);
		userSearchQuery.setSince("2020-01-25");
		//searches
		QueryResult result = twitter.search(userSearchQuery);

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
		}
		writer.close();
	}
}