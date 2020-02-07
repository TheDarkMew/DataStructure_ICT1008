import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Reddit {
	private Document doc;
	// FileWriter fileWriter;
	// PrintWriter printWriter;

	public Reddit(String query) throws IOException {
		this.doc = Jsoup.connect("https://old.reddit.com/search/?q=" + query).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();
		// this.fileWriter = new FileWriter("java\\reddit.txt");
		// this.printWriter = new PrintWriter(fileWriter);
	}

	public void crawlReddit() throws IOException {
		FileWriter fileWriter = new FileWriter("reddit.txt");
		PrintWriter printWriter = new PrintWriter(fileWriter);
		System.out.println("==========CRAWLING REDDIT SEARCH==========");
		Elements searchResults = doc.select(
				"div.search-result-link header.search-result-header > a[href], div.search-result-link div.search-result-meta > span,  span.search-time > time[title]")
				.not("span.search-result-icon"); // looks for links under the search results for links' header.
		if (searchResults.isEmpty()) {
			System.out.println("FUCK");
		}
		int i = 1; // index of posts
		String url = "";
		System.out.println(searchResults.size());
		for (Element result : searchResults) {
			// title, (blank?), points, exact post timestamp, OP, subreddit
			if (result.tagName().equals("a") || i == searchResults.size() + 1) {
				if (i != 1) {
					System.out.println((i));
					printWriter.print("\nComments:\n");
					crawlComments(url, printWriter);
				}
				// System.out.println("\n==================================POST=======================================================");
				// System.out.println("Post Title: " + result.text());
				printWriter.print("Post Title: " + result.text() + "\n");
				url = result.attr("abs:href");
			} else if (result.tagName().equals("time")) {
				// System.out.println("Posted on: " + result.attr("title"));
				printWriter.print("Posted on: " + result.attr("title") + "\n");
			} else {
				// System.out.println(result.text());
				printWriter.print(result.text() + "\n");
			}
			i++;
		}
		printWriter.close();
	}

	public void crawlComments(String url, PrintWriter printWriter) throws IOException {
		System.out.println("==========CRAWLING COMMENTS==========");
		Document commentsPage = Jsoup.connect(url).userAgent(
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
				.get();
		Elements comments = commentsPage.select(
				"div.commentarea p.tagline > a.author, p.tagline > span.author, p.tagline > span.unvoted, div.commentarea p.tagline > time[title], div.thing div.usertext-body div.md > p");
		for (Element comment : comments) {
			// every 2 elements: commenter username, comment
			if (comment.tagName().equals("a")) {
				// System.out.println("\nComment by: " + comment.text());
				printWriter.print("\nComment by: " + comment.text() + "\n");
			} else if (comment.tagName().equals("time")) {
				// System.out.println("Posted: " + comment.attr("title"));
				printWriter.print("Posted on: " + comment.attr("title") + "\n");
			} else if (comment.tagName().equals("span")) {
				if (comment.attr("class").equals("score unvoted")) {
					// System.out.println("Score: "+ comment.text());
					printWriter.print("Comment Score: " + comment.text() + "\n");
				} else {
					// System.out.println("\nDeleted Comment: " +comment.text());
					printWriter.print("\nDeleted Comment: " + comment.text() + "\n");
				}
			} else {
				// System.out.println(comment.text());
				printWriter.print(comment.text() + "\n");
			}
		}
		printWriter.print("\n");
	}
}
