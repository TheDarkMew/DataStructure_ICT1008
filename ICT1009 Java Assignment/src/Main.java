import twitter4j.TwitterException;

import java.util.Scanner;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException, TwitterException {
		Crawler searchTwitter = new TwitterCrawl();
		searchTwitter.crawlMedia();
		Reddit searchReddit = new Reddit();
		searchReddit.crawlMedia();
	}
}
