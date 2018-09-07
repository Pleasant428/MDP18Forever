package Map;

import java.awt.Point;
/**
 * 
 */

/**
 * @author Saklani Pankaj
 *
 */

public class Cell {
	// Position Variables
	private Point pos;

	// Exploration Booleans
	private boolean explored;
	private boolean obstacle;
	private boolean virtualWall;
	
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
}
