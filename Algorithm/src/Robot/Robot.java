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
	public ArrayList<Sensor> getSensorList() {
		return sensorList;
	}

	public void setSensorList(ArrayList<Sensor> sensorList) {
		this.sensorList = sensorList;
	}

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
		Sensor SF1 = new Sensor("SF1",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col-1, Direction.UP);
		Sensor SF2 = new Sensor("SF2",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col,  Direction.UP);
		Sensor SF3 = new Sensor("SF3",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col+1,  Direction.UP);
		
		//Left Sensor Next Direction of Direction
		Sensor SL1 = new Sensor("SL1",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col-1,  Direction.LEFT);
		Sensor SL2 = new Sensor("SL2",RobotConstants.LONG_MIN, RobotConstants.LONG_MAX, row-1, col-1, Direction.LEFT);
		
		//Right Sensor Prev Direction of Robot Direction
		Sensor LR1 = new Sensor("LR1",RobotConstants.LONG_MIN, RobotConstants.LONG_MAX, row, col+1,  Direction.RIGHT);
		
		sensorList.add(SF1);
		sensorList.add(SF2);
		sensorList.add(SF3);
		sensorList.add(SL1);
		sensorList.add(SL2);
		sensorList.add(LR1);
		
		System.out.println("Before");
		for (Sensor s : sensorList) {
			System.out.println(s.getId()+" col: "+s.getCol()+" row:"+s.getRow()+" Direction: "+s.getSensorDir().name());
		}
		
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
		
		System.out.println("After");
		for (Sensor s : sensorList) {
			System.out.println(s.getId()+" col: "+s.getCol()+" row:"+s.getRow()+" Direction: "+s.getSensorDir().name());
		}
	
	}
	
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
			System.out.println("Before "+s.getId()+"col: "+s.getCol()+" row:"+s.getRow()+" Direction: "+s.getSensorDir().name());
			if(left)
				s.setSensorDir(Direction.getNext(s.getSensorDir()));
			else
				s.setSensorDir(Direction.getPrevious(s.getSensorDir()));
			
			
			newCol = (int)Math.round((Math.cos(angle)*(s.getCol() - pos.x) - Math.sin(angle)*(s.getRow() - pos.y) + pos.x));
			newRow = (int)Math.round((Math.sin(angle)*(s.getCol() - pos.x) - Math.cos(angle)*(s.getRow() - pos.y) + pos.y));
			s.setPos(newCol, newRow);
			System.out.println("After "+s.getId()+"col: "+s.getCol()+" row:"+s.getRow()+" Direction: "+s.getSensorDir().name()+"\n");
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
		
		System.out.println("x:"+pos.x+" y:"+pos.y);
		System.out.println("col:"+colInc+" row:"+rowInc);
		setPosition(pos.x+ colInc*steps, pos.y+rowInc*steps);
		System.out.println("x:"+pos.x+" y:"+pos.y);
		
		for (Sensor s : sensorList) {
			s.setPos(s.getCol()+ colInc*steps, s.getRow()+rowInc*steps);
			System.out.println(s.getId()+" col: "+s.getCol()+" row:"+s.getRow()+" Direction: "+s.getSensorDir().name());
		}
		
		
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
			System.out.println(sensor.getId()+" col: "+sensor.getCol()+" row:"+sensor.getRow()+" Direction: "+sensor.getSensorDir().name());
			
			//Discover each of the blocks infront of the sensor if possible
			for (int i = sensor.getMinRange(); i <= sensor.getMaxRange(); i++) {
				//Check if the block is valid otherwise exit (Edge of Map)
				if (exploredMap.checkValidCell(+ rowInc * i, sensor.getCol() + colInc * i)) {
					//Change the cell to explored first
					Cell cell = exploredMap.getCell(sensor.getRow() + rowInc * i, sensor.getCol() + colInc * i);
					System.out.println("Exploring CELL ROW: "+cell.getPos().y+" COL: "+cell.getPos().x);
					exploredMap.getCell(sensor.getRow() + rowInc * i, sensor.getCol() + colInc * i).setExplored(true);
					if (i == obsBlock) {
						exploredMap.getCell(sensor.getRow() + rowInc * i, sensor.getCol() + colInc * i).setObstacle(true);
						break;
					}
				}
				else
					break;
			}
		}

	}
}
