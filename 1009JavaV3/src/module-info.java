module JavaProjectV3 {
	exports application;
	
	requires org.jsoup;
	requires org.twitter4j.core;
	requires opencsv;
	requires google.cloud.language;
	requires proto.google.cloud.language.v1;
	requires javafx.base;
	requires proto.google.cloud.language.v1beta2;
	
	//opens application to javafx.fxml;
}