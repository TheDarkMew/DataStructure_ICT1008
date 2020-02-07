import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
				.setOAuthAccessTokenSecret("rntSl0F6SDTcxu4JxbXa4gEwKfPXy7FrYLrWoatDwmvfF");
		this.tf = new TwitterFactory(cb.build());
		this.twitter = tf.getInstance();
	}

	public void crawlTwitter() throws TwitterException, IOException {
		// opens file
		FileWriter fileWriter = new FileWriter("twitter.txt");
		PrintWriter printWriter = new PrintWriter(fileWriter);
		// creates the query to search for
		Query userSearchQuery = new Query(this.query);
		userSearchQuery.setCount(100);
		userSearchQuery.setSince("2020-01-25");

		QueryResult result = twitter.search(userSearchQuery);

		this.crawledTweets = result.getTweets();
		for (Status status : crawledTweets) {
			/*
			 * System.out.println("@" + status.getUser().getScreenName() + ":");
			 * System.out.println(status.getText()); System.out.println("Retweets: "+
			 * status.getRetweetCount()); System.out.println("Favourites: "+
			 * status.getFavoriteCount()+"\n\n\n");
			 */
			printWriter.print("@" + status.getUser().getScreenName() + ":\n");
			printWriter.print(status.getText() + "\n");
			printWriter.print("Retweets: " + status.getRetweetCount() + "\n");
			printWriter.print("Favourites: " + status.getFavoriteCount() + "\n\n");
		}
		printWriter.close();
	}
}