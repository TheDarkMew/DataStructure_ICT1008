package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class ProjectController extends Application implements Initializable{
	//fxid for javafx objects
	@FXML private TableView<testTable> twittertableView;
	@FXML private TableView<testRedditTable> reddittableView;
	@FXML private TableColumn<testTable,String> userNameColumn;
	@FXML private TableColumn<testTable,Integer> retweetsColumn;
	@FXML private TableColumn<testTable,Integer> favouritesColumn;
	@FXML private TableColumn<testTable,String> postColumn;
	@FXML private TableColumn<testTable,String> datesColumn;
	@FXML private TableColumn<testTable,String> sentimentColumnT;
	@FXML private BarChart<?,?> barChart;
	@FXML private BarChart<?,?> barChart2;
	@FXML private PieChart pieChart;
	@FXML private NumberAxis ybar; 
    @FXML private CategoryAxis xbar;
	@FXML private TextArea textArea;
	@FXML private TableColumn<testRedditTable,String> titleColumn;
	@FXML private TableColumn<testRedditTable,Integer> scoreColumn;
	@FXML private TableColumn<testRedditTable,String> dateColumn;
	@FXML private TableColumn<testRedditTable,String> authorColumn;
	@FXML private TableColumn<testRedditTable,String> subRedditColumn;
	@FXML private TableColumn<testRedditTable,String> sentimentColumnR;
	@FXML private MenuItem importCSV;

	
	private Crawler c;
	private Analyzer a;
	private Importer i;
	private List<TwitterPost> tpost = null;
	private List<RedditPost> rpost = null;
	 
	//Link stage and scene to project.fxml file 
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Project.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.show();
			primaryStage.setTitle("ICT1009 Project");
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}

	//Read csv file
	public ObservableList<testTable> readCSV() {
		ObservableList<testTable> dataList = FXCollections.observableArrayList(); 
		
        	for (TwitterPost post : tpost) {
        		testTable twitterTable = new testTable(post.getUsername(), post.getRTCount(), post.getLikeCount(), post.getPostContent(), post.getPostDate(), post.getSentiment()); 
        		dataList.add(twitterTable);
        	}
        	return dataList;
 
    } 
	
	public ObservableList<testRedditTable> readCSV2() {
		ObservableList<testRedditTable> dataList1 = FXCollections.observableArrayList();
			for (RedditPost post : rpost) {
				testRedditTable redditTable = new testRedditTable(post.getPostContent(), post.getLikeCount(), post.getPostDate(), post.getUsername(), post.getPostSubreddit(), post.getSentiment());
				dataList1.add(redditTable);
				for (RedditComment comment : post.getCommentList()) {
					testRedditTable redditTable2 = new testRedditTable(comment.getCommentText(), comment.getCommentScore(), comment.getCommentDate(), comment.getUsername(), "From Above Post", comment.getSentiment());
					dataList1.add(redditTable2);
				}
			}
	        return dataList1;
 
    } 

	public void setBarChart() {
		int[] twitterStats = a.getTwitterStats(); //total posts, total favs, total retweets
    	int[] redditStats = a.getRedditStats(); //total posts, total comments, total post upvotes, total comment upvotes
    	XYChart.Series reddit = new XYChart.Series();    
    	reddit.getData().add(new XYChart.Data("Total Posts", redditStats[0])); 
    	reddit.getData().add(new XYChart.Data("Total Comments", redditStats[1])); 
    	reddit.getData().add(new XYChart.Data("Total Posts Upvotes", redditStats[2])); 
    	reddit.getData().add(new XYChart.Data("Total Comments Upvotes", redditStats[3]));
        barChart.getData().add(reddit);
        
        XYChart.Series twitter = new XYChart.Series();    
        twitter.getData().add(new XYChart.Data("Total Posts", twitterStats[0])); 
        twitter.getData().add(new XYChart.Data("Total Favourites", twitterStats[1])); 
        twitter.getData().add(new XYChart.Data("Total Comments Upvotes", twitterStats[2])); 
        barChart.getData().add(twitter);    
	}
	
	public void setPieChart() {
		int[] sentimentStats = a.getSentimentStats(); //very positive, positive, neutral, negative, very negative
        ObservableList <PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Very Positive", sentimentStats[0]),
                new PieChart.Data("Positive", sentimentStats[1]),
                new PieChart.Data("Neutral", sentimentStats[2]),
                new PieChart.Data("Negative", sentimentStats[3]),
                new PieChart.Data("Very Negative", sentimentStats[4]));
                
           	pieChart.setData(pieChartData);
	}
	
	public void setStatsArea() {
		String popularSubreddit = a.getPopularSubreddit();
        RedditPost popularPost = a.getPopularPost();
        RedditComment popularComment = a.getPopularComment();
        TwitterPost popularTweet = a.getPopularTweet();
        TwitterPost popularRT = a.getPopularRT();
        textArea.appendText("Most Popular Subreddit: " + popularSubreddit);
        textArea.appendText("\nMost Popular Reddit Post:\n");  
        textArea.appendText("Title: "+popularPost.getPostContent()+"\n");
        textArea.appendText("Posted By: "+ popularPost.getUsername()+"\n");
        textArea.appendText("Upvotes: "+popularPost.getLikeCount()+"\n");
        textArea.appendText("Comments: "+popularPost.getPostCommentCount()+"\n");
        textArea.appendText("Posted to: "+popularPost.getPostSubreddit()+"\n");
        textArea.appendText("Post Date: "+popularPost.getPostDate()+"\n\n");
        textArea.appendText("Most Popular Reddit Comment \n");
        textArea.appendText("Comment: "+popularComment.getCommentText()+"\n");
        textArea.appendText("Posted By: "+popularComment.getUsername()+"\n");
        textArea.appendText("Upvotes: "+popularComment.getCommentScore()+"\n");
        textArea.appendText("Post Date: "+popularComment.getCommentDate()+"\n");
        textArea.appendText("Most Favourited Tweet\n");
        textArea.appendText("Tweet: "+popularTweet.getPostContent()+"\n");
        textArea.appendText("Posted By: "+popularTweet.getUsername()+"\n");
        textArea.appendText("Favourites: "+popularTweet.getLikeCount()+"\n");
        textArea.appendText("RTs: "+popularTweet.getRTCount()+"\n");
        textArea.appendText("Post Date: "+popularTweet.getPostDate()+"\n");
        textArea.appendText("Most Retweeted Tweet\n");
        textArea.appendText("Tweet: "+popularRT.getPostContent()+"\n");
        textArea.appendText("Posted By: "+popularRT.getUsername()+"\n");
        textArea.appendText("Favourites: "+popularRT.getLikeCount()+"\n");
        textArea.appendText("RTs: "+popularRT.getRTCount()+"\n");
        textArea.appendText("Post Date: "+popularRT.getPostDate()+"\n");
	}
	
	public void startCrawling() {
		try {
			this.c = new Crawler();
			
			this.tpost = c.getTwitterPosts();
			this.rpost = c.getRedditPosts();
			this.a = new Analyzer(rpost, tpost);
			
			populateTables();
	    	
	    	setBarChart();
	        
	        setPieChart();
	        	
	        setStatsArea();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error!");
			alert.setHeaderText(null);
			alert.setContentText("Unable to crawl for data! Please try again.");
			alert.showAndWait();
			System.exit(-1);
		}
		
		
	}
	
	public void startImporting() { // String rpath, String tpath
		try {
//			this.i = new Importer(rpath, tpath);
			this.i = new Importer("./reddit.csv", "./twitter.csv");
			this.tpost = i.getTwitterPosts();
			this.rpost = i.getRedditPosts();
			this.a = new Analyzer(rpost, tpost);
			
			populateTables();
	    	
	    	setBarChart();
	        
	        setPieChart();
	        	
	        setStatsArea();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error!");
			alert.setHeaderText(null);
			alert.setContentText("Unable to import CSVs!");
			alert.showAndWait();
			System.exit(-1);
		}
		
	}
	
	public void populateTables() {
		userNameColumn.setCellValueFactory(new PropertyValueFactory<testTable, String>("userName"));
    	retweetsColumn.setCellValueFactory(new PropertyValueFactory<testTable, Integer>("retweets"));
    	favouritesColumn.setCellValueFactory(new PropertyValueFactory<testTable, Integer>("favourites"));
    	postColumn.setCellValueFactory(new PropertyValueFactory<testTable, String>("post"));
    	datesColumn.setCellValueFactory(new PropertyValueFactory<testTable, String>("dates"));
    	sentimentColumnT.setCellValueFactory(new PropertyValueFactory<testTable, String>("sentiment"));
    	
    	titleColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("title"));
    	scoreColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, Integer>("score"));
    	dateColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("date"));
    	authorColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("author"));
    	subRedditColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("subReddit"));
    	sentimentColumnR.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("sentiment"));
    	
    	twittertableView.setItems(readCSV());
    	reddittableView.setItems(readCSV2());
	}
	
	//initialize columns with variables as cells from testTable.java and testRedditTable.java
	//set both tables with readCSV() and readCSV2 method
    public void initialize(URL location, ResourceBundle resources) 
    { 	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("1009 Crawler");
    	alert.setHeaderText("Would you like to crawl for new data or import data?");
    	alert.setContentText("Warning: Crawling for data will take a few minutes. Please be patient.\nImporting data would require an existing reddit.csv and twitter.csv.");
    	
    	ButtonType buttonTypeOne = new ButtonType("Crawl");
    	ButtonType buttonTypeTwo = new ButtonType("Import");
    	ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
    	alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == buttonTypeOne){
    	    startCrawling();
    	} else if (result.get() == buttonTypeTwo){
    	    startImporting();
    	}
    	else {
    		System.exit(0);
    	}
       
        
    }
    
	public static void main(String[] args) throws Exception {
		launch(args);
	}


  
}

