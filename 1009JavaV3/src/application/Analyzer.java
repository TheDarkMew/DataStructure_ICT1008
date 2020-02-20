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
		//initSentimentAnalyzer(rPosts, tPosts);
		//initWordCloud();
		initStatisticsAnalyzer(rPosts, tPosts);
	}
	//init method for Statistics Analysis - Boon Kiat
	public void initStatisticsAnalyzer(List<RedditPost> rPosts, List<TwitterPost> tPosts) {
//		StatisticsAnalyzer s = new StatisticsAnalyzer(rPosts, tPosts);
//		this.popularSubreddit = s.getPopularSubreddit();
//		this.popularPost = s.getBestRedditPost();
//		this.popularComment = s.getBestRedditComment();
//		this.popularTweet = s.getBestTwitterPost();
//		this.popularRT = s.getMostRTPost();
//		this.redditStats = s.redditPostGeneralStats(); //total posts, total comments, total post upvotes, total comment upvotes
//		this.twitterStats = s.twitterPostGeneralStats(); //total posts, total favs, total retweets
		int [] sentstats = {0,0,0,0,0};
		RedditPost bestPost = null;
		RedditComment bestComment = null;
		Map<String, Integer> popSub = new HashMap<String, Integer>();
		int postScore = 0;
		int commentScore = 0;
		int totalRScore = 0;
		int totalCommentScore = 0;
		int totalRComments = 0;
		for (RedditPost posts : rPosts) {
			totalRScore += posts.getLikeCount();
			totalRComments += posts.getCommentList().size();
			int upvotes = posts.getLikeCount();
			if (upvotes > postScore) {
				postScore = upvotes;
				bestPost = posts;
			}
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
				totalCommentScore += rc.getCommentScore();
				if (cscore > commentScore) {
					commentScore = cscore;
					bestComment = rc;
				}
				String textC = rc.getCommentText();
				String sentC = genSentiment(textC);
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
		
		int occurrence = 0;
		String max = null;
		
		for (Map.Entry<String, Integer> subs : popSub.entrySet()) {
			int subval = subs.getValue();
			if (subval > occurrence) {
				occurrence = subval;
				max = subs.getKey();
			}
		}
		
		TwitterPost bestTPost = null;
		TwitterPost bestRTPost = null;
		int postTScore = 0;
		int postRTScore = 0;
		int postLikes = 0;
		int postRTs = 0;
		for (TwitterPost Tposts : tPosts) {
			postLikes += Tposts.getLikeCount();
			postRTs += Tposts.getRTCount();
			int likecount = Tposts.getLikeCount();
			if (likecount > postTScore) {
				postTScore = likecount;
				bestTPost = Tposts;
			}
			int rtcount = Tposts.getRTCount();
			if (rtcount > postRTScore) {
				postRTScore = rtcount;
				bestRTPost = Tposts;
			}
			String text = Tposts.getPostContent();
			String sent = genSentiment(text);
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
	
	//init method for Sentiment Analysis - Kin Seong
	public void initSentimentAnalyzer(List<RedditPost> rPosts, List<TwitterPost> tPosts) throws IOException, Exception {
		int [] stats = {0,0,0,0,0};
		
		for(TwitterPost tweet : tPosts) {
			
			String text = tweet.getPostContent();
			String sent = genSentiment(text);
			//tweet.setSentiment(sent);
			
			if(sent.equals("Very Positive")) {
				stats[0]++;
			}
			else if(sent.equals("Positive")) {
				stats[1]++;
			} 
			else if(sent.equals("Neutral")) {
				stats[2]++;
			}
			else if(sent.equals("Negative")) {
				stats[3]++;
			}
			else if(sent.equals("Very Negative")) {
				stats[4]++;
			}
		}
		
		for (RedditPost post : rPosts) {
			
			for (RedditComment comment : post.getCommentList()) {
				String textC = comment.getCommentText();
				String sentC = genSentiment(textC);
				//comment.setSentiment(sentC);
				
				if(sentC.equals("Very Positive")) {
					stats[0]++;
				}
				else if(sentC.equals("Positive")) {
					stats[1]++;
				} 
				else if(sentC.equals("Neutral")) {
					stats[2]++;
				}
				else if(sentC.equals("Negative")) {
					stats[3]++;
				}
				else if(sentC.equals("Very Negative")) {
					stats[4]++;
				}
			}
		}
		
		this.sentimentStats = stats;
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

	public static String genSentiment(String text) {
		
		RedwoodConfiguration.current().clear().apply(); 
		
		String sentiment = "";
		Properties props = new Properties();
		 props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		 StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		 
		 
		 CoreDocument coreDocument = new CoreDocument(text);
		 pipeline.annotate(coreDocument);
		 
		 
		 List<CoreSentence> sentences = coreDocument.sentences();
		 
		 
		 for(CoreSentence sentence : sentences) {
			 sentiment = sentence.sentiment();
			
			
		 }
		 return sentiment;
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
			int upvotes = posts.getLikeCount();
			if (upvotes > postScore) {
				postScore = upvotes;
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
				int cscore = rc.getCommentScore();
				if (cscore > commentScore) {
					commentScore = cscore;
					bestComment = rc;
				}
			}
		}
		return bestComment;
	}
	
	public String getPopularSubreddit() {
		Map<String, Integer> popSub = new HashMap<String, Integer>();
		for (RedditPost posts : this.redditPosts) {
			String sub = posts.getPostSubreddit();
			if(popSub.containsKey(sub)) {
				int currval = popSub.get(sub);
				popSub.put(sub, currval +1);
			}
			else {
				popSub.put(sub, 1);
			}
		}
		
		int occurrence = 0;
		String max = null;
		
		for (Map.Entry<String, Integer> subs : popSub.entrySet()) {
			int subval = subs.getValue();
			if (subval > occurrence) {
				occurrence = subval;
				max = subs.getKey();
			}
		}
		
		return max;
	}
	
	public TwitterPost getBestTwitterPost() {
		TwitterPost bestPost = null;
		int postScore = 0;
		for (TwitterPost posts : this.twitterPosts) {
			int likecount = posts.getLikeCount();
			if (likecount > postScore) {
				postScore = likecount;
				bestPost = posts;
			}
		}
		return bestPost;
	}
	
	public TwitterPost getMostRTPost() {
		TwitterPost bestPost = null;
		int postScore = 0;
		for (TwitterPost posts : this.twitterPosts) {
			int rtcount = posts.getRTCount();
			if (rtcount > postScore) {
				postScore = rtcount;
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
