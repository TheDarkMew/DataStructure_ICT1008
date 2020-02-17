package application;
import javafx.beans.property.SimpleStringProperty;

public class testRedditTable {
	private SimpleStringProperty comments,user, points;
	
	public testRedditTable(String comments, String user, String points){
		this.comments = new SimpleStringProperty(comments);
		this.user = new SimpleStringProperty(user);
		this.points = new SimpleStringProperty(points);

	}

	public String getComments() {
		return comments.get();
	}

	public void setComments(SimpleStringProperty comments) {
		this.comments = comments;
	}

	public String getUser() {
		return user.get();
	}

	public void setUser(SimpleStringProperty user) {
		this.user = user;
	}

	public String getPoints() {
		return points.get();
	}

	public void setPoints(SimpleStringProperty points) {
		this.points = points;
	}
	
	
}
