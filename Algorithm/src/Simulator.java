import java.awt.Point;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import Algorithm.*;
import Map.*;
import Map.Cell;
import Map.Map;
import Network.NetMgr;
import Robot.*;
import Robot.RobotConstants.Command;
import Robot.RobotConstants.Direction;

//JavaFX Libraries
import javafx.application.Application;
import javafx.geometry.*;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;

//Program Classes
/**
 * @author Saklani Pankaj
 *
 */
public class Simulator extends Application {

	// Program Variables
	private Map map;
	private Map exploredMap;
	private Point wayPoint = new Point(MapConstants.GOALZONE);
	private Robot robot;
	private boolean sim = true;
	private boolean expMapDraw = true;

	private boolean setObstacle = false;
	private boolean setWaypoint = false;
	private boolean setRobot = false;
	
	// Network Manager to Send and Recieve Messages
	private static final NetMgr netMgr = NetMgr.getInstance();

	// GUI Components
	private Canvas mapGrid;
	private GraphicsContext gc;

	// UI components
	private Button loadMapBtn, saveMapBtn, resetMapBtn, startBtn, connectBtn, setWaypointBtn, setRobotBtn,
			setObstacleBtn;
	private ScrollBar timeLimitSB, coverageLimitSB, stepsSB;
	private TextField timeLimitTxt, coverageLimitTxt, stepsTxt;
	private Label timeLimitLbl, coverageLimitLbl, stepsLbl;
	private ComboBox<String> modeCB;
	private FileChooser fileChooser;

	// Mode Constants
	private final String REAL_FAST = "Real Fastest Path";
	private final String REAL_EXP = "Real Exploration";
	private final String SIM_FAST = "Simulation Fastest Path";
	private final String SIM_EXP = "Simulation Exploration Path";

	// Threads for each of the tasks
	private Thread fastTask, expTask;

	public void start(Stage primaryStage) {
		// Init for Map and Robot
		map = new Map();
		// Set to all explored for loading and saving map
		map.setAllExplored(true);
		exploredMap = new Map();

		// Default Location at the startzone
		robot = new Robot(sim, Direction.UP, 1, 1);
		robot.setStartPos(robot.getPosition().x, robot.getPosition().y, exploredMap);
		
		// Setting the Title and Values for the Window
		primaryStage.setTitle("MDP Group 18: Algorithm Simulator");
		GridPane grid = new GridPane();
		GridPane controlGrid = new GridPane();

		// Grid Settings
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(5, 5, 5, 5));

		controlGrid.setAlignment(Pos.TOP_CENTER);
		controlGrid.setHgap(10);
		controlGrid.setVgap(10);

		// Drawing Component
		mapGrid = new Canvas(MapConstants.MAP_CELL_SZ * MapConstants.MAP_WIDTH + 1 + MapConstants.MAP_OFFSET,
				MapConstants.MAP_CELL_SZ * MapConstants.MAP_HEIGHT + 1 + MapConstants.MAP_OFFSET);
		gc = mapGrid.getGraphicsContext2D();
		expMapDraw = !setObstacle;
		expMapDraw = true;
		
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
				drawMap(expMapDraw);
				drawRobot();
			}
		},100,100);

		// Canvas MouseEvent
		mapGrid.setOnMouseClicked(MapClick);

		// Lbl Init
		timeLimitLbl = new Label("Time Limit: ");
		coverageLimitLbl = new Label("Coverage Limit:");
		timeLimitTxt = new TextField();
		coverageLimitTxt = new TextField();
		timeLimitTxt.setDisable(true);
		coverageLimitTxt.setDisable(true);
		stepsLbl = new Label("Steps: ");
		stepsTxt = new TextField();
		stepsTxt.setDisable(true);
		stepsTxt.setMaxWidth(50);
		timeLimitTxt.setMaxWidth(50);
		coverageLimitTxt.setMaxWidth(50);

		// ChoiceBox Init
		modeCB = new ComboBox<String>();
		modeCB.getItems().addAll(SIM_EXP, SIM_FAST, REAL_EXP, REAL_FAST);
		modeCB.getSelectionModel().select(SIM_EXP);

		// Buttons Init
		connectBtn = new Button("Connect");
		startBtn = new Button("Start Sim");
		loadMapBtn = new Button("Load Map");
		saveMapBtn = new Button("Save Map");
		resetMapBtn = new Button("Reset Map");
		setWaypointBtn = new Button("Set Waypoint");
		setRobotBtn = new Button("Set Robot Position");
		setObstacleBtn = new Button("Set Obstacles");

		// File Chooser
		fileChooser = new FileChooser();

		// ScrollBar
		timeLimitSB = new ScrollBar();
		coverageLimitSB = new ScrollBar();
		stepsSB = new ScrollBar();
		stepsSB.setMin(1);
		stepsSB.setMax(100);
		timeLimitSB.setMin(10);
		timeLimitSB.setMax(240);
		coverageLimitSB.setMin(10);
		coverageLimitSB.setMax(100);

		connectBtn.setMaxWidth(500);
		startBtn.setMaxWidth(500);
		loadMapBtn.setMaxWidth(500);
		saveMapBtn.setMaxWidth(500);
		resetMapBtn.setMaxWidth(500);
		setWaypointBtn.setMaxWidth(500);
		setRobotBtn.setMaxWidth(500);
		setObstacleBtn.setMaxWidth(500);

		// Button ActionListeners
		resetMapBtn.setOnMouseClicked(resetMapBtnClick);
		startBtn.setOnMouseClicked(startBtnClick);
		
		// Click to change Position and Direction of Robot 
		setRobotBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				setRobot = !setRobot;
				if (!setRobot)
					setRobotBtn.setText("Set Robot Position");
				else
					setRobotBtn.setText("Confirm Robot Position");

				setWaypoint = false;
				setObstacle = false;
			}
		});
		
		// Click to change position of Waypoint
		setWaypointBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				setWaypoint = !setWaypoint;
				if(setWaypoint)
					setWaypointBtn.setText("Confirm Waypoint");
				else
					setWaypointBtn.setText("Set Waypoint");
				setObstacle = false;
				setRobot = false;
			}
		});
		
		// Click to place obstacle on the Map (Creating/Modifying Map)
		setObstacleBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				setObstacle = !setObstacle;
				if (!setObstacle) {
					setObstacleBtn.setText("Set Obstacles");
					loadMapBtn.setText("Load Explored Map");
					saveMapBtn.setText("Save Explored Map");
				} else {
					setObstacleBtn.setText("Confirm Obstacles");
					loadMapBtn.setText("Load Map");
					saveMapBtn.setText("Save Map");
				}
				setRobot = false;
				setWaypoint = false;
				expMapDraw = !setObstacle;
			}
		});
		
		// Click to Load Map from txt files using MDF Strings
		loadMapBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				if (setObstacle) {
					fileChooser.setTitle("Choose file to load Map from");
					File file = fileChooser.showOpenDialog(primaryStage);
					if (file != null) {
						MapDescriptor.loadMapFromDisk(map, file.getAbsolutePath());
					}
					expMapDraw = false;
				} else {
					fileChooser.setTitle("Choose file to load ExploredMap to");
					File file = fileChooser.showOpenDialog(primaryStage);
					if (file != null) {
						MapDescriptor.loadMapFromDisk(exploredMap, file.getAbsolutePath());
					}
					expMapDraw = true;
				}

			}
		});
		
		// Click to Save Map save MDF String of Map to a txt file
		saveMapBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				if (setObstacle) {
					fileChooser.setTitle("Choose file to save Map to");
					File file = fileChooser.showOpenDialog(primaryStage);
					if (file != null) {
						MapDescriptor.saveMapToDisk(map, file.getAbsolutePath());
					}
				} else {
					fileChooser.setTitle("Choose file to save ExploredMap to");
					File file = fileChooser.showOpenDialog(primaryStage);
					if (file != null) {
						MapDescriptor.saveMapToDisk(exploredMap, file.getAbsolutePath());
					}
				}

			}
		});

		timeLimitSB.valueProperty().addListener(change -> {
			timeLimitTxt.setText("" + (int) timeLimitSB.getValue() + " s");
		});

		coverageLimitSB.valueProperty().addListener(change -> {
			coverageLimitTxt.setText("" + (int) coverageLimitSB.getValue() + "%");
		});

		stepsSB.valueProperty().addListener(change -> {
			stepsTxt.setText("" + (int) stepsSB.getValue());
		});

		// Layer 1 (6 Grids)
		// controlGrid.add(ipLbl, 0, 0, 1, 1);
		// controlGrid.add(ipTxt, 1, 0, 3, 1);
		// controlGrid.add(portLbl, 0, 1, 1, 1);
		// controlGrid.add(portTxt, 1, 1, 3, 1);
		// controlGrid.add(connectBtn, 0, 2, 4, 1);
		// Layer 2
		controlGrid.add(timeLimitLbl, 0, 2, 1, 1);
		controlGrid.add(timeLimitSB, 1, 2, 4, 1);
		controlGrid.add(timeLimitTxt, 5, 2, 1, 1);

		controlGrid.add(coverageLimitLbl, 0, 3, 1, 1);
		controlGrid.add(coverageLimitSB, 1, 3, 4, 1);
		controlGrid.add(coverageLimitTxt, 5, 3, 1, 1);

		controlGrid.add(stepsLbl, 0, 4, 1, 1);
		controlGrid.add(stepsSB, 1, 4, 4, 1);
		controlGrid.add(stepsTxt, 5, 4, 1, 1);

		// Layer 2
		controlGrid.add(modeCB, 0, 5, 3, 1);
		controlGrid.add(startBtn, 3, 5, 3, 1);

		// Layer 3
		controlGrid.add(loadMapBtn, 0, 6, 3, 1);
		controlGrid.add(saveMapBtn, 3, 6, 3, 1);
		controlGrid.add(resetMapBtn, 0, 7, 6, 1);
		// Layer 4
		controlGrid.add(setWaypointBtn, 0, 8, 6, 1);
		// Layer 5
		controlGrid.add(setRobotBtn, 0, 9, 2, 1);
		controlGrid.add(setObstacleBtn, 2, 9, 4, 1);

		GridPane.setFillWidth(modeCB, true);
		
		// Button Init
		// Choosing where to place components on the Grid
		grid.add(mapGrid, 0, 0);
		grid.add(controlGrid, 1, 0);

		// Font and Text Alignment

		// Dimensions of the Window
		Scene scene = new Scene(grid, 800, 600);
		primaryStage.setScene(scene);
		
		//Manually Move Actual Robot for Testing
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				sim = false;
				robot.setSim(sim);
				System.out.println("System movement");
				if (!netMgr.isConnected()) {
					netMgr.startConn();
					netMgr.send("Alg|Ard|S|0");
					robot.sense(exploredMap, map);
				}
				switch (e.getCode()) {
				case W:
					robot.move(Command.FORWARD, 1, exploredMap);
					robot.sense(exploredMap, map);
					break;
				case S:
					robot.move(Command.BACKWARD, 1, exploredMap);
					robot.sense(exploredMap, map);
					break;
				case A:
					robot.move(Command.TURN_RIGHT, 1, exploredMap);
					robot.sense(exploredMap, map);
					break;
				case D:
					robot.move(Command.TURN_LEFT, 1, exploredMap);
					robot.sense(exploredMap, map);
					break;
				default:
					break;
				}
				System.out.println("Robot Direction AFTER:" + robot.getDirection());
			}
		});

		primaryStage.show();

	}

	// Draw the Map Graphics Cells
	private void drawMap(boolean explored) {
		// Basic Init for the Cells
		gc.setStroke(MapConstants.CW_COLOR);
		gc.setLineWidth(2);

		// Draw the Cells on the Map Canvas
		for (int row = 0; row < MapConstants.MAP_HEIGHT; row++) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				// Select Color of the Cells
				if (row <= MapConstants.STARTZONE_ROW + 1 && col <= MapConstants.STARTZONE_COL + 1)
					gc.setFill(MapConstants.SZ_COLOR);
				else if (row >= MapConstants.GOALZONE_ROW - 1 && col >= MapConstants.GOALZONE_COL - 1)
					gc.setFill(MapConstants.GZ_COLOR);
				else {
					if (explored) {
						if (exploredMap.getCell(row, col).isObstacle())
							gc.setFill(MapConstants.OB_COLOR);
						else if (exploredMap.getCell(row, col).isPath())
							gc.setFill(MapConstants.PH_COLOR);
						else if (exploredMap.getCell(row, col).isMoveThru())
							gc.setFill(MapConstants.THRU_COLOR);
						else if (exploredMap.getCell(row, col).isExplored())
							gc.setFill(MapConstants.EX_COLOR);
						else
							gc.setFill(MapConstants.UE_COLOR);
					} else {
						if (map.getCell(row, col).isObstacle())
							gc.setFill(MapConstants.OB_COLOR);
						else
							gc.setFill(MapConstants.EX_COLOR);
					}
				}

				// Draw the Cell on the Map based on the Position Indicated
				gc.strokeRect(col * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
						(MapConstants.MAP_CELL_SZ - 1) * MapConstants.MAP_HEIGHT - row * MapConstants.MAP_CELL_SZ
								+ MapConstants.MAP_OFFSET / 2,
						MapConstants.MAP_CELL_SZ, MapConstants.MAP_CELL_SZ);
				gc.fillRect(col * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
						(MapConstants.MAP_CELL_SZ - 1) * MapConstants.MAP_HEIGHT - row * MapConstants.MAP_CELL_SZ
								+ MapConstants.MAP_OFFSET / 2,
						MapConstants.MAP_CELL_SZ, MapConstants.MAP_CELL_SZ);
			}

			// Draw waypoint on the Map
			if (wayPoint != null) {
				gc.setFill(MapConstants.WP_COLOR);
				gc.fillRect(wayPoint.getX() * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
						(MapConstants.MAP_CELL_SZ - 1) * MapConstants.MAP_HEIGHT
								- wayPoint.getY() * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
						MapConstants.MAP_CELL_SZ, MapConstants.MAP_CELL_SZ);
				gc.setFill(Color.BLACK);
				gc.fillText("W",
						wayPoint.getX() * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2
								+ MapConstants.CELL_CM / 2,
						(MapConstants.MAP_CELL_SZ - 1) * MapConstants.MAP_HEIGHT
								- (wayPoint.getY() - 1) * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2
								- MapConstants.CELL_CM / 2);
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	// Mouse Event Handler for Clicking on Map (Effect Depends on What Action Button Was Pressed)
	private EventHandler<MouseEvent> MapClick = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			double mouseX = event.getX();
			double mouseY = event.getY();
			
			// Derive Selected Cell based on Mouse Position
			int selectedCol = (int) ((mouseX - MapConstants.MAP_OFFSET / 2) / MapConstants.MAP_CELL_SZ);
			int selectedRow = (int) (MapConstants.MAP_HEIGHT
					- (mouseY - MapConstants.MAP_OFFSET / 2) / MapConstants.MAP_CELL_SZ);
			
			// Display Status of Cell
			System.out.println(exploredMap.getCell(selectedRow, selectedCol).toString() + " validMove:"
					+ exploredMap.checkValidMove(selectedRow, selectedCol));
			
			// Actions Based on Button Pressed Previously
			if (setWaypoint) {
				System.out.println(setWayPoint(selectedRow, selectedCol)
						? "New WayPoint set at row: " + selectedRow + " col: " + selectedCol
						: "Unable to put waypoint at obstacle or virtual wall!");
			}
			if (setRobot)
				System.out.println(setRobotLocation(selectedRow, selectedCol) ? "Robot Position has changed"
						: "Unable to put Robot at obstacle or virtual wall!");

			if (setObstacle||!setObstacle) {
				if (event.getButton() == MouseButton.PRIMARY)
					System.out.println(setObstacle(selectedRow, selectedCol)
							? "New Obstacle Added at row: " + selectedRow + " col: " + selectedCol
							: "Obstacle at location alredy exists!");
				else
					System.out.println(removeObstacle(selectedRow, selectedCol)
							? "Obstacle removed at row: " + selectedRow + " col: " + selectedCol
							: "Obstacle at location does not exists!");

			}
			// if adding obstacle display the Map instead of exploredMap for ease of obstacle addition
			if (setObstacle)
				expMapDraw = false;
			else
				expMapDraw = true;
		}

	};

	// Place Obstacle at Location
	private boolean setObstacle(int row, int col) {
		// Check to make sure the cell is valid and is not a existing obstacle
		if (map.checkValidCell(row, col) && !map.getCell(row, col).isObstacle()) {
			map.getCell(row, col).setObstacle(true);

			// Set the virtual wall around the obstacle
			for (int r = row - 1; r <= row + 1; r++)
				for (int c = col - 1; c <= col + 1; c++)
					if (map.checkValidCell(r, c))
						map.getCell(r, c).setVirtualWall(true);

			return true;
		}
		return false;
	}

	// Remove Obstacle at Location
	private boolean removeObstacle(int row, int col) {
		// Check to make sure the cell is valid and is not a existing obstacle
		if (map.checkValidCell(row, col) && map.getCell(row, col).isObstacle()) {
			map.getCell(row, col).setObstacle(false);

			// Set the virtual wall around the obstacle
			for (int r = row - 1; r <= row + 1; r++)
				for (int c = col - 1; c <= col + 1; c++)
					if (map.checkValidCell(r, c))
						map.getCell(r, c).setVirtualWall(false);

			reinitVirtualWall();
			return true;
		}
		return false;
	}

	// Reinit virtual walls around obstacle
	private void reinitVirtualWall() {
		for (int row = 0; row < MapConstants.MAP_HEIGHT; row++) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				if (map.getCell(row, col).isObstacle()) {
					for (int r = row - 1; r <= row + 1; r++)
						for (int c = col - 1; c <= col + 1; c++)
							if (map.checkValidCell(r, c))
								map.getCell(r, c).setVirtualWall(true);
				}
			}
		}
	}

	// Set the waypoint
	private boolean setWayPoint(int row, int col) {
		if (exploredMap.wayPointClear(row, col)) {
			if (wayPoint != null)
				exploredMap.getCell(wayPoint).setWayPoint(false);

			wayPoint = new Point(col, row);
			if (!setObstacle)
				expMapDraw = false;
			return true;
		} else
			return false;
	}

	// Set Robot Location and Rotate
	private boolean setRobotLocation(int row, int col) {
		if (map.checkValidMove(row, col)) {
			Point point = new Point(col, row);
			if (robot.getPosition().equals(point)) {
				robot.move(Command.TURN_LEFT, RobotConstants.MOVE_STEPS, exploredMap);
				System.out.println("Robot Direction Changed to " + robot.getDirection().name());
			} else {
				robot.setStartPos(col, row, exploredMap);
				System.out.println("Robot moved to new position at row: " + row + " col:" + col);
			}

			return true;
		}
		return false;
	}

	// Event Handler for StartButton
	private EventHandler<MouseEvent> startBtnClick = new EventHandler<MouseEvent>() {

		public void handle(MouseEvent event) {

			String selectedMode = modeCB.getSelectionModel().getSelectedItem();
			switch (selectedMode) {
			//Run Just the Fastest Path on Real Robot
			case REAL_FAST:
				netMgr.startConn();
				sim = false;
				robot.setSim(false);
				exploredMap.removePaths();
				expMapDraw = true;
				fastTask = new Thread(new FastTask());
				fastTask.start();
			//Run Complete Exploration + Fastest Path on Real Robot
			case REAL_EXP:
				netMgr.startConn();
				sim = false;
				robot.setSim(false);
				
				//Start Exploration Thread (Run Concurrently with Sim)
				expTask = new Thread(new ExplorationTask());
				expTask.start();
				break;
			//Run Fastest Path on Simulator
			case SIM_FAST:
				sim = true;
				expMapDraw = true;
				robot.setFastSense(true);
				exploredMap.removePaths();
				fastTask = new Thread(new FastTask());
				fastTask.start();
				break;
			//Run Exploration on Simulator
			case SIM_EXP:
				sim = true;
				robot.sense(exploredMap, map);
				expMapDraw = true;
				expTask = new Thread(new ExplorationTask());
				expTask.start();
				break;

			}
		}
	};

	//Thread For Exploration
	class ExplorationTask extends Task<Integer> {
		@Override
		protected Integer call() throws Exception {
			String msg = null;
			Command c;
			// Real Run Wait for Android's Command
			if (!sim) {
				
				// Loop till Start Exploration Command Recieved
				do {
					robot.setFastSense(false);
					msg = netMgr.receive();
					String[] msgArr = msg.split("\\|");
					System.out.println("Calibrating: " + msgArr[2]);
					c = Command.ERROR;
					
					// Calibrate Command Recieved
					if (msgArr[2].compareToIgnoreCase("C") == 0) {
						System.out.println("Calibrating");
						for (int i = 0; i < 4; i++) {
							robot.move(Command.TURN_RIGHT, RobotConstants.MOVE_STEPS, exploredMap);
							senseAndAlign();
						}
						netMgr.send("Alg|Ard|" + Command.ALIGN_RIGHT.ordinal() + "|0");
						msg = netMgr.receive();
						System.out.println("Done Calibrating");
					}
					
					else {
						c = Command.values()[Integer.parseInt(msgArr[2])];
					}
					
					// Initial Robot Position, Direction and Waypoint Command
					if (c == Command.ROBOT_POS) {
						//Split Message
						String[] data = msgArr[3].split("\\,");
						int col = Integer.parseInt(data[0]);
						int row = Integer.parseInt(data[1]);
						Direction dir = Direction.values()[Integer.parseInt(data[2])];
						int wayCol = Integer.parseInt(data[3]);
						int wayRow = Integer.parseInt(data[4]);
						
						// Update Robot Position, Direction and Waypoint
						robot.setStartPos(col, row, exploredMap);
						while(robot.getDirection()!=dir) {
							robot.rotateSensors(true);
							robot.setDirection(Direction.turnRight(robot.getDirection()));
						}
						wayPoint = new Point(wayCol, wayRow);
					} else if (c == Command.START_EXP) {
						netMgr.send("Alg|Ard|S|0");
					}
				} while (c != Command.START_EXP);
			}
			
			// Robot Sense before starting
			robot.sense(exploredMap, map);
			
			// Take Exploration Variables from Sim
			System.out.println("coverage: " + coverageLimitSB.getValue());
			System.out.println("time: " + timeLimitSB.getValue());
			double coverageLimit = (int) (coverageLimitSB.getValue());
			int timeLimit = (int) (timeLimitSB.getValue() * 1000);
			int steps = (int) (stepsSB.getValue());
			// Limits not set
			if (coverageLimit == 0)
				coverageLimit = 100;
			if (timeLimit == 0)
				timeLimit = 240000;
			if (steps == 0)
				steps = 5;
			
			//Start Exploration, using Exploration Class
			Exploration explore = new Exploration(exploredMap, map, robot, coverageLimit, timeLimit, steps, sim);
			explore.exploration(new Point(MapConstants.STARTZONE_COL, MapConstants.STARTZONE_COL));
			
			// If Real Run Send End Exploration Msg to Android
			if (!sim) {
				netMgr.send("Alg|And|DONE|"+exploredMap.detectedImgToString());
				netMgr.send("Alg|And|" + Command.ENDEXP + "|");
				Command com = null;
				do {
					String[] msgArr = NetMgr.getInstance().receive().split("\\|");
					com = Command.values()[Integer.parseInt(msgArr[2])];
					System.out.println("Fastest path msg :" + msgArr[2]);
					if (com == Command.START_FAST) {
						sim = false;
						System.out.println("RF Here");
						fastTask = new Thread(new FastTask());
						fastTask.start();
						break;
					}
				} while (com != Command.START_FAST);
			}

			return 1;
		}
	}

	//Normal
	public void senseAndAlign() {
		String msg = null;
		double[][] sensorData = new double[6][2];
		msg = NetMgr.getInstance().receive();
		String[] msgArr = msg.split("\\|");
		String[] strSensor = msgArr[3].split("\\,");
		System.out.println("Recieved " + strSensor.length + " sensor data");
		// Translate string to integer
		for (int i = 0; i < strSensor.length; i++) {
			String[] arrSensorStr = strSensor[i].split("\\:");
			sensorData[i][0] = Double.parseDouble(arrSensorStr[1]);
			sensorData[i][1] = Double.parseDouble(arrSensorStr[2]);
		}

		// Discrepancy detected among the sensor data received
		if (sensorData[0][1] == 1 || sensorData[2][1] == 1) {
			netMgr.send("Alg|Ard|" + Command.ALIGN_FRONT.ordinal() + "|1");
			netMgr.receive();
		}
	}
	

	class FastTask extends Task<Integer> {
		@Override
		protected Integer call() throws Exception {
			robot.setFastSense(true);
			double startT = System.currentTimeMillis();
			double endT = 0;
			FastestPath fp = new FastestPath(exploredMap, robot);
			ArrayList<Cell> path;
			
			// Get Path to Waypoint
			path = fp.run(new Point(robot.getPosition().x, robot.getPosition().y), wayPoint, robot.getDirection());
			// Get Path from Waypoint to Goal and Append to previous path
			path.addAll(fp.run(wayPoint, MapConstants.GOALZONE, robot.getDirection()));

			fp.displayFastestPath(path, true);
			ArrayList<Command> commands = fp.getPathCommands(path);

			int steps = (int) (stepsSB.getValue());
			// Limits not set
			if (steps == 0)
				steps = 5;

			int moves = 0;
			System.out.println(commands);
			Command c = null;
			
			//Sends Commands 1 by 1 to Robot
			for (int i = 0; i < commands.size(); i++) {
				c = commands.get(i);
				if (sim) {
					try {
						TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED / steps);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				robot.move(c, RobotConstants.MOVE_STEPS, exploredMap);
				System.out.println("c:"+commands.get(i)+" Condition:"+(commands.get(i)==Command.FORWARD|| commands.get(i) == Command.BACKWARD));
				System.out.println("index: "+i+" condition: "+(i==(commands.size()-1)));
				
				// Add Multiple Forward together and send as 1 move
				if (c == Command.FORWARD && moves<9) {
					// System.out.println("moves "+moves);
					moves++;
					// If last command
					if (i == (commands.size() - 1)) {
						robot.move(c, moves, exploredMap);
						robot.sense(exploredMap, map);
					}
				// Send other Command to Robots
				} else {
					if (moves > 0) {
						System.out.println("Moving Forwards "+moves+" steps.");
						robot.move(Command.FORWARD, moves, exploredMap);
						robot.sense(exploredMap, map);
					}
					robot.move(c, RobotConstants.MOVE_STEPS, exploredMap);
					robot.sense(exploredMap, map);
					moves = 0;
				}
			}
			
			//Send End of Fastest to Robot
			if (!sim) {
				netMgr.send("Alg|Ard|"+RobotConstants.Command.ALIGN_FRONT.ordinal()+"|");
				netMgr.send("Alg|And|" + RobotConstants.Command.ENDFAST+"|");
			}
			
			endT = System.currentTimeMillis();
			int seconds = (int)((endT - startT)/1000%60);
			int minutes = (int)((endT - startT)/1000/60);
			System.out.println("Total Time: "+minutes+"mins "+seconds+"seconds");
			return 1;
		}
	}

	// Event Handler for resetMapBtn will Clear Entire Map
	private EventHandler<MouseEvent> resetMapBtnClick = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			if (setObstacle) {
				map.resetMap();
				map.setAllExplored(true);
			} else {
				exploredMap.resetMap();
				exploredMap.setAllExplored(false);
			}
			robot.setStartPos(robot.getPosition().x, robot.getPosition().y, exploredMap);
		}
	};
	
	// Draw Method for Robot
	public void drawRobot() {
		
		// Draw Robot Body
		gc.setStroke(RobotConstants.ROBOT_OUTLINE);
		gc.setLineWidth(2);

		gc.setFill(RobotConstants.ROBOT_BODY);

		int col = robot.getPosition().x - 1;
		int row = robot.getPosition().y + 1;
		int dirCol = 0, dirRow = 0;

		gc.strokeOval(col * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
				(MapConstants.MAP_CELL_SZ - 1) * MapConstants.MAP_HEIGHT - row * MapConstants.MAP_CELL_SZ
						+ MapConstants.MAP_OFFSET / 2,
				3 * MapConstants.MAP_CELL_SZ, 3 * MapConstants.MAP_CELL_SZ);
		gc.fillOval(col * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
				(MapConstants.MAP_CELL_SZ - 1) * MapConstants.MAP_HEIGHT - row * MapConstants.MAP_CELL_SZ
						+ MapConstants.MAP_OFFSET / 2,
				3 * MapConstants.MAP_CELL_SZ, 3 * MapConstants.MAP_CELL_SZ);
		
		// Draw Robot Direction (front of Robot)
		gc.setFill(RobotConstants.ROBOT_DIRECTION);
		switch (robot.getDirection()) {
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
		gc.fillOval(dirCol * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
				(MapConstants.MAP_CELL_SZ - 1) * MapConstants.MAP_HEIGHT - dirRow * MapConstants.MAP_CELL_SZ
						+ MapConstants.MAP_OFFSET / 2,
				MapConstants.MAP_CELL_SZ, MapConstants.MAP_CELL_SZ);
		
		// Draw Sensors on Map (text)
		gc.setFill(Color.BLACK);
		for (Sensor s : robot.getSensorList()) {
			gc.fillText(s.getId(), s.getCol() * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
					(MapConstants.MAP_CELL_SZ) * MapConstants.MAP_HEIGHT - s.getRow() * MapConstants.MAP_CELL_SZ
							+ MapConstants.MAP_OFFSET / 2);
		}

	}
}
