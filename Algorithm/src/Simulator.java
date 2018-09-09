import java.awt.Point;
import java.util.*;
import Map.*;
import Map.Map;
//JavaFX Librarys
import javafx.application.Application;
import javafx.geometry.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.*;

//Program Classes

public class Simulator extends Application {
	
	//Program Variables
	private Map map;
    private Point wayPoint;
	
	//GUI Components
    private int stage = 1;
    private Canvas mapGrid;
    private Button loadMapBtn, resetMapBtn, realExpBtn, realFastBtn, expBtn, fastBtn, setUpConnBtn;
    private TextField ipTxt;
    private Label ipLbl;
    
    
    
	
	public void start(Stage primaryStage) {
		//Init for Map and Robot
		map = new Map();
		
		
        //Setting the Title and Values for the Window
        primaryStage.setTitle("MDP Group 18: Algorithm Simulator");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
       
        //Map Drawing COmpaonent
        mapGrid = new Canvas(MapConstants.MAP_CELL_SZ*MapConstants.MAP_WIDTH + MapConstants.MAP_OFFSET,MapConstants.MAP_CELL_SZ*MapConstants.MAP_HEIGHT + MapConstants.MAP_OFFSET);
        GraphicsContext gc = mapGrid.getGraphicsContext2D();
        drawMap(gc);
        
        //Canvas MouseEvent
        mapGrid.setOnMouseClicked(MapClick);
        
        
        //Choosing where to place components on the Grid
        grid.add(mapGrid, 0, 0);
        
        //Font and Text Alignment
        
        
        //Dimensions of the Window
        Scene scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        
        primaryStage.show();
        
	}
	
	//Draw the Map Graphics Cells
	private void drawMap(GraphicsContext gc) {
		//Basic Init for the Cells
		gc.setStroke(MapConstants.CW_COLOR);
		gc.setLineWidth(2);
		
		//Draw the Cells on the Map Canvas
		for(int row=0; row< MapConstants.MAP_HEIGHT; row++) {
			
			for(int col=0; col < MapConstants.MAP_WIDTH; col++)
			{
				//Select Color of the Cells
				if(row <= MapConstants.STARTZONE_ROW+1 && col <= MapConstants.STARTZONE_COL+1)
					gc.setFill(MapConstants.SZ_COLOR);
				else if(row >= MapConstants.GOALZONE_ROW-1 && col >= MapConstants.GOALZONE_COL-1)
					gc.setFill(MapConstants.GZ_COLOR);
				else {
					
					if(map.getCell(row, col).isObstacle())
						gc.setFill(MapConstants.OB_COLOR);
					else if(map.getCell(row, col).isExplored())
						gc.setFill(MapConstants.EX_COLOR);
					else
						gc.setFill(MapConstants.UE_COLOR);
				}
				
				//Draw the Cell on the Map based on the Position Indicated
				gc.strokeRect(col*MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET/2, (MapConstants.MAP_CELL_SZ-1)*MapConstants.MAP_HEIGHT - row*MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET/2, MapConstants.MAP_CELL_SZ, MapConstants.MAP_CELL_SZ);
				gc.fillRect(col*MapConstants.MAP_CELL_SZ+ MapConstants.MAP_OFFSET/2, (MapConstants.MAP_CELL_SZ-1)*MapConstants.MAP_HEIGHT - row*MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET/2, MapConstants.MAP_CELL_SZ, MapConstants.MAP_CELL_SZ);
			}
		}
		
	}
	
	public static void main(String[] args) {
        launch(args);
    }
	
	//Mouse Event Handler for 
	private EventHandler<MouseEvent> MapClick = new EventHandler<MouseEvent>() {
    	
    	/* (non-Javadoc)
    	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
    	 */
    	public void handle(MouseEvent event) {
    		System.out.println("X = "+event.getX()+"\n");
    		System.out.println("Y = "+event.getY()+"\n");
    		double mouseX = event.getX();
    		double mouseY = event.getY();
    		
    		int selectedCol  = (int)((mouseX - MapConstants.MAP_OFFSET/2)/MapConstants.MAP_CELL_SZ);
    		int selectedRow  = (int)(MapConstants.MAP_HEIGHT-(mouseY - MapConstants.MAP_OFFSET/2)/MapConstants.MAP_CELL_SZ);
    		//Debug Text
    		System.out.println("Row = "+selectedRow+"\n");
    		System.out.println("Col = "+selectedCol+"\n");
    		
    		wayPoint = new Point(selectedCol,selectedRow);
    		
    	}
    	
    };
        
}
