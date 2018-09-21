/**
 * 
 */
package Robot;

import java.awt.Point;
import java.util.ArrayList;

import Map.*;
import Robot.RobotConstants.Direction;

/**
 * @author Saklani Pankaj
 *
 */
public class Robot {

	private ArrayList<Sensor> sensorList;
	private boolean sim;
	private Direction direction;
	
	
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	private Point pos;

	public Robot(boolean sim, Direction dir, int row, int col) {
		this.sim = sim;
		this.direction = dir;
		this.pos = new Point(col, row);
		sensorList = new ArrayList<Sensor>();
		
		// Initializing the Sensors
		/* ID information for sensors:
		 * 
		 * SF1/SL1 SF2 	SF3
		 *  X 		X	LR1
		 * SL2		X	 X
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
		Sensor SF1 = new Sensor("SF1",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col-1, dir);
		Sensor LF2 = new Sensor("LF2",RobotConstants.LONG_MIN, RobotConstants.LONG_MAX, row+1, col, dir);
		Sensor SF3 = new Sensor("SF3",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col+1, dir);
		
		//Left Sensor Next Direction of Direction
		Sensor SL1 = new Sensor("SF1",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col-1, dir);
		Sensor SL2 = new Sensor("LF2",RobotConstants.LONG_MIN, RobotConstants.LONG_MAX, row-1, col-1, dir);
		
		//Right Sensor Prev Direction of Robot Direction
		Sensor LR1 = new Sensor("SF1",RobotConstants.LONG_MIN, RobotConstants.LONG_MAX, row, col+1, dir);
		
		sensorList.add(SF1);
		sensorList.add(LF2);
		sensorList.add(SF3);
		sensorList.add(SL1);
		sensorList.add(SL2);
		sensorList.add(LR1);
		
		rotateSensors(dir);
	
	}
	
	public Sensor getSensor(String id) {
		for(Sensor s: sensorList)
			if(s.getId().equals(id))
				return s;
		
		return null;
	}
	
	public void rotateSensors(Direction dir) {
		double angle = 0;
		int newRow, newCol;
		switch(dir) {
		case UP:
			angle = 0;
			break;
		case LEFT:
			angle = -Math.PI/2;
			break;
		case DOWN:
			angle = Math.PI;
			break;
		case RIGHT:
			angle = Math.PI/2;
			break;
		}
		
		//Rotation Formula used: x = cos(a) * (x1 - x0) - sin(a) * (y1 - y0) + x0
		//						 y = sin(a) * (x1 - x0) + cos(a) * (y1 - y0) + y0
		for(Sensor s: sensorList) {
			s.setSensorDir(dir);
			newCol = (int)(Math.cos(angle)*(s.getCol() - pos.x) - Math.sin(angle)*(s.getRow() - pos.y) + pos.x);
			newRow = (int)(Math.sin(angle)*(s.getCol() - pos.x) - Math.cos(angle)*(s.getRow() - pos.y) + pos.x);
			s.setPos(newCol, newRow);
		}
	}
	
	//Movement Method for robot and Sensors
	public void move(Direction dir, boolean forward, int steps) {
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
		
		for (Sensor s : sensorList) {
			s.setPos(s.getCol()+ colInc*steps, s.getRow()+rowInc*steps);
		}
		System.out.println("x:"+pos.x+" y:"+pos.y);
		System.out.println("col:"+colInc+" row:"+rowInc);
		setPosition(pos.x+ colInc*steps, pos.y+rowInc*steps);
		System.out.println("x:"+pos.x+" y:"+pos.y);
		
	}
	

	public void setPosition(int col, int row) {
		pos.setLocation(col, row);
	}
	
	public Point getPosition() {
		return pos;
	}

	// Robot Sense method for simulator
	public void sense(Map exploredMap, Map map) {
		int obsBlock;
		int rowInc = 1, colInc = 1;

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
				if (exploredMap.checkValidCell(sensor.getRow() + i, sensor.getCol())) {
					//Change the cell to explored first
					exploredMap.getCell(sensor.getRow() + rowInc * i, sensor.getCol() + colInc * i).setExplored(true);
					if (i == obsBlock) {
						exploredMap.getCell(sensor.getRow() + rowInc * i, sensor.getCol() + colInc * i)
								.setObstacle(true);
						break;
					}
				}
				else
					break;
			}
		}

	}
}
