package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.chart.LineChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class ProjectController extends Application implements Initializable{
	//fxid for javafx objects
	@FXML private TableView<testTable> twittertableView;
	@FXML private TableView<testRedditTable> reddittableView;
	@FXML private TableColumn<testTable,String> firstNameColumn;
	@FXML private TableColumn<testTable,String> lastNameColumn;
	@FXML private TableColumn<testTable,String> birthdayColumn;
	@FXML private TableColumn<testTable,String> genderColumn;
	@FXML private TableColumn<testTable,String> professionColumn;
	@FXML private TableColumn<testRedditTable,String> commentsColumn;
	@FXML private TableColumn<testRedditTable,String> userColumn;
	@FXML private TableColumn<testRedditTable,String> pointsColumn;
	
	 
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
		URL url = getClass().getResource("DummyData.csv");
        String CsvFile = new String(url.getPath());
        String FieldDelimiter = ",";
 
        BufferedReader br;
 
        try {
            br = new BufferedReader(new FileReader(CsvFile));
 
            String line;
            while ((line = br.readLine()) != null) 
            {
                String[] fields = line.split(FieldDelimiter, -1);
                testTable test = new testTable(fields[0], fields[1], fields[2], fields[3], fields[4]);     
                dataList.add(test);
            }
        }
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(ProjectController.class.getName())
                    .log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(ProjectController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
		return dataList;
 
    } 
	
	public ObservableList<testRedditTable> readCSV2() {
		ObservableList<testRedditTable> dataList1 = FXCollections.observableArrayList(); 
		URL url = getClass().getResource("TestRedditComments.csv");
        String CsvFile = new String(url.getPath());
        String FieldDelimiter = ",";
 
        BufferedReader br;
 
        try {
            br = new BufferedReader(new FileReader(CsvFile));
 
            String line;
            while ((line = br.readLine()) != null) 
            {
                String[] fields = line.split(FieldDelimiter, -1);
                testRedditTable test = new testRedditTable(fields[0], fields[1], fields[2]);     
                dataList1.add(test);
            }
        }
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(ProjectController.class.getName())
                    .log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(ProjectController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
		return dataList1;
 
    } 
	
	//button method to load the line chart
	public void loadStatsBtn(ActionEvent e) {
//		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
//		lineChart.getData().clear();
//		lineChart.getData().add();
	}

	
	//initialize columns with variables as cells from testTable.java and testRedditTable.java
	//set both tables with readCSV() and readCSV2 method
    public void initialize(URL location, ResourceBundle resources) 
    { 	
    	firstNameColumn.setCellValueFactory(new PropertyValueFactory<testTable, String>("firstName"));
    	lastNameColumn.setCellValueFactory(new PropertyValueFactory<testTable, String>("lastName"));
    	birthdayColumn.setCellValueFactory(new PropertyValueFactory<testTable, String>("birthday"));
    	genderColumn.setCellValueFactory(new PropertyValueFactory<testTable, String>("gender"));
    	professionColumn.setCellValueFactory(new PropertyValueFactory<testTable, String>("profession"));
    	commentsColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("comments"));
    	userColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("user"));
    	pointsColumn.setCellValueFactory(new PropertyValueFactory<testRedditTable, String>("points"));
    	twittertableView.setItems(readCSV());
    	reddittableView.setItems(readCSV2());
    }
    
    public static void main(String[] args) {
    	launch(args);
    }    
}

