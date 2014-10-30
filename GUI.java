/* 
 * Class:
 *    GUI
 * Author:
 *    Justin Hurley
 * Summary: 
 *    Handles the GUI interface of the Journal
 */

import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/*
 * Class: GUI
 */
public class GUI extends Application {
    
    private BorderPane root;               /* Main borderPane */
    private ListView<String> entryList;    /* List view storing Entries */
    private SpirtualJournal journal;       /* Journal class */
    private TextField dateField;           /* Stores the date */
    private TextArea entryContent;         /* Stores the entry text */
    private ComboBox selection;            /* Selection of List view*/
    
    /*
     * refreshs the Date List
     */
    private void refreshDateEntryList(){
        entryList.getItems().clear();
        List<String> dates = journal.getDates();
        
        for(int i =0; i < dates.size(); i++) {
            entryList.getItems().add(dates.get(i));
        }
    }
    
    /*
     * refreshs the Scripture List
     */
    private void refreshScriptureList(){
        entryList.getItems().clear();
        List<String> scriptures = journal.getListByScriptures();
        
        for(int i=0; i < scriptures.size(); i++) {
            entryList.getItems().add(scriptures.get(i));
        }  
    }
    
    /*
     * refreshs the Topic List
     */
    private void refreshTopicList(){
        entryList.getItems().clear();
        List<String> topics = journal.getListByTopic();
        
        for(int i=0; i < topics.size(); i++) {
            entryList.getItems().add(topics.get(i));
        }
    }
    
    /*
     * Starts the GUI
     */
    @Override
    public void start(Stage primaryStage) {
        //set up private variables
        journal = new SpirtualJournal();
        root = new BorderPane();
        
        //set up Border pane
        root.setTop(getMenu());
        root.setCenter(getMiddle());
        root.setRight(getRight());
        
        //set the scene
        primaryStage.setTitle("Scripture Journal");
        Scene scene = new Scene(root, 650, 440);
       
        //display the GUI
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /*
     * handles the right side of the GUI
     */
    private GridPane getRight(){
        GridPane grid = new GridPane();
                
        Text text = new Text("Entry List by ");
        
        //set up selection for the List view
        selection = new ComboBox();
        selection.getItems().addAll(
            "Date",
            "Scripture",
            "Topic"
        );
        
        selection.setValue("Date");
        
        //change the viewing list to the user
        selection.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (selection.getValue().equals("Date") ) {
                    refreshDateEntryList();
                } else if (selection.getValue().equals("Scripture")){
                    refreshScriptureList();
                } else if (selection.getValue().equals("Topic")){
                    refreshTopicList();
                }
            }             
        });
        
        //Setup so the text and drop menu appear next to eachother
        HBox topRow = new HBox();
        topRow.getChildren().addAll(text, selection);
        
        //set view list settings
        entryList = new ListView<>();
        entryList.setPrefHeight(400);
        entryList.setPrefWidth(200);
        
        Button btn = new Button();
        btn.setText("Load Entry");
        
        //action for loading an Entry
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dateField.clear();
                entryContent.clear();                
                
                //choose action base on user Selection of ListView
                if (selection.getValue() == "Date")
                {
                    int listIndex = entryList.getSelectionModel().getSelectedIndex();

                    dateField.setText(journal.getEntryByIndex(listIndex).getDate());
                    entryContent.setText(journal.getEntryByIndex(listIndex).getText());
                    journal.deleteEntryByIndex(listIndex);

                    refreshDateEntryList();
                } else if (selection.getValue() == "Topic"){
                    int listIndex = entryList.getSelectionModel().getSelectedIndex();
                    Entry entry = journal.deleteEntryByTopicIndex(listIndex);
                   
                    if(entry != null) {
                        dateField.setText(entry.getDate()); 
                        entryContent.setText(entry.getText());

                        refreshScriptureList();
                    }
                    
                } else if (selection.getValue() == "Scripture"){
                    int listIndex = entryList.getSelectionModel().getSelectedIndex();
                    Entry entry = journal.deleteEntryByScriptureIndex(listIndex);
                   
                    if (entry != null) {
                        dateField.setText(entry.getDate()); 
                        entryContent.setText(entry.getText());

                        refreshTopicList();
                    }
                }
            }
        });
        
        //add everything to the grid
        grid.add(topRow,0,0);
        grid.add(entryList, 0, 1);
        grid.add(btn, 0, 2);
        
        return grid;
    }
    
    /*
     *handles the middle of the GUI
     */
    private BorderPane getMiddle(){
        BorderPane borderPane = new BorderPane();

        //date field
        Text dateFormat = new Text("(YYYY-MM-DD)");
        Text dateText = new Text("Date:");
        dateField = new TextField();
        HBox dateHBox = new HBox();
        dateHBox.getChildren().addAll(dateText, dateField, dateFormat);
        dateHBox.setSpacing(10);
        
        borderPane.setTop(dateHBox);
        
        //setup of the Entry text field
        entryContent = new TextArea();
        entryContent.setPrefColumnCount(40);
        entryContent.setPrefRowCount(10);
        entryContent.setWrapText(true);
        
        //Add Entry button
        Button btn = new Button();
        btn.setText("Add Entry");
        
        //adds the entry to the journal class and refreshs the proper view list
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                journal.addEntry(dateField.getText(), entryContent.getText());
                
                if (selection.getValue().equals("Date") ) {
                    refreshDateEntryList();
                } else if (selection.getValue().equals("Scripture")){
                    refreshScriptureList();
                } else if (selection.getValue().equals("Topic")){
                    refreshTopicList();
                }
                
                dateField.clear();
                entryContent.clear();
                dateField.requestFocus();
            }
        });

        borderPane.setCenter(entryContent);
        borderPane.setBottom(btn);
        
        return borderPane;
    }
    
    /*
     *set up the Menu bar
     */
    private MenuBar getMenu(){
        MenuBar menuBar = new MenuBar();
        
        Menu menuFile = new Menu("File");
        
        MenuItem openTXT = new MenuItem("Open text file");
        
        //parse a TXT file
        openTXT.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Text File");
                File file = fileChooser.showOpenDialog(null);
                
                if (file != null){
                    journal.parseTxt(file.getPath());
                    
                    //handles refreshing the proper view List
                    if (selection.getValue().equals("Date") ) {
                        refreshDateEntryList();
                    } else if (selection.getValue().equals("Scripture")){
                        refreshScriptureList();
                    } else if (selection.getValue().equals("Topic")){
                        refreshTopicList();
                    }
                }
            }
        });
        
        MenuItem openXML = new MenuItem("Open XML file");
        
        //parse an XML file
        openXML.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open XML File");
                File file = fileChooser.showOpenDialog(null);
                
                if (file != null){
                    journal.parseXML(file.getPath());
                    
                    //handles refreshing the proper view list
                    if (selection.getValue().equals("Date") ) {
                        refreshDateEntryList();
                    } else if (selection.getValue().equals("Scripture")){
                        refreshScriptureList();
                    } else if (selection.getValue().equals("Topic")){
                        refreshTopicList();
                    }
                }
            }
        });
        
        //write to an XML file
        MenuItem exportXML = new MenuItem("Export XML file");
        exportXML.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open XML File");
                File file = fileChooser.showSaveDialog(null);
                
                if (file != null){
                    journal.writeXML(file.getPath());
                }
            }
        });
        
        //write to a TXT file
        MenuItem exportTXT = new MenuItem("Export text file");
        exportTXT.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open XML File");
                File file = fileChooser.showSaveDialog(null);
                
                if (file != null){
                    journal.writeDoc(file.getPath());
                }
            }
        });
        
        //add all the options
        menuFile.getItems().addAll(openTXT, openXML, exportXML, exportTXT);
        
        //set them to File option
        menuBar.getMenus().addAll(menuFile);
        
        return menuBar;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}