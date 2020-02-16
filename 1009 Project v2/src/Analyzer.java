import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Document.Type;

import javafx.util.Pair;

public class Analyzer {
	public Analyzer() {
		//calling this will start analysis
	}
	
	//ideally will contain methods for each kind of analysis - sentiment, word cloud, statistics
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
