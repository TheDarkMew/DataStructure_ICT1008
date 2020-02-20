package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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
import javafx.scene.control.ListView;
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

	
	private Crawler c;
	private Analyzer a;
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
        		testTable twitterTable = new testTable(post.getUsername(), post.getRTCount(), post.getLikeCount(), post.getPostContent(), post.getPostDate());     
        		dataList.add(twitterTable);
        	}
        	return dataList;
 
    } 
	
	public ObservableList<testRedditTable> readCSV2() {
		ObservableList<testRedditTable> dataList1 = FXCollections.observableArrayList();
			for (RedditPost post : rpost) {
				testRedditTable redditTable = new testRedditTable(post.getPostContent(), post.getLikeCount(), post.getPostDate(), post.getUsername(), post.getPostSubreddit());
				dataList1.add(redditTable);
				for (RedditComment comment : post.getCommentList()) {
					testRedditTable redditTable2 = new testRedditTable(comment.getCommentText(), comment.getCommentScore(), comment.getCommentDate(), comment.getUsername(), "From Above Post");
					dataList1.add(redditTable2);
				}
			}
	        return dataList1;
 
    } 
	

	public void loadStatistics() {
        String popularSubreddit = a.getPopularSubreddit();
        RedditPost popularPost = a.getPopularPost();
        RedditComment popularComment = a.getPopularComment();
        TwitterPost popularTweet = a.getPopularTweet();
        TwitterPost popularRT = a.getPopularRT();
        
        int[] redditStats = a.getRedditStats(); //total posts, total comments, total post upvotes, total comment upvotes
        int redditTotalPosts = redditStats[0];
        int redditTotalComments = redditStats[1];
        int redditPostUpvotes = redditStats[2];
        int redditCommentUpvotes = redditStats[3];
        
        int[] twitterStats = a.getTwitterStats(); //total posts, total favs, total retweets
        int twitterTotalPosts = twitterStats[0];
        int twitterTotalLikes = twitterStats[1];
        int twitterTotalRT = twitterStats[2];
        
        //set statistics text portion
        
        
        //set bar chart for general statistics
        
        
    }

	
	//initialize columns with variables as cells from testTable.java and testRedditTable.java
	//set both tables with readCSV() and readCSV2 method
    public void initialize(URL location, ResourceBundle resources) 
    { 	
    	
		try {
			c = new Crawler();
			tpost = c.getTwitterPosts();
			rpost = c.getRedditPosts();
			this.a = new Analyzer(rpost, tpost);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	userNameColumn.setCellValueFactory(new PropertyValueFactory<testTable, String>("userName"));
    	retweetsColumn.setCellValueFactory(new PropertyValueFactory<testTable, Integer>("retweets"));
    	favouritesColumn.setCellValueFactory(new PropertyValueFactory<testTable, Integer>("favourites"));
    	postColumn.setCellValueFactory(new PropertyValueFactory<testTable, String>("post"));
    	datesColumn.setCellValueFactory(new PropertyValueFactory<testTable, String>("dates"));
    	titleColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("title"));
    	scoreColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, Integer>("score"));
    	dateColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("date"));
    	authorColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("author"));
    	subRedditColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("subReddit"));
    	twittertableView.setItems(readCSV());
    	reddittableView.setItems(readCSV2());
    	
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
        
        //int[] sentimentStats = a.getSentiment(); //very positive, positive, neutral, negative, very negative
        int[] sentimentStats = {13, 13, 24, 55, 65};
        ObservableList <PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Very Positive", sentimentStats[0]),
                new PieChart.Data("Positive", sentimentStats[1]),
                new PieChart.Data("Neutral", sentimentStats[2]),
                new PieChart.Data("Negative", sentimentStats[3]),
                new PieChart.Data("Very Negative", sentimentStats[4]));
                
           	pieChart.setData(pieChartData);
        
           	
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
    
	public static void main(String[] args) throws Exception {
		launch(args);
	}


  
}

