package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;


import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

public class Sentiment {
	
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

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		

		
		
		BufferedReader reader;
		
		System.out.println(genSentiment("I hate the Kentaco, it is damn unhealthy and disgusting to eat"));

//		try {
//			reader = new BufferedReader(new FileReader("src/sentiment/sentiment/edu/stanford/wordcloud.txt"));
//			String line = reader.readLine();
//			while (line != null) {
//		
//				if(genSentiment(line).equals("Positive")) {
//					System.out.println(line);
//				}
//				// read next line
//				line = reader.readLine();
//			}
//			reader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	

		
}

}
