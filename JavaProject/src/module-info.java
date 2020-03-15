module JavaProject {
	exports application;
	
	requires java.logging;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires org.jsoup;
	requires opencsv;
	requires org.twitter4j.core;
	
	opens application to javafx.fxml;
}