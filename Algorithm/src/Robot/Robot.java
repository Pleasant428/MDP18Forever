/**
 * 
 */
package Robot;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import Map.*;
import Robot.RobotConstants.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import Robot.RobotConstants.Command;

/**
 * @author Saklani Pankaj
 *
 */
public class Robot {

	private ArrayList<Sensor> sensorList;

	private boolean sim;
	private boolean reachedGoal;
	private Direction direction;
	
	private GraphicsContext gc;

	public GraphicsContext getGc() {
		return gc;
	}

	public void setGc(GraphicsContext gc) {
		this.gc = gc;
	}

	public boolean isReachedGoal() {
		return reachedGoal;
	}

	public void setReachedGoal(boolean reachedGoal) {
		this.reachedGoal = reachedGoal;
	}

	public Robot(boolean sim, Direction dir, int row, int col) {
		this.sim = sim;
		this.direction = dir;
		this.reachedGoal = false;
		this.pos = new Point(col, row);
		sensorList = new ArrayList<Sensor>();
		
		// Initializing the Sensors
		/* ID information for sensors:
		 * 
		 * SF1/SL1 SF2 	SF3/SR1
		 *  X 		X	 X
		 * 	X		X	SR2
		 * 
		 * 1st Letter:
		 * S: Short Range Sensor L:Long Range Sensor
		 * 
		 * 2nd Letter:
		 * F: front L: Left R:Right
		 * 
		 * 3rd Letter:
		 * Basic Identifier
		*/
		
		//Front Sensors same direction (Init with respect to UP direction)
		Sensor SF1 = new Sensor("SF1",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col-1, Direction.UP);
		Sensor SF2 = new Sensor("SF2",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col,  Direction.UP);
		Sensor SF3 = new Sensor("SF3",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col+1,  Direction.UP);
		
		//RIGHT Sensor Next Direction of Direction
		Sensor SR1 = new Sensor("SR1",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col+1,  Direction.RIGHT);
		Sensor SR2 = new Sensor("SR2",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row-1, col+1, Direction.RIGHT);
		
		//LEFT Sensor Prev Direction of Robot Direction
		Sensor LL1 = new Sensor("LL1",RobotConstants.LONG_MIN, RobotConstants.LONG_MAX, row+1, col-1,  Direction.LEFT);
		
		sensorList.add(SF1);
		sensorList.add(SF2);
		sensorList.add(SF3);
		sensorList.add(SR1);
		sensorList.add(SR2);
		sensorList.add(LL1);
		
		switch(dir) {
		case LEFT:
			rotateSensors(true);
			break;
		case RIGHT:
			rotateSensors(false);
			break;
		case DOWN:
			rotateSensors(true);
			rotateSensors(true);
			break;
		}
	
	}
	
	public ArrayList<Sensor> getSensorList() {
		return sensorList;
	}

	public void setSensorList(ArrayList<Sensor> sensorList) {
		this.sensorList = sensorList;
	}
	
	
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	private Point pos;
	
	
	public Sensor getSensor(String id) {
		for(Sensor s: sensorList)
			if(s.getId().equals(id))
				return s;
		
		return null;
	}
	
	public void rotateSensors(boolean left) {
		double angle = 0;
		int newCol, newRow;
		
		if(left)
			angle = Math.PI/2;
		else
			angle = -Math.PI/2;
		
		//Rotation Formula used: x = cos(a) * (x1 - x0) - sin(a) * (y1 - y0) + x0
		//						 y = sin(a) * (x1 - x0) + cos(a) * (y1 - y0) + y0
		for(Sensor s: sensorList) {
			if(left)
				s.setSensorDir(Direction.getNext(s.getSensorDir()));
			else
				s.setSensorDir(Direction.getPrevious(s.getSensorDir()));
			
			
			newCol = (int)Math.round((Math.cos(angle)*(s.getCol() - pos.x) - Math.sin(angle)*(s.getRow() - pos.y) + pos.x));
			newRow = (int)Math.round((Math.sin(angle)*(s.getCol() - pos.x) - Math.cos(angle)*(s.getRow() - pos.y) + pos.y));
			s.setPos(newCol, newRow);
		}
	}
	
	//Movement Method for robot and Sensors
	public void move(Direction dir, boolean forward, int steps, Map exploredMap) {
		int rowInc = 0, colInc=0;
		switch(dir) {
		case UP:
			rowInc = 1;
			colInc = 0;
			break;
		case LEFT:
			rowInc = 0;
			colInc = -1;
			break;
		case DOWN:
			rowInc = -1;
			colInc = 0;
			break;
		case RIGHT:
			rowInc = 0;
			colInc = 1;
			break;
		}
		
		if(!forward)
		{
			rowInc *= -1;
			colInc *= -1;
		}
		
		if(exploredMap.checkValidMove(pos.y+rowInc*steps, pos.x+ colInc*steps))
		{
			setPosition(pos.x+ colInc*steps, pos.y+rowInc*steps);
		}
	}
	
	//Moving using the Command enum
	public void move(Command m, int steps, Map exploredMap) {
		switch(m) {
		case FORWARD:
			move(direction, true, steps, exploredMap);
			break;
		case BACKWARD:
			move(direction, false, steps, exploredMap);
			break;
		case TURN_LEFT:
			direction = Direction.getNext(direction);
			rotateSensors(true);
			break;
		case TURN_RIGHT:
			direction = Direction.getPrevious(direction);
			rotateSensors(false);
			break;
		}
	}
	
	public void setStartPos(int col, int row, Map exploredMap) {
		setPosition(col,row);
		exploredMap.setAllExplored(false);
		for(int r=row-1; r <= row+1; r++) {
			for(int c=col-1; c<= col+1; c++)
				exploredMap.getCell(r, c).setExplored(true);
		}
	}
	
	public void setPosition(int col, int row) {
		int colDiff = col - pos.x;
		int rowDiff = row - pos.y;
		pos.setLocation(col, row);
		for (Sensor s : sensorList) {
			s.setPos(s.getCol()+ colDiff, s.getRow()+rowDiff);
		}
	}
	
	public Point getPosition() {
		return pos;
	}

	// Robot Sense method for simulator
	public void sense(Map exploredMap, Map map) {
		int obsBlock;
		int rowInc = 1, colInc = 1;
		exploredMap.draw(true);
		draw();
		for (Sensor sensor : sensorList) {
			// check if sensor detects any obstacle
			obsBlock = sensor.detect(map);
			//Assign the rowInc and colInc based on sensor Direction
			switch (sensor.getSensorDir()) {
			case UP: 
				rowInc = 1;
				colInc = 0;
				break;
				
			case LEFT:
				rowInc = 0;
				colInc = -1;
				break;
				
			case RIGHT:
				rowInc = 0;
				colInc = 1;
				break;
				
			case DOWN:
				rowInc = -1;
				colInc = 0;
				break;
			}
			
			//Discover each of the blocks infront of the sensor if possible
			for (int i = sensor.getMinRange(); i <= sensor.getMaxRange(); i++) {
				
				//Check if the block is valid otherwise exit (Edge of Map)
				if (exploredMap.checkValidCell(sensor.getRow()+ rowInc * i, sensor.getCol() + colInc * i)) {
					//Change the cell to explored first
					Cell cell = exploredMap.getCell(sensor.getRow() + rowInc * i, sensor.getCol() + colInc * i);
					exploredMap.getCell(sensor.getRow() + rowInc * i, sensor.getCol() + colInc * i).setExplored(true);
					if (i == obsBlock) {
						exploredMap.getCell(sensor.getRow() + rowInc * i, sensor.getCol() + colInc * i).setObstacle(true);
						
						//Virtual Wall Initialized
						for (int r = sensor.getRow() + rowInc * i - 1; r <= sensor.getRow() + rowInc * i + 1; r++)
							for (int c = sensor.getCol() + colInc * i - 1; c <= sensor.getCol() + colInc * i + 1; c++)
								if (exploredMap.checkValidCell(r, c))
									exploredMap.getCell(r, c).setVirtualWall(true);
						break;
					}
				}
				else
					break;
			}
		}
		exploredMap.draw(true);
		draw();
	}

	//Draw Method for Robot
	public void draw() {
		gc.setStroke(RobotConstants.ROBOT_OUTLINE);
		gc.setLineWidth(2);

		gc.setFill(RobotConstants.ROBOT_BODY);

		int col = pos.x - 1;
		int row = pos.y + 1;
		int dirCol = 0, dirRow = 0;

		gc.strokeOval(col * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
				(MapConstants.MAP_CELL_SZ - 1) * MapConstants.MAP_HEIGHT - row * MapConstants.MAP_CELL_SZ
						+ MapConstants.MAP_OFFSET / 2,
				3 * MapConstants.MAP_CELL_SZ, 3 * MapConstants.MAP_CELL_SZ);
		gc.fillOval(col * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
				(MapConstants.MAP_CELL_SZ - 1) * MapConstants.MAP_HEIGHT - row * MapConstants.MAP_CELL_SZ
						+ MapConstants.MAP_OFFSET / 2,
				3 * MapConstants.MAP_CELL_SZ, 3 * MapConstants.MAP_CELL_SZ);

		gc.setFill(RobotConstants.ROBOT_DIRECTION);
		switch (direction) {
		case UP:
			dirCol = pos.x;
			dirRow = pos.y + 1;
			break;
		case DOWN:
			dirCol = pos.x;
			dirRow = pos.y - 1;
			break;
		case LEFT:
			dirCol = pos.x - 1;
			dirRow = pos.y;
			break;
		case RIGHT:
			dirCol = pos.x + 1;
			dirRow = pos.y;
			break;
		}
		gc.fillOval(dirCol * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
				(MapConstants.MAP_CELL_SZ - 1) * MapConstants.MAP_HEIGHT - dirRow * MapConstants.MAP_CELL_SZ
						+ MapConstants.MAP_OFFSET / 2,
				MapConstants.MAP_CELL_SZ, MapConstants.MAP_CELL_SZ);

		gc.setFill(Color.BLACK);
		for (Sensor s : sensorList) {
			gc.fillText(s.getId(), s.getCol() * MapConstants.MAP_CELL_SZ + MapConstants.MAP_OFFSET / 2,
					(MapConstants.MAP_CELL_SZ) * MapConstants.MAP_HEIGHT - s.getRow() * MapConstants.MAP_CELL_SZ
							+ MapConstants.MAP_OFFSET / 2);
		}

	}
}
