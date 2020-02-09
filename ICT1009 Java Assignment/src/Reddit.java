import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Reddit {
	private Document doc;

	public Reddit(String query) throws IOException {
		this.doc = Jsoup.connect("https://old.reddit.com/search/?q=" + query)
				.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
				.get();
	}

	public void crawlReddit() throws IOException {
		FileWriter fileWriter = new FileWriter("reddit.csv");
		CSVWriter writer = new CSVWriter(fileWriter);
		System.out.println("==========CRAWLING REDDIT SEARCH==========");
		
		//headers to the csv to mark start and stop
		String[] csvHeader = {"Title", "Score", "Date", "Author", "Subreddit"};
		writer.writeNext(csvHeader);
		String[] commentHeader = {"Username", "Score", "Date", "Comment"};
		String [] startPost = {"START POST"};
		String [] endPost = {"END POST"};
		
		//pulling the post details from the Document
		List<String> postTitles = this.doc.select("div.search-result-link header.search-result-header > a[href]").eachText();
		List<String> postLinks = this.doc.select("div.search-result-link header.search-result-header > a[href]").eachAttr("href");
		List<String> postTimeStamps = this.doc.select("span.search-time > time[title]").eachAttr("title");
		List<String> postPoints = this.doc.select("div.search-result-link div.search-result-meta > span.search-score").eachText();
		List<String> postAuthors = this.doc.select("div.search-result-link div.search-result-meta > span.search-author > a.author").eachText();
		List<String> postSubs = this.doc.select("div.search-result-meta > span > a.search-subreddit-link").eachText();
		
		//writing to CSV
		for (int i = 0; i < postTitles.size(); i++) {
			//prints post header
			writer.writeNext(startPost);
			//prints post to csv
			String[] postContent = {postTitles.get(i), postPoints.get(i), postTimeStamps.get(i), postAuthors.get(i), postSubs.get(i)};
			writer.writeNext(postContent);
			//writes header for comments
			writer.writeNext(commentHeader);
			//crawl comments to print comments
			System.out.println("====CRAWLING " + postTitles.get(i) + "======");
			crawlComments(postLinks.get(i)+"?sort=confidence&limit=500", writer);
			writer.writeNext((endPost));
		}
		//closes file output
		writer.close();
		fileWriter.close();
	}

	public void crawlComments(String url, CSVWriter writer) throws IOException {
		Document commentsPage = Jsoup.connect(url).userAgent(
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
				.get();
		
		//headers to mark start and stop of comment section
		String [] startComment = {"START COMMENTS"};
		writer.writeNext(startComment);
		String [] endComment = {"END COMMENTS"};
		
		//extracting comments from comments page
		List<String> commentAuthor = commentsPage.select("div.commentarea div.comment p.tagline > a.author, div.commentarea div.comment p.tagline > span.author").not("div.commentarea div.comment div.deleted").eachText();
		List<String> commentScore = commentsPage.select("div.commentarea div.comment p.tagline > span.score, div.commentarea div.comment p.tagline > span.score-hidden").not("div.commentarea div.comment div.deleted, span.likes, span.dislikes").eachText();
		List<String> commentDate = commentsPage.select("div.commentarea div.comment p.tagline > time").not("time.edited-timestamp, div.commentarea div.comment div.deleted").eachAttr("title");
		List<String> commentText = commentsPage.select("div.commentarea div.comment form.usertext div.md").not("div.commentarea div.comment div.deleted").eachText();
		
		System.out.println(commentAuthor.size());
		System.out.println(commentScore.size());
		System.out.println(commentDate.size());
		System.out.println(commentText.size());
		
		for (int i = 0; i < commentAuthor.size(); i++) {
			String[] comment = {commentAuthor.get(i), commentScore.get(i), commentDate.get(i), commentText.get(i)};
			writer.writeNext(comment);
		}
		writer.writeNext(endComment);
	}
}
