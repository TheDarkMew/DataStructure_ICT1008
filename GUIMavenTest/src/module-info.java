module GUIMavenTest {
	exports application;
	
	requires java.logging;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	
	opens application to javafx.fxml;
}