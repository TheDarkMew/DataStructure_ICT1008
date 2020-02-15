package com_javaproject.crawling_project;

import java.io.IOException;
import java.util.ArrayList;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Document.Type;


class getSentiment{
	
	String text;
	private Object sentiment;
	
	public getSentiment(String text) throws IOException, Exception {
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

public class App {
	
	
	
    public static void main(String... args) throws Exception {
    	System.err.close();
	    System.setErr(System.out);
        // Instantiates a client
	    
	    //Insert array of comments under the variable text
	    String[] text = {"I am so happy with my life","I hate this job"};
	    ArrayList<String[]> textTuple = new ArrayList<String[]>();
	    
	    //loops through list of crawled comments 
        for(int i = 0;i < text.length;i++) {
        	getSentiment s = new getSentiment(text[i]);
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
    
}
