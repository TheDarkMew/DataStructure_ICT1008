package application;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class testRedditTable {
	private SimpleStringProperty title, date, author, subReddit;
	private SimpleIntegerProperty score;
	
	public testRedditTable(String title, int score, String date, String author,  String subReddit){
		this.title = new SimpleStringProperty(title);
		this.score = new SimpleIntegerProperty(score);
		this.date = new SimpleStringProperty(date);
		this.author = new SimpleStringProperty(author);
		this.subReddit = new SimpleStringProperty(subReddit);

	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(SimpleStringProperty title) {
		this.title = title;
	}

	public int getScore() {
		return score.get();
	}

	public void setScore(SimpleIntegerProperty score) {
		this.score = score;
	}

	public String getDate() {
		return date.get();
	}

	public void setDate(SimpleStringProperty date) {
		this.date = date;
	}
	
	public String getAuthor() {
		return author.get();
	}

	public void setAuthor(SimpleStringProperty author) {
		this.author = author;
	}
	
	public String getSubReddit() {
		return subReddit.get();
	}

	public void setSubReddit(SimpleStringProperty subReddit) {
		this.subReddit = subReddit;
	}
	
	
	
}
