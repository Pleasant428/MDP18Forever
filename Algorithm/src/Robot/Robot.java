/**
 * 
 */
package Robot;

import java.awt.Point;
import java.util.ArrayList;

import Map.*;
import Network.NetMgr;
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
	private Command prevMove;
	private int senseCount = 0;
	private boolean rightDistAlign = false;	
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

	public boolean isSim() {
		return sim;
	}

	public void setSim(boolean sim) {
		this.sim = sim;
	}

	public Robot(boolean sim, Direction dir, int row, int col) {
		this.setSim(sim);
		this.direction = dir;
		this.reachedGoal = false;
		this.pos = new Point(col, row);
		sensorList = new ArrayList<Sensor>();
		
		// Initializing the Sensors
		/* ID information for sensors:
		 * 
		 * SF3/SL1 SF2 	SF1/SR2
		 *  X 		X	 X
		 * 	X		X	SR1
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
		Sensor SF1 = new Sensor("SF1",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col+1, Direction.UP);
		Sensor SF2 = new Sensor("SF2",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col,  Direction.UP);
		Sensor SF3 = new Sensor("SF3",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col-1,  Direction.UP);
		
		//RIGHT Sensor Next Direction of Direction
		Sensor SR1 = new Sensor("SR1",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row-1, col+1,  Direction.RIGHT);
		Sensor SR2 = new Sensor("SR2",RobotConstants.SHORT_MIN, RobotConstants.SHORT_MAX, row+1, col+1, Direction.RIGHT);
		
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
		default:
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
			for(int i=0; i<steps; i++) {
				exploredMap.passThru(pos.y-rowInc*i, pos.x-colInc*i);
			}
		}
	}
	
	//Moving using the Command enum
	public void move(Command m, int steps, Map exploredMap) {
		//System.out.println(m+" steps:"+steps);
		if(!sim) {
			//System.out.println("Alg|Ard|"+m+"|"+steps);
			NetMgr.getInstance().send("Alg|And|"+m.ordinal()+"|"+steps+"|");
			NetMgr.getInstance().send("Alg|Ard|"+m.ordinal()+"|"+steps);
		}
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
			default:
				System.out.println("Invalid Move Received");
				break;
		}
		prevMove = m;
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
		double obsBlock;
		double [][] sensorData = new double[6][2];
		int rowInc = 1, colInc = 1;
		exploredMap.draw(true);
		draw();
		String msg = null;
		if(!sim) {
			msg = NetMgr.getInstance().receive();
			String [] msgArr = msg.split("\\|");
			String [] strSensor = msgArr[3].split("\\,");
			System.out.println("Recieved "+strSensor.length+" sensor data");
			
			//Translate string to integer
			for(int i=0; i< strSensor.length; i++) {
				String [] arrSensorStr = strSensor[i].split("\\:");
				sensorData[i][0] = Double.parseDouble(arrSensorStr[1]);
				sensorData[i][1] = Double.parseDouble(arrSensorStr[2]);
			}
		}
		
		for (int i = 0; i < sensorList.size(); i++) {
			// check if sensor detects any obstacle
			if (!sim) {
				obsBlock = sensorData[i][1];
			}
			else
				obsBlock = sensorList.get(i).detect(map);
			// Assign the rowInc and colInc based on sensor Direction
			switch (sensorList.get(i).getSensorDir()) {
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
			if(!sim) {
				int existingObsBlock = -1;
				// Check Map for existing obstacle location
				for (int j = sensorList.get(i).getMinRange(); j <= sensorList.get(i).getMaxRange(); j++) {
					if (exploredMap.checkValidCell(sensorList.get(i).getRow() + rowInc * j, sensorList.get(i).getCol() + colInc * j) && !exploredMap.getCell(sensorList.get(i).getRow() + rowInc * j, sensorList.get(i).getCol() + colInc * j).isExplored()) {
						existingObsBlock = 0;
						if(exploredMap.getCell(sensorList.get(i).getRow() + rowInc * j, sensorList.get(i).getCol() + colInc * j).isObstacle())
							existingObsBlock = j;
							break;
					}
					else
						break;
				}
				
				System.out.println(sensorList.get(i).getId()+" exisiting:"+existingObsBlock+" obsBlock:"+obsBlock);
				//Discrepancy between explored map and sensor reading request sensor reading again
				if(existingObsBlock != -1 && existingObsBlock != obsBlock && obsBlock != 9 && existingObsBlock != 0)
				{
					System.out.println("Possible Phantom block conflict with existing block---------------------------------");
					System.out.println("SenseCount: "+senseCount);
					//Second Reading reset existing block
					if(senseCount>1) {
						senseCount = 0;
						System.out.println("Discarding existing block");
						exploredMap.getCell(sensorList.get(i).getRow() + rowInc * existingObsBlock, sensorList.get(i).getCol() + colInc * existingObsBlock).setObstacle(false);
					}
					else {
						System.out.println("Error Possible Phantom Block Detected! Resensing");
						NetMgr.getInstance().send("Alg|Ard|S|0");
						senseCount++;
						sense(exploredMap, map);
						return;
					}
				}
				sensorList.get(i).setPrevData(obsBlock);
				sensorList.get(i).setPrevRawData(sensorData[i][0]);
			}
			// Discover each of the blocks infront of the sensor if possible
			for (int j = sensorList.get(i).getMinRange(); j <= sensorList.get(i).getMaxRange(); j++) {

				// Check if the block is valid otherwise exit (Edge of Map)
				if (exploredMap.checkValidCell(sensorList.get(i).getRow() + rowInc * j, sensorList.get(i).getCol() + colInc * j)) {
					// Change the cell to explored first
					exploredMap.getCell(sensorList.get(i).getRow() + rowInc * j, sensorList.get(i).getCol() + colInc * j).setExplored(true);
					if (j == obsBlock && !exploredMap.getCell(sensorList.get(i).getRow() + rowInc * j, sensorList.get(i).getCol() + colInc * j).isMoveThru()) {
						exploredMap.getCell(sensorList.get(i).getRow() + rowInc * j, sensorList.get(i).getCol() + colInc * j).setExplored(true);
						exploredMap.getCell(sensorList.get(i).getRow() + rowInc * j,
								sensorList.get(i).getCol() + colInc * j).setObstacle(true);

						// Virtual Wall Initialized
						for (int r = sensorList.get(i).getRow() + rowInc * j - 1; r <= sensorList.get(i).getRow()
								+ rowInc * j + 1; r++)
							for (int c = sensorList.get(i).getCol() + colInc * j - 1; c <= sensorList.get(i).getCol()
									+ colInc * j + 1; c++)
								if (exploredMap.checkValidCell(r, c))
									exploredMap.getCell(r, c).setVirtualWall(true);

						break;
					}
					else if(exploredMap.getCell(sensorList.get(i).getRow() + rowInc * j, sensorList.get(i).getCol() + colInc * j).isObstacle()) {
						exploredMap.getCell(sensorList.get(i).getRow() + rowInc * j, sensorList.get(i).getCol() + colInc * j).setExplored(true);
						exploredMap.getCell(sensorList.get(i).getRow() + rowInc * j, sensorList.get(i).getCol() + colInc * j).setObstacle(false);
						// Set Virtual Wall off
						for (int r = sensorList.get(i).getRow() + rowInc * j - 1; r <= sensorList.get(i).getRow()
								+ rowInc * j + 1; r++)
							for (int c = sensorList.get(i).getCol() + colInc * j - 1; c <= sensorList.get(i).getCol()
									+ colInc * j + 1; c++)
								if (exploredMap.checkValidCell(r, c))
									exploredMap.getCell(r, c).setVirtualWall(false);
						
						exploredMap.reinitVirtualWall();
					}
				} else
					break;
			}
		}
		if(!sim) {
			
			//Check for Zeros
//			for(int i=0; i<sensorData.length; i++) {
//				if(sensorData[i][1]==0)
//				{
//					System.out.println("Zero detected");
//					senseCount++;
//					if(senseCount>0) {
//						NetMgr.getInstance().send("Alg|Ard|S|0");
//						sense(exploredMap, map);
//						return;
//					}	
//					else {
//						senseCount =0;
//						break;
//					}
//						
//				}
//			}
//			System.out.println("No Zeros Detected!");
			
			//Check Right Alignment
			if(Math.abs(sensorData[3][0] - sensorData[4][0])> RobotConstants.RIGHT_THRES && sensorData[3][1] <=1 && sensorData[4][1] <=1){
				System.out.println("Right Alignment------------------------");
				NetMgr.getInstance().send("Alg|And|"+Command.ALIGN_RIGHT.ordinal()+"|0");
				NetMgr.getInstance().send("Alg|Ard|"+Command.ALIGN_RIGHT.ordinal()+"|0");
				sense(exploredMap, map);
				return;
			}
			
			//Check Right distance
//			double prevRightAvg = (sensorList.get(3).getPrevRawData() + sensorList.get(4).getPrevRawData())/2;
//			double curRightAvg = (sensorData[3][0] + sensorData[3][0])/2;
//			System.out.println("cur: "+curRightAvg+" prev:"+prevRightAvg);
			// if too close/ far from right wall
			//if(!rightDistAlign && Math.abs(curRightAvg-prevRightAvg) >= RobotConstants.RIGHT_DIS_THRES && sensorData[3][1]==1 && sensorData[4][1]==1){
			boolean distAlign = true;
			for(int i=3;i<5;i++)
				if((sensorData[i][0] < RobotConstants.RIGHT_DIS_THRES_CLOSE || sensorData[i][0] > RobotConstants.RIGHT_DIS_THRES_FAR)&& sensorData[i][1] == 1)
					distAlign = false;
			
			System.out.println("distAlign "+!distAlign);
			System.out.println("rightDistAlign "+!rightDistAlign);
			System.out.println("R1 or R2 "+(sensorData[3][1]==1 || sensorData[4][1]==1));
			if(!rightDistAlign && !distAlign && (sensorData[3][1]==1 || sensorData[4][1]==1)) {	
				System.out.println("Right Distance Alignment-------------------------------");
				NetMgr.getInstance().send("Alg|And|"+Command.ALIGN_FRONT+"|1");
				NetMgr.getInstance().send("Alg|Ard|"+Command.TURN_RIGHT.ordinal()+"|1");
				msg = NetMgr.getInstance().receive();
				NetMgr.getInstance().send("Alg|Ard|"+Command.ALIGN_FRONT.ordinal()+"|1");
				msg = NetMgr.getInstance().receive();
				NetMgr.getInstance().send("Alg|Ard|"+Command.TURN_LEFT.ordinal()+"|1");
				
				if(sensorData[3][1]==1 && sensorData[4][1]==1)
				{
					msg = NetMgr.getInstance().receive();
					NetMgr.getInstance().send("Alg|Ard|"+Command.ALIGN_RIGHT.ordinal()+"|1");
				}
				rightDistAlign = true;
				sense(exploredMap, map);
				
				return;
			}
			rightDistAlign = false;
			//Checking Front Alignment too close/far from location
			System.out.println("prevMove :"+prevMove);
			if(prevMove == Command.FORWARD|| prevMove == Command.BACKWARD)
			{
				
				for(int i=0; i<3; i++) {
					System.out.println("Front "+i+": "+sensorData[i][1]+" PrevData: "+sensorList.get(i).getPrevData()+" SensorDiff: "+Math.abs(sensorData[i][1] - sensorList.get(i).getPrevData()));
					if(sensorList.get(i).getPrevData()<9 && Math.abs(sensorData[i][1] - sensorList.get(i).getPrevData())!=1)
					{
						System.out.println("Initial Front Cal Condition Passed!");
						senseCount++;
						if(senseCount<2) {
							NetMgr.getInstance().send("Alg|Ard|S|0");
							sense(exploredMap, map);
							return;
						}
						else
						{
							senseCount = 0; 
							break;
						}
					}
				}
				boolean cal = false;
				for(int i=0; i<3; i++) {
					if(sensorData[i][1]==1 && (sensorData[i][0] < RobotConstants.RIGHT_DIS_THRES_CLOSE || sensorData[i][0] > RobotConstants.RIGHT_DIS_THRES_FAR))
					{
						cal = true;
						break;
					}
					
				}
				
				//Discrepancy detected among the sensor data recieved
				if(cal) {
					NetMgr.getInstance().send("Alg|Ard|"+Command.ALIGN_FRONT.ordinal()+"|1");
					NetMgr.getInstance().send("Alg|And|"+Command.ALIGN_FRONT.ordinal()+"|1");
					NetMgr.getInstance().receive();
				}
					 
			}
			sendMapDescriptor(exploredMap);
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
	
	public void sendMapDescriptor(Map exploredMap) {
		String data = MapDescriptor.generateMDFStringPart1(exploredMap);
		NetMgr.getInstance().send("Alg|And|MD1|"+data+"|");
		data = MapDescriptor.generateMDFStringPart2(exploredMap);
		System.out.println(data);
		NetMgr.getInstance().send("Alg|And|MD2|"+data+"|");
	}
}
