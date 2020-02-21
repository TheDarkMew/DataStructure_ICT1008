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

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Scanner;

//import com.google.cloud.language.v1.Document;
//import com.google.cloud.language.v1.Document.Type;
//import com.google.cloud.language.v1.LanguageServiceClient;
//import com.google.cloud.language.v1.Sentiment;

import javafx.util.Pair;

public class Analyzer {
	private String popularSubreddit;
	private RedditPost popularPost;
	private RedditComment popularComment;
	private TwitterPost popularTweet;
	private TwitterPost popularRT;
	private int[] redditStats; //total posts, total comments, total post upvotes, total comment upvotes
	private int[] twitterStats; //total posts, total favs, total retweets
	private int[] sentimentStats; //very pos, pos, neutral, neg, very neg
 	
	public Analyzer(List<RedditPost> rPosts, List<TwitterPost> tPosts) throws Exception {
		//calling this will start analysis
		//initWordCloud();
		initStatisticsAnalyzer(rPosts, tPosts);
	}
	//init method for Statistics Analysis - Boon Kiat & Kin Seong
	public void initStatisticsAnalyzer(List<RedditPost> rPosts, List<TwitterPost> tPosts) {
		int [] sentstats = {0,0,0,0,0}; //sentiment stats counter
		RedditPost bestPost = null; //best reddit post
		RedditComment bestComment = null; //best reddit comment
		Map<String, Integer> popSub = new HashMap<String, Integer>(); //hashmap to find most popular subreddit
		int postScore = 0; //best post upvote counter
		int commentScore = 0; //best comment upvote counter
		int totalRScore = 0; //total reddit post upvote counter
		int totalCommentScore = 0; //total reddit comment upvote counter
		int totalRComments = 0; // total reddit comments counter
		for (RedditPost posts : rPosts) {
			//increment total reddit post upvotes counter
			totalRScore += posts.getLikeCount();
			//increment total comments counter
			totalRComments += posts.getCommentList().size();
			int upvotes = posts.getLikeCount();
			if (upvotes > postScore) {
				//sets best post score
				postScore = upvotes;
				//sets best post
				bestPost = posts;
			}
			//checking subreddit popularity
			String sub = posts.getPostSubreddit();
			if(popSub.containsKey(sub)) {
				int currval = popSub.get(sub);
				popSub.put(sub, currval +1);
			}
			else {
				popSub.put(sub, 1);
			}
			
			for (RedditComment rc : posts.getCommentList()) {
				int cscore = rc.getCommentScore();
				//increments total comment upvotes
				totalCommentScore += rc.getCommentScore();
				if (cscore > commentScore) {
					//set best comment score
					commentScore = cscore;
					//set best comment
					bestComment = rc;
				}
				//Sentiment Calculation
				String sentC = rc.getSentiment();
				//comment.setSentiment(sentC);
				
				if(sentC.equals("Very Positive")) {
					sentstats[0]++;
				}
				else if(sentC.equals("Positive")) {
					sentstats[1]++;
				} 
				else if(sentC.equals("Neutral")) {
					sentstats[2]++;
				}
				else if(sentC.equals("Negative")) {
					sentstats[3]++;
				}
				else if(sentC.equals("Very Negative")) {
					sentstats[4]++;
				}
			}
		}
		//checking subreddit popularity
		int occurrence = 0;
		String max = null;
		
		for (Map.Entry<String, Integer> subs : popSub.entrySet()) {
			int subval = subs.getValue();
			if (subval > occurrence) {
				occurrence = subval;
				max = subs.getKey();
			}
		}
		
		TwitterPost bestTPost = null; //most favourited tweet
		TwitterPost bestRTPost = null; //most retweeted
		int postTScore = 0; //best tweet favs count
		int postRTScore = 0; //best tweet rt count
		int postLikes = 0; //total fav count
		int postRTs = 0; //total rt count
		for (TwitterPost Tposts : tPosts) {
			//increment total fav count
			postLikes += Tposts.getLikeCount();
			//increment total rt count
			postRTs += Tposts.getRTCount();
			int likecount = Tposts.getLikeCount();
			if (likecount > postTScore) {
				//set best tweet fav
				postTScore = likecount;
				//set best tweet
				bestTPost = Tposts;
			}
			int rtcount = Tposts.getRTCount();
			if (rtcount > postRTScore) {
				//set best rt count
				postRTScore = rtcount;
				//set best rt
				bestRTPost = Tposts;
			}
			//sentiment analysis for tweet
			String sent = Tposts.getSentiment();
			//Tposts.setSentiment(sent);
			
			if(sent.equals("Very Positive")) {
				sentstats[0]++;
			}
			else if(sent.equals("Positive")) {
				sentstats[1]++;
			} 
			else if(sent.equals("Neutral")) {
				sentstats[2]++;
			}
			else if(sent.equals("Negative")) {
				sentstats[3]++;
			}
			else if(sent.equals("Very Negative")) {
				sentstats[4]++;
			}
		}
		//sets everything
		int[] rStats = {rPosts.size(), totalRComments, totalRScore, totalCommentScore};
		int[] tStats = {tPosts.size(), postLikes, postRTs};
		redditStats = rStats;
		twitterStats = tStats;
		popularPost = bestPost;
		popularComment = bestComment;
		popularTweet = bestTPost;
		popularRT = bestRTPost;
		popularSubreddit = max;
		sentimentStats = sentstats;
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

	public String getPopularSubreddit() {
		return this.popularSubreddit;
	}
	public RedditPost getPopularPost() {
		return this.popularPost;
	}
	public RedditComment getPopularComment() {
		return this.popularComment;
	}
	public TwitterPost getPopularTweet() {
		return this.popularTweet;
	}
	public TwitterPost getPopularRT() {
		return this.popularRT;
	}
	public int[] getRedditStats() {
		return this.redditStats;
	}
	public int[] getTwitterStats() {
		return this.twitterStats;
	}
	public int[] getSentimentStats() {
		return this.sentimentStats;
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
