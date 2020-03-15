package application;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class Importer {
	private List<RedditPost> redditPosts; //stores all crawled reddit posts
	private List<TwitterPost> twitterPosts; //stores all the crawled tweets
	public Importer(String redditFile, String twitterFile) {
		//Creates new lists
		this.redditPosts = new ArrayList<RedditPost>();
		this.twitterPosts = new ArrayList<TwitterPost>();
		importTwitter(twitterFile);
		importReddit(redditFile);
		
	}
	
	public void importReddit(String filePath) {
		//CSVReader csvReader = null;
		//List<String[]> reddit = null;
		//Reader reader = null;
		try {
			//Reader reader = Files.newBufferedReader(Paths.get(filePath));
			//CSVReader csvReader = new CSVReader(reader);
			CSVReader csvReader = new CSVReader(new FileReader(filePath));
			List<String[]> reddit = csvReader.readAll();
			while (!reddit.isEmpty()) {
				String[] currline = reddit.get(0);
				if (currline[0].equalsIgnoreCase("START POST")) {
					reddit.remove(0);
					String[] post = reddit.get(0);
					List<RedditComment> rcomment = new ArrayList<RedditComment>();
					reddit.remove(0);
					reddit.remove(0);
					while (!reddit.get(0)[0].equalsIgnoreCase("END COMMENTS")) {
						String[] comment = reddit.get(0);
						RedditComment rc = new RedditComment(comment[0], Integer.parseInt(comment[1]), comment[2], comment[3], comment[5]);
						rcomment.add(rc);
						reddit.remove(0);
					}
					
					RedditPost rpost = new RedditPost(post[0], Integer.parseInt(post[1]), post[3], post[2], post[4], post[6], rcomment.size(), post[5]);
					rpost.addCommentList(rcomment);
					redditPosts.add(rpost);
					reddit.remove(0);
				}
				else {
					reddit.remove(0);
				}
			}
			
			csvReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void importTwitter(String filePath) {
		CSVReader csvReader = null;
		List<String[]> twitter = null;
		Reader reader = null;
		try {
			reader = Files.newBufferedReader(Paths.get(filePath));
	        csvReader = new CSVReader(reader);
			twitter = csvReader.readAll();
			for (String[] line : twitter) {
				TwitterPost tpost = new TwitterPost(line[3], Integer.parseInt(line[1]), line[0], line[4], Integer.parseInt(line[2]), line[5]);
				twitterPosts.add(tpost);
			}
			csvReader.close();
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			System.out.println("Unable to import Twitter CSV!");
		}
		
		
	}

	public List<RedditPost> getRedditPosts() {
		return redditPosts;
	}

	public List<TwitterPost> getTwitterPosts() {
		return twitterPosts;
	}
	
	
}
