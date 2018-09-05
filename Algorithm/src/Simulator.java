import java.util.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.*;

public class Simulator extends Application {
	
	//GUI Components
    private int stage = 1;
	
	public void start(Stage primaryStage) {
        //Setting the Title and Values for the Window
        primaryStage.setTitle("MDP Group 18: Algorithm Simulator");
        GridPane grid = new GridPane();
        GridPane mapGrid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
       
        
        //Font and Text Alignment
        
        
        //Dimensions of the Window
        Scene scene = new Scene(grid, 500, 450);
        primaryStage.setScene(scene);
        
        //Choosing where to place components on the Grid
        
}
