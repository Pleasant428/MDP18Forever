package Map;

import java.awt.Point;
/**
 * @author Saklani Pankaj
 *
 */
public class Cell {
	// Position Variables
	private Point pos;
	
	// Exploration Booleans
	private boolean explored;		//If Cell is Explored
	private boolean obstacle;		//If Cell is Obstacle
	private boolean virtualWall;	//If Cell is virtualWall
	private boolean isWayPoint;		//If Cell is waypoint
	private boolean moveThru;		//If Cell is virtualWall
	private boolean path;			//If Cell is path
	
	public Cell(Point pos) {
		this.pos = pos;
		this.explored = false;
	}
	
	// Getters and Setters
	public boolean isExplored() {
		return explored;
	}
	public void setExplored(boolean explored) {
		this.explored = explored;
	}
	public boolean isObstacle() {
		return obstacle;
	}
	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;
	}
	public boolean isVirtualWall() {
		return virtualWall;
	}
	public void setVirtualWall(boolean virtualWall) {
		this.virtualWall = virtualWall;
	}
	public Point getPos() {
		return pos;
	}
	public void setPos(Point pos) {
		this.pos = pos;
	}
	public boolean isPath() {
		return path;
	}

	public void setPath(boolean path) {
		this.path = path;
	}

	public boolean isWayPoint() {
		return isWayPoint;
	}

	public boolean setWayPoint(boolean isWayPoint) {
		if(!obstacle && explored && !virtualWall) {
			this.isWayPoint = isWayPoint;
			return true;
		}
		return false;
	}

	//Method to check if robot can move to this cell
	public boolean movableCell() {
		return explored && !obstacle && !virtualWall;
	}

	@Override
	public String toString() {
		return "Cell [pos=" + pos + ", explored=" + explored + ", obstacle=" + obstacle + ", virtualWall=" + virtualWall
				+ ", isWayPoint=" + isWayPoint + ", moveThru=" + moveThru + ", path=" + path + "]";
	}

	public boolean isMoveThru() {
		return moveThru;
	}

	public void setMoveThru(boolean moveThru) {
		this.moveThru = moveThru;
	}
	
}
