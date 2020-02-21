package application;

public class Main {
	public static void main(String[] args) throws Exception {
		//start program
		//prompts user if he wishes to crawl for data -> yes, will crawl (will take a while), no, will go straight to gui.
		//gui will be empty at first. click generate, will run all the analyzer and populate the gui
		
		//initialises crawler - if user chooses to crawl before starting program
		//Crawler c = new Crawler();
//		
		//passing over to analyzer - after crawling so that data can be populated
		//Analyzer a = new Analyzer(c.getRedditPosts(), c.getTwitterPosts());
		
		//otherwise, GUI will be unpopulated, users can load data to analyze
		Importer i = new Importer("reddit.csv", "twitter.csv");
		i.importReddit("./reddit.csv");
	}
}
