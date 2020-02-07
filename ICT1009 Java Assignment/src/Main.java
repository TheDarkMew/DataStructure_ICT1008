import twitter4j.TwitterException;

import java.util.Scanner;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException, TwitterException {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter query: ");
		String query = input.nextLine();
		input.close();
		
		TwitterCrawl searchTwitter = new TwitterCrawl(query);
		searchTwitter.crawlTwitter();
		
		Reddit searchReddit = new Reddit(query);
		searchReddit.crawlReddit();
	}
}
