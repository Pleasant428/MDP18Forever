package Map;
import java.awt.Point;
/**
 * 
 */

/**
 * @author Saklani Pankaj
 *
 */

public class Map {
	
	private final Cell[][] grid;
	
	//KIV add Robot once Created
	public Map()
	{
		grid = new Cell[MapConstants.MAP_HEIGHT][MapConstants.MAP_WIDTH];
		
		//Init Cells on the grid;
		for(int row=0; row < MapConstants.MAP_HEIGHT; row++) {
			for(int col=0; col < MapConstants.MAP_WIDTH; col++) {
				grid[row][col] = new Cell(new Point(col,row));
				
				//Init Virtual wall
				if(row==0||col==0||row==MapConstants.MAP_HEIGHT-1||col==MapConstants.MAP_WIDTH-1) {
					grid[row][col].setVirtualWall(true);
				}
				
			}
		}
		
	}
	
	//Returns the Cell
	public Cell getCell(int row, int col) {
		return grid[row][col];
	}

	// Returns the Cell based on Point
	public Cell getCell(Point pos) {
		return grid[pos.y][pos.x];
	}
	
	//Check if the row and col is within the map boundary
	public boolean checkValidCell(int row, int col) {
		return row>0&&col>0&&row<MapConstants.MAP_HEIGHT&&col<MapConstants.MAP_HEIGHT;
	}
	
	
	//Reset Map
	public void resetMap() {
		for(int row = 0; row < MapConstants.MAP_HEIGHT; row++) {
			for(int col = 0; col < MapConstants.MAP_HEIGHT; col++) {
				grid[row][col].setExplored(false);
			}
		}
	}
	
	//Returns the Percentage Explored
	public double exploredPercentage() {
		double total = MapConstants.MAP_HEIGHT*MapConstants.MAP_WIDTH;
		double explored = 0;
		
		for (int row = 0; row < MapConstants.MAP_HEIGHT; row++) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				if(grid[row][col].isExplored())
					explored++;
			}
		}
		return explored/total;
	}
}
