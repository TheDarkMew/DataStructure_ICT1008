package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;

//import com.google.cloud.language.v1.Document;
//import com.google.cloud.language.v1.Document.Type;
//import com.google.cloud.language.v1.LanguageServiceClient;
//import com.google.cloud.language.v1.Sentiment;

import javafx.util.Pair;

public class Analyzer {
	
	public Analyzer(List<RedditPost> rPosts, List<TwitterPost> tPosts) throws Exception {
		//calling this will start analysis
		//initSentimentAnalyzer(rPosts, tPosts);
		//initWordCloud();
		initStatisticsAnalyzer(rPosts, tPosts);
	}
	//init method for Statistics Analysis - Juve
	public void initStatisticsAnalyzer(List<RedditPost> rPosts, List<TwitterPost> tPosts) {
		StatisticsAnalyzer s = new StatisticsAnalyzer(rPosts, tPosts);
		String popularSubreddit = s.getPopularSubreddit();
		RedditPost popularPost = s.getBestRedditPost();
		RedditComment popularComment = s.getBestRedditComment();
		TwitterPost popularTweet = s.getBestTwitterPost();
		TwitterPost popularRT = s.getMostRTPost();
		int[] redditStats = s.redditPostGeneralStats(); //total posts, total comments, total post upvotes, total comment upvotes
		int[] twitterStats = s.twitterPostGeneralStats(); //total posts, total favs, total retweets
		
		//display the data
		System.out.println("Popular Post:");
		System.out.println(popularPost.getPostContent());
		System.out.println(popularPost.getLikeCount());
		System.out.println(popularPost.getPostCommentCount());
		System.out.println("Popular Comment:");
		System.out.println(popularComment.getCommentText());
		System.out.println(popularComment.getCommentScore());
		System.out.println("Popular Subreddit: "+ popularSubreddit);
		System.out.println("Popular Tweet");
		System.out.println(popularTweet.getPostContent());
		System.out.println(popularTweet.getLikeCount());
		System.out.println("Popular RT:");
		System.out.println(popularRT.getPostContent());
		System.out.println(popularRT.getRTCount());
		System.out.println("Reddit Stats");
		//System.out.println(redditStats);
		System.out.println("Twitter Stats");
		//System.out.println(twitterStats);
	}
	
	//init method for Sentiment Analysis - Kin Seong
	public void initSentimentAnalyzer(List<RedditPost> rPosts, List<TwitterPost> tPosts) throws IOException, Exception {
		// Instantiates a client
	    
	    //Insert array of comments under the variable text
	    String[] text = {"I am so happy with my life","I hate this job"};
	    ArrayList<String[]> textTuple = new ArrayList<String[]>();
	    
	    //loops through list of crawled comments 
        for(int i = 0;i < text.length;i++) {
        	SentimentAnalyzer s = new SentimentAnalyzer(text[i]);
        	String score = String.valueOf(s.getScore());
        	String magnitude = String.valueOf(s.getMagnitude());
        	
        	// each iteration of the loop will create an array with the inputted text, sentiment score 
        	// and sentiment magnitude
        	// Sentiment score => -1 -> worst , 0 -> in the middle, 1 -> best
        	// Sentiment score => strength of the sentiment ( ranging from 0 to inf.) Longer texts usually higher magnitude
        	String[] myString1= {text[i],score,magnitude}; 
        	textTuple.add(myString1);
        }
        
        
        //To test for output
        for(int i = 0;i < textTuple.size();i++) {
        	System.out.println(textTuple.get(i)[2]);
        }
        
	}
	
	//Init method for word cloud generation - Boon Kiat
	public void initWordCloud() throws IOException {
		//System.out.println(mostFrequentWords("/home/sangar/Documents/test.txt", 3));
    	ArrayList<String> resultWords = new ArrayList<>();
    	resultWords = WordCloudAnalyzer.mostFrequentWords("stopwords.txt", 450);
    	
    	for(int i=0; i< resultWords.size();i++) {
        	System.out.println(resultWords.get(i));
    	}
    	
    	//System.out.println(new java.io.File("output.txt").getAbsolutePath());
    	//System.out.println(TellMeMyWorkingDirectory.class.getClassLoader().getResource("").getPath());
    	
    	FileWriter writer = new FileWriter("src/test/resources/text/output.txt"); 
    	for(String str: resultWords) {
    	  writer.write(str + " ");
    	}
    	writer.close();
	}
	
	//Init method for statistical analysis - Joel
}

class StatisticsAnalyzer{
	List<RedditPost> redditPosts;
	List<TwitterPost> twitterPosts;
	public StatisticsAnalyzer(List<RedditPost> rPosts, List<TwitterPost> tPosts) {
		this.redditPosts = rPosts;
		this.twitterPosts = tPosts;
	}
	
	public RedditPost getBestRedditPost() {
		RedditPost bestPost = null;
		int postScore = 0;
		for (RedditPost posts : this.redditPosts) {
			if (posts.getLikeCount() > postScore) {
				postScore = posts.getLikeCount();
				bestPost = posts;
			}
		}
		return bestPost;
	}
	
	public RedditComment getBestRedditComment() {
		RedditComment bestComment = null;
		int commentScore = 0;
		for (RedditPost posts : this.redditPosts) {
			for (RedditComment rc : posts.getCommentList()) {
				if (rc.getCommentScore() > commentScore) {
					commentScore = posts.getLikeCount();
					bestComment = rc;
				}
			}
		}
		return bestComment;
	}
	
	public String getPopularSubreddit() {
		Map<String, Integer> popSub = new HashMap<String, Integer>();
		for (RedditPost posts : this.redditPosts) {
			if(popSub.containsKey(posts.getPostSubreddit())) {
				int currval = popSub.get(posts.getPostSubreddit());
				popSub.put(posts.getPostSubreddit(), currval +1);
			}
			else {
				popSub.put(posts.getPostSubreddit(), 1);
			}
		}
		
		int occurrence = 0;
		String max = null;
		
		for (Map.Entry<String, Integer> subs : popSub.entrySet()) {
			if (subs.getValue() > occurrence) {
				occurrence = subs.getValue();
				max = subs.getKey();
			}
		}
		
		return max;
	}
	
	public TwitterPost getBestTwitterPost() {
		TwitterPost bestPost = null;
		int postScore = 0;
		for (TwitterPost posts : this.twitterPosts) {
			if (posts.getLikeCount() > postScore) {
				postScore = posts.getLikeCount();
				bestPost = posts;
			}
		}
		return bestPost;
	}
	
	public TwitterPost getMostRTPost() {
		TwitterPost bestPost = null;
		int postScore = 0;
		for (TwitterPost posts : this.twitterPosts) {
			if (posts.getRTCount() > postScore) {
				postScore = posts.getRTCount();
				bestPost = posts;
			}
		}
		return bestPost;
	}
	
	public int[] redditPostGeneralStats() {
		int postScore = 0;
		int commentScore = 0;
		int totalPosts = this.redditPosts.size();
		int totalComments = 0;
		
		for (RedditPost rp : this.redditPosts) {
			postScore += rp.getLikeCount();
			totalComments += rp.getCommentList().size();
			for (RedditComment rc : rp.getCommentList()) {
				commentScore += rc.getCommentScore();
			}
		}
		
		int[] stats = {totalPosts, totalComments, postScore, commentScore};
		return stats;
	}
	
	public int[] twitterPostGeneralStats() {
		int postLikes = 0;
		int postRTs = 0;
		int totalPosts = this.twitterPosts.size();
		
		for (TwitterPost tp : this.twitterPosts) {
			postLikes += tp.getLikeCount();
			postRTs += tp.getRTCount();
		}
		
		int[] stats = {totalPosts, postLikes, postRTs};
		return stats;
	}
	
}

class SentimentAnalyzer {
	String text;
	private Object sentiment;
	
	public SentimentAnalyzer(String text) throws IOException, Exception {
		this.text = text;
		
		try (LanguageServiceClient language = LanguageServiceClient.create()) {
            // The text to analyze
            Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();
            // Detects the sentiment of the text
            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
            this.sentiment = sentiment;
  
        }
	}
	
	public float getScore() {
		Object s = this.sentiment;
		
		return ((Sentiment) s).getScore();
		
	}
	
	public float getMagnitude() {
		Object s = this.sentiment;
		
		return ((Sentiment) s).getMagnitude();
		
	}
}

class WordCloudAnalyzer {
	public static HashMap<String, Integer> readFile(String fileName) throws IOException {
		HashMap<String, Integer> wordMap = new HashMap<>();

		Path path = Paths.get(fileName);
		try (Scanner scanner = new Scanner(path)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (wordMap.containsKey(line)) {
					wordMap.put(line, wordMap.get(line) + 1);
				} else {
					wordMap.put(line, 1);
				}
			}
		}
		return wordMap;
	}

	public static ArrayList<String> mostFrequentWords(String fileName, int n) {
		ArrayList<String> topWords = new ArrayList<>();

		try {
			HashMap<String, Integer> wordMap = readFile(fileName);
			PriorityQueue<Pair<String, Integer>> pq = new PriorityQueue<>(n,
					(x, y) -> x.getValue().compareTo(y.getValue()));

			int i = 0;
			Iterator it = wordMap.entrySet().iterator();
			/*
			 * Get first n words on heap
			 */
			while (it.hasNext()) {
				if (i == n)
					break;
				HashMap.Entry<String, Integer> entry = (HashMap.Entry<String, Integer>) it.next();
				pq.add(new Pair<>(entry.getKey(), entry.getValue()));
				it.remove();
				i++;
			}

			/*
			 * Check all other words, if anyone more than least remove the least and add the
			 * new word.
			 */
			for (String key : wordMap.keySet()) {
				if (pq.peek().getValue() < wordMap.get(key)) {
					pq.poll();
					pq.add(new Pair<>(key, wordMap.get(key)));
				}
			}
			while (!pq.isEmpty()) {
				topWords.add(pq.poll().getKey());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return topWords;
	}
}

class stopWords {

	public void stopClass1() {
		String[] stopWrds = { "i", "a", "about", "an", "are", "as", "at", "be", "by", "com", "for", "from", "how", "in",
				"is", "it", "of", "on", "or", "that", "the", "this", "to", "was", "what", "when", "where", "who",
				"will", "with", "so", "not", "his", "he", "she", "her", "have", "my", "you", "and", "but", "me", "him",
				"no", "get", "all", "if", "they", "like", "just", "too", "every", "maybe", "soon", "tbh", "him.",
				"there", "can", "one", "had", "it's", "had", "your", "won't", "than", "do", "their", "think", "really",
				"because", "it's", "were", "it's", "has", "i'm", "would", "know", "it’s", "even", "out", "in", "don't",
				"never", "won't", "fucking", "that's", "@visselkobe", "barcelona", "utama", "fuck", "booba", "booba",
				"drunk", "stupid", "islamophobie", "riner", "funeral", "vissel", "i'm", "many", "don't", "most",
				"could", "also", "can't", "want", "would", "then", "our", "been", "say", "#acl2020", "very", "en", "go",
				"thing", "i'm", "being", "don't", "make", "them", "some", "can,t", "it.", "-", "got", "i'm", "don't",
				"little", "makes", "actually", "same", "those", "Other" };

		try {
			Scanner fip1 = new Scanner(new File("wordcloud.txt"));
			FileOutputStream out = new FileOutputStream("StopWords.txt");

			while (fip1.hasNext()) {
				int flag = 1;
				String s1 = fip1.next();
				s1 = s1.toLowerCase();
				for (int i = 0; i < stopWrds.length; i++) {
					if (s1.equals(stopWrds[i])) {
						flag = 0;
					}
				}
				if (flag != 0) {
					System.out.println(s1);
					PrintStream p = new PrintStream(out);
					p.println(s1);
				}

			}

		} catch (Exception e) {
			System.err.println("cannot read file");
		}
	}
}

class removeStopWords {

	/**
	 * @param args
	 */
	// MAKE THIS A METHOD TO INVOKE?
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		stopWords obj = new stopWords();
		obj.stopClass1();

	}

}
