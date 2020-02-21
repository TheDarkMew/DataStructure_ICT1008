module JavaProjectV3 {
	exports application;
	
	requires org.jsoup;
	requires org.twitter4j.core;
	requires opencsv;
	requires javax.json;
	//requires ejml;
	//requires ejml.simple;
	requires ejml;
	requires java.xml;
	requires protobuf.java;


	
	requires java.logging;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires slf4j.api;
	requires java.desktop;
	requires commons.lang3;
	requires commons.io;
	requires rxjava;
	requires rtree;
	
	opens application to javafx.fxml;
}