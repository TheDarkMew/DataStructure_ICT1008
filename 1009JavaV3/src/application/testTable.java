package application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class testTable {
	private SimpleStringProperty userName, post, dates;
	private SimpleIntegerProperty retweets, favourites;
	
	
	public testTable(String userName, int retweets, int favourites,String post, String dates) {
		this.userName = new SimpleStringProperty(userName);
		this.retweets = new SimpleIntegerProperty(retweets);
		this.favourites = new SimpleIntegerProperty(favourites);
		this.post = new SimpleStringProperty(post);
		this.dates = new SimpleStringProperty(dates);
		
	}
	
	public String getUserName() {
		return userName.get();
	}
	
	public void setUserName(SimpleStringProperty userName) {
		this.userName = userName;
	}
	
	public int getRetweets() {
		return retweets.get();
	}
	
	public void setRetweets(SimpleIntegerProperty retweets) {
		this.retweets = retweets;
	}
	
	public int getFavourites() {
		return favourites.get();
	}
	
	public void setFavourites(SimpleIntegerProperty favourites) {
		this.favourites = favourites;
	}
	
	public String getPost() {
		return post.get();
	}
	
	public void setPost(SimpleStringProperty post) {
		this.post = post;
	}
	
	public String getDates() {
		return dates.get();
	}
	
	public void setDates(SimpleStringProperty dates) {
		this.dates = dates;
	}
	

	
	
}
