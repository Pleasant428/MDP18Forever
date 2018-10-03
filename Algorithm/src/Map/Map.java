package Map;

import java.awt.Point;
import java.util.ArrayList;
/**
 * 
 */

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * @author Saklani Pankaj
 *
 */

public class Map {

	private final Cell[][] grid;
	private GraphicsContext gc;
	private Point wayPoint;

	public Point getWayPoint() {
		return wayPoint;
	}

	public void setWayPoint(Point wayPoint) {
		this.wayPoint = wayPoint;
	}

	public GraphicsContext getGc() {
		return gc;
	}

	public void setGc(GraphicsContext gc) {
		this.gc = gc;
	}

	// KIV add Robot once Created
	public Map() {
		grid = new Cell[MapConstants.MAP_HEIGHT][MapConstants.MAP_WIDTH];

		initMap();
	}
	
	
	private void initMap() {
		// Init Cells on the grid;
		for (int row = 0; row < MapConstants.MAP_HEIGHT; row++) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				grid[row][col] = new Cell(new Point(col, row));

				// Init Virtual wall
				if (row == 0 || col == 0 || row == MapConstants.MAP_HEIGHT - 1 || col == MapConstants.MAP_WIDTH - 1) {
					grid[row][col].setVirtualWall(true);
				}

			}
		}
	}
	
	//Set all cells as explored based on boolean true is all explord false is all unexplored
	public void setAllExplored(boolean explored) {
		for (int row = 0; row < MapConstants.MAP_HEIGHT; row++) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				grid[row][col].setExplored(explored);
			}
		}
	}

	// Returns the Cell
	public Cell getCell(int row, int col) {
		return grid[row][col];
	}

	// Returns the Cell based on Point
	public Cell getCell(Point pos) {
		return grid[pos.y][pos.x];
	}
	
	// Returns the nearest unexplored cell to the loc
	public Cell nearestUnexp(Point loc) {
		Cell cell, nearest = null;
		double distance = 1000;
		
		//Check for nearest unexplored 
		for (int row = 0; row < MapConstants.MAP_HEIGHT; row++) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				cell = grid[row][col];
				if(!cell.isExplored() && distance > loc.distance(cell.getPos()))
				{
					nearest = cell;
					distance = loc.distance(cell.getPos());
				}
			}
		}
		return nearest;
	}
	
	//Returns the nearest explored cell to the loc
	public Cell nearestExp(Point loc, Point botLoc) {
		Cell cell, nearest = null;
		double distance = 1000;
		
		//Check for nearest unexplored 
		for (int row = 0; row < MapConstants.MAP_HEIGHT; row++) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				cell = grid[row][col];
				if(checkValidMove(row,col))
				{
					if((distance > loc.distance(cell.getPos())) || 
							(distance == loc.distance(cell.getPos()) && cell.getPos().distance(botLoc) < nearest.getPos().distance(botLoc))){
						nearest = cell;
						distance = loc.distance(cell.getPos());
					}
				}
			}
		}
		return nearest;
	}

	// Check if the row and col is within the map boundary
	public boolean checkValidCell(int row, int col) {
		return row >= 0 && col >= 0 && row < MapConstants.MAP_HEIGHT && col < MapConstants.MAP_WIDTH;
	}

	// Check if valid to move there cannot move to virtual wall
	public boolean checkValidMove(int row, int col) {
		return checkValidCell(row, col) && !getCell(row, col).isVirtualWall() && !getCell(row, col).isObstacle() && getCell(row,col).isExplored();
	}

	// Reset Map
	public void resetMap() {
		initMap();
	}

	// Returns the Percentage Explored
	public double exploredPercentage() {
		double total = MapConstants.MAP_HEIGHT * MapConstants.MAP_WIDTH;
		double explored = 0;

		for (int row = 0; row < MapConstants.MAP_HEIGHT; row++) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				if (grid[row][col].isExplored())
					explored++;
			}
		}
		return explored / total * 100;
	}

	public ArrayList<Cell> getNeighbours(Cell c) {
		ArrayList<Cell> neighbours = new ArrayList<Cell>();

		// UP
		if (checkValidMove(c.getPos().y + 1, c.getPos().x)) {
			neighbours.add(getCell(c.getPos().y + 1, c.getPos().x));
		}
		// DOWN
		if (checkValidMove( c.getPos().y - 1, c.getPos().x)) {
			neighbours.add(getCell(c.getPos().y - 1, c.getPos().x));
		}

		// RIGHT
		if (checkValidMove(c.getPos().y, c.getPos().x + 1)) {
			neighbours.add(getCell(c.getPos().y, c.getPos().x + 1));
		}

		// LEFT
		if (checkValidMove( c.getPos().y, c.getPos().x - 1)) {
			neighbours.add(getCell(c.getPos().y, c.getPos().x - 1));
		}

		return neighbours;
	}

	// Draw the Map Graphics Cells
	public void draw(boolean explored) {
		// Basic Init for the Cells
		gc.setStroke(MapConstants.CW_COLOR);
		gc.setLineWidth(2);

		// Draw the Cells on the Map Canvas
		for (int row = 0; row < MapConstants.MAP_HEIGHT; row++) {

			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				// Select Color of the Cells
				if(grid[row][col].isPath())
					gc.setFill(MapConstants.PH_COLOR);
				else if (row <= MapConstants.STARTZONE_ROW + 1 && col <= MapConstants.STARTZONE_COL + 1)
					gc.setFill(MapConstants.SZ_COLOR);
				else if (row >= MapConstants.GOALZONE_ROW - 1 && col >= MapConstants.GOALZONE_COL - 1)
					gc.setFill(MapConstants.GZ_COLOR);
				else {
					if (explored) {
						if (grid[row][col].isObstacle())
							gc.setFill(MapConstants.OB_COLOR);
						else if (grid[row][col].isExplored())
							gc.setFill(MapConstants.EX_COLOR);
						else
							gc.setFill(MapConstants.UE_COLOR);
					} else {
						if (grid[row][col].isObstacle())
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
}
