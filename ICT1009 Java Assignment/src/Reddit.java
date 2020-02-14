import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Reddit extends Crawler {
	private Document doc;

	public Reddit() throws IOException {
		
	}

	public void crawlMedia(){
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
		for (String query : Crawler.getSearchTerms()) {
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
						crawlComments(postLinks.get(i)+"?sort=confidence&limit=500", writer, wcWriter);
					} catch (IOException e) {
						System.out.println("Unable to write to file!");
					}
				}
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

	public void crawlComments(String url, CSVWriter writer, PrintWriter wcwriter) throws IOException {
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
		List<String> commentDate = commentsPage.select("div.commentarea div.comment p.tagline > time[title]").not("time.edited-timestamp, div.commentarea div.comment div.deleted").eachAttr("title");
		List<String> commentText = commentsPage.select("div.commentarea div.comment div.usertext-body div.md").not("div.commentarea div.comment div.deleted").eachText();
		
		int lsize = Math.min(commentAuthor.size(), commentText.size());
		for (int i = 0; i < lsize; i++) {
			String[] comment = {commentAuthor.get(i), commentScore.get(i), commentDate.get(i), commentText.get(i)};
			writer.writeNext(comment);
			wcwriter.println(commentText.get(i));
		}
		writer.writeNext(endComment);
	}
}
