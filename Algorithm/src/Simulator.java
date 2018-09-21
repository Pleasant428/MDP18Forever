import java.awt.Point;
import java.util.*;
import Map.*;
import Map.Map;
import Robot.*;
import Robot.RobotConstants.Direction;

//JavaFX Librarys
import javafx.application.Application;
import javafx.geometry.*;
import javafx.event.*;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.*;

//Program Classes

public class Simulator extends Application{
	
	//Program Variables
	private Map map; //Used to hold loaded map for sim
	private Map exploredMap;
    private Point wayPoint;
    private Robot robot;
    private boolean sim = true;
	
	//GUI Components
    private int stage = 1;
    private Canvas mapGrid;
    private GraphicsContext gc;
    
    //UI components
    private Button loadMapBtn, resetMapBtn, startBtn, connectBtn, setWaypoint, setRobotBtn;
    private TextField ipTxt, portTxt;
    private Label ipLbl, portLbl;
    private ComboBox<String> modeCB;
    
    
    //Mode Constants
    private final String REAL_FAST = "Real Fastest Path";
    private final String REAL_EXP = "Real Exploration";
    private final String SIM_FAST = "Simulation Fastest Path";
    private final String SIM_EXP = "Simulation Exploration Path";
    
	public void start(Stage primaryStage) {
		//Init for Map and Robot
		map = new Map();
		exploredMap = new Map();
		
		//Default Location at the startzone
		robot = new Robot(sim,Direction.UP,1,1);
		
        //Setting the Title and Values for the Window
        primaryStage.setTitle("MDP Group 18: Algorithm Simulator");
        GridPane grid = new GridPane();
        GridPane controlGrid = new GridPane();
        
        //Grid Settings
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        controlGrid.setAlignment(Pos.TOP_CENTER);
        controlGrid.setHgap(10);
        controlGrid.setVgap(10);
        controlGrid.setPadding(new Insets(25, 25, 25, 25));
         
        //Drawing Component
        mapGrid = new Canvas(MapConstants.MAP_CELL_SZ*MapConstants.MAP_WIDTH+1 + MapConstants.MAP_OFFSET,MapConstants.MAP_CELL_SZ*MapConstants.MAP_HEIGHT+1 + MapConstants.MAP_OFFSET);
        gc = mapGrid.getGraphicsContext2D();
        drawMap(gc);
        drawRobot(gc);
        
        //Canvas MouseEvent
        mapGrid.setOnMouseClicked(MapClick);
        
        //Lbl Init
        ipLbl = new Label("IP Address:");
        ipTxt = new TextField();
        portLbl = new Label("Port:");
        portTxt = new TextField();
        
        //ChoiceBox Init
        modeCB = new ComboBox<String>();
        modeCB.getItems().addAll(
        		REAL_FAST,
        		REAL_EXP,
        		SIM_FAST,
        		SIM_EXP
        		);
        modeCB.getSelectionModel().select(SIM_FAST);
        
        //Buttons Init
        connectBtn = new Button("Connect");
        startBtn = new Button("Start Sim");
        loadMapBtn = new Button("Load Map");
        resetMapBtn = new Button("Reset Map");
        setWaypoint = new Button("Set Waypoint");
        setRobotBtn = new Button("Set Robot Position");
        
        connectBtn.setMaxWidth(500);
        startBtn.setMaxWidth(500);
        loadMapBtn.setMaxWidth(500);
        resetMapBtn.setMaxWidth(500);
        setWaypoint.setMaxWidth(500);
        setRobotBtn.setMaxWidth(500);
        
        //Button ActionListeners
        resetMapBtn.setOnMouseClicked(resetMapBtnClick);
        startBtn.setOnMouseClicked(startBtnClick);
        
        //Layer 1
//		controlGrid.add(ipLbl, 0, 0, 1, 1);
//		controlGrid.add(ipTxt, 1, 0, 3, 1);
//		controlGrid.add(portLbl, 0, 1, 1, 1);
//		controlGrid.add(portTxt, 1, 1, 3, 1);
//		controlGrid.add(connectBtn, 0, 2, 4, 1);
		
		//Layer 2
		controlGrid.add(modeCB, 0, 4, 3, 1);
		controlGrid.add(startBtn, 3, 4, 1, 1);
		//Layer 3
		controlGrid.add(loadMapBtn, 0, 5, 2, 1);
		controlGrid.add(resetMapBtn, 2, 5, 2, 1);
		//Layer 4
		controlGrid.add(setWaypoint, 0, 7, 4, 1);
		//Layer 5
		controlGrid.add(setRobotBtn, 0, 8, 4, 1);
        
        controlGrid.setFillWidth(ipTxt, true);
        controlGrid.setFillWidth(modeCB, true);
        controlGrid.setFillWidth(startBtn, true);
        controlGrid.setFillWidth(loadMapBtn, true);
        controlGrid.setFillWidth(resetMapBtn, true);
        controlGrid.setFillWidth(setWaypoint, true);
        controlGrid.setFillWidth(setRobotBtn, true);
        //Button Init
        
        
        //Choosing where to place components on the Grid
        grid.add(mapGrid, 0, 0);
        grid.add(controlGrid, 1, 0);
        
        //Font and Text Alignment
        
        
        //Dimensions of the Window
        Scene scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
        	public void handle(KeyEvent e) {
        		System.out.println("Before:"+robot.getDirection());
        		switch(e.getCode()){
        			case W:
        				robot.move(robot.getDirection(), true, 1);
        				break;
        			case S:
        				robot.move(robot.getDirection(), false, 1);
        				break;
        			case A:
        				robot.setDirection(Direction.getNext(robot.getDirection()));
        				break;
        			case E:
        				robot.setDirection(Direction.getPrevious(robot.getDirection()));
        				break;
        		}
        		System.out.println("AFTER:"+robot.getDirection());
        		drawMap(gc);
				drawRobot(gc);
        	}
        });
       
        
        primaryStage.show();
        
	}
	
	//Draw Robot
	private void drawRobot(GraphicsContext gc) {
		gc.setStroke(RobotConstants.ROBOT_OUTLINE);
		gc.setLineWidth(2);
		
		gc.setFill(RobotConstants.ROBOT_BODY);
		
		int col = robot.getPosition().x-1;
		int row = robot.getPosition().y+1;
		int dirCol=0, dirRow=0;
		
		gc.strokeOval(col*MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET/2, (MapConstants.MAP_CELL_SZ-1)*MapConstants.MAP_HEIGHT - row*MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET/2, 3*MapConstants.MAP_CELL_SZ, 3*MapConstants.MAP_CELL_SZ);
		gc.fillOval(col*MapConstants.MAP_CELL_SZ+ MapConstants.MAP_OFFSET/2, (MapConstants.MAP_CELL_SZ-1)*MapConstants.MAP_HEIGHT - row*MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET/2, 3*MapConstants.MAP_CELL_SZ, 3*MapConstants.MAP_CELL_SZ);
		
		gc.setFill(RobotConstants.ROBOT_DIRECTION);
		switch(robot.getDirection()) {
		case UP:
			dirCol = robot.getPosition().x;
			dirRow = robot.getPosition().y + 1;
			break;
		case DOWN:
			dirCol = robot.getPosition().x;
			dirRow = robot.getPosition().y - 1;
			break;
		case LEFT:
			dirCol = robot.getPosition().x - 1;
			dirRow = robot.getPosition().y;
			break;
		case RIGHT:
			dirCol = robot.getPosition().x + 1;
			dirRow = robot.getPosition().y;
			break;
		}
		System.out.print("col: "+dirCol+" row:"+dirRow);
		gc.fillOval(dirCol*MapConstants.MAP_CELL_SZ+ MapConstants.MAP_OFFSET/2, (MapConstants.MAP_CELL_SZ-1)*MapConstants.MAP_HEIGHT - dirRow*MapConstants.MAP_CELL_SZ+ MapConstants.MAP_OFFSET/2, MapConstants.MAP_CELL_SZ, MapConstants.MAP_CELL_SZ);
		
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
	
	//Mouse Event Handler for clicking and detecting Location
	private EventHandler<MouseEvent> MapClick = new EventHandler<MouseEvent>() {
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
    
    //Event Handler for StartButton
    private EventHandler<MouseEvent> startBtnClick = new EventHandler<MouseEvent>(){
    	
    	public void handle(MouseEvent event) {
    		
    		String selectedMode = modeCB.getSelectionModel().getSelectedItem();
			switch (selectedMode) {
			case REAL_FAST:
				System.out.println("RF Here");
				break;

			case REAL_EXP:
				System.out.println("RE Here");
				break;

			case SIM_FAST:
				System.out.println("SF Here");
				break;

			case SIM_EXP:
				System.out.println("SE Here");
				break;

			}
    	}
    };
    
  //Event Handler for resetMapBtn
    private EventHandler<MouseEvent> resetMapBtnClick = new EventHandler<MouseEvent>() {
    	public void handle(MouseEvent event) {
    		exploredMap.resetMap();
    		GraphicsContext gc = mapGrid.getGraphicsContext2D();
            drawMap(gc);
    	}  	
    };
    
    
        
}
