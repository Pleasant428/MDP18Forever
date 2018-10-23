package com.mdp18.group18android2018;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.math.BigInteger;

import static android.content.ContentValues.TAG;

public class PixelGridView extends View {

    // VARIABLE DECLARATIONS
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private int frontStartPos, backStartPos, leftStartPos, rightStartPos;
    private int frontCurPos, backCurPos, leftCurPos, rightCurPos;
    private int robotDirection;
    private int[] startCoord = new int[2];
    private ArrayList<int[]> arrowImageCoords = new ArrayList<>();
    private boolean autoUpdate;
    private int[] curCoord = new int[2];
    private int[] wayPoint;
    private boolean selectStartPosition = false, selectWayPoint = false;
    private Paint blackPaint = new Paint();
    private Paint cyanPaint = new Paint();
    private Paint redPaint = new Paint();
    private Paint greenPaint = new Paint();
    private Paint yellowPaint = new Paint();
    private Paint grayPaint = new Paint();
    private Paint whitePaint = new Paint();
    private Paint bluePaint = new Paint();
    private Paint lightGrayPaint = new Paint();
    private boolean[][] cellExplored;
    private boolean[][] obstacles;

    public PixelGridView(Context context) {
        this(context, null);
    }

    public PixelGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        redPaint.setColor(Color.RED);
        whitePaint.setColor(Color.WHITE);
        grayPaint.setColor(Color.GRAY);
        yellowPaint.setColor(Color.YELLOW);
        greenPaint.setColor(Color.GREEN);
        bluePaint.setColor(Color.BLUE);
        cyanPaint.setColor(Color.CYAN);
        lightGrayPaint.setColor(Color.LTGRAY);

    }

    // Initialise Map
    public void initializeMap() {
        this.setNumColumns(15);
        this.setNumRows(20);
        this.setAutoUpdate(true);
        this.obstacles = new boolean[this.getNumColumns()][this.getNumRows()];
        this.cellExplored = new boolean[this.getNumColumns()][this.getNumRows()];

        this.setStartCoord(1,1);
        this.setStartPos(17, 0, 19, 2);
        this.setRobotDirection(0);

    }

    // Set obstacle
    public void setObstacle(int i, int j, boolean obstacle) {
        this.obstacles[i][j] = obstacle;
    }

    // Set cell as Explored
    public void setCellExplored(int column, int row, boolean explored){
            this.cellExplored[column][row] = explored;
    }

    // Get cell Explored
    public boolean[][] getCellExplored(){
        return this.cellExplored;
    }

    // Set number of columns
    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    // Get number of columns
    public int getNumColumns() {
        return numColumns;
    }

    // Set number of rows
    public void setNumRows(int numRows) {
        this.numRows = numRows;
        calculateDimensions();
    }

    // Get number of rows
    public int getNumRows() {
        return numRows;
    }

    // Set Start Position
    public void setStartPos(int frontStartPos, int leftStartPos, int backStartPos, int rightStartPos) {
        this.frontStartPos = frontStartPos;
        this.leftStartPos = leftStartPos;
        this.backStartPos = backStartPos;
        this.rightStartPos = rightStartPos;
        this.frontCurPos = frontStartPos;
        this.leftCurPos = leftStartPos;
        this.backCurPos = backStartPos;
        this.rightCurPos = rightStartPos;
    }

    // Set Start Point Coordinates
    public void setStartCoord(int row, int column) {
        this.startCoord[0] = column;
        this.startCoord[1] = row;
        this.curCoord = this.startCoord;
        for (int i = 0; i < this.getNumColumns(); i++) {
            for (int j = 0; j < this.getNumRows(); j++) {
                this.setObstacle(i, j, false);
                this.setCellExplored(i, j, false);
            }
        }

        for(int i = column - 1; i <= column + 1; i++){
            for(int j = row - 1; j <= row + 1; j++){
                this.setCellExplored(i, j, true);
            }
        }

    }

    // Get Start Point coordinates
    public int[] getStartCoord() {
        return this.startCoord;
    }

    // Get current position coordinates
    public int[] getCurCoord() {
        return this.curCoord;
    }

    // Move current position coordinates
    public void moveCurCoord(int xInc, int yInc) {
        this.setCurPos(this.getCurCoord()[1] + yInc, this.getCurCoord()[0] + xInc);
    }

    // Set Start Position
    public void setStartPos(int row, int column) {
        this.setStartCoord(row, column);
        int[] startEdges = convertRobotPosToEdge(row, column);
        this.setStartPos(startEdges[0], startEdges[1], startEdges[2], startEdges[3]);
        for (int i = 0; i < this.getNumColumns(); i++) {
            for (int j = 0; j < this.getNumRows(); j++) {
                this.setObstacle(i, j, false);
                this.setCellExplored(i, j, false);
            }
        }
    }

    // Get Start Position
    public int[] getStartPos() {
        int[] startPos = new int[4];
        startPos[0] = this.frontStartPos;
        startPos[1] = this.leftStartPos;
        startPos[2] = this.backStartPos;
        startPos[3] = this.rightStartPos;
        return startPos;
    }

    // Set current position (integers)
    public void setCurPos(int row, int column) {
        int[] edges = convertRobotPosToEdge(row, column);
        this.frontCurPos = edges[0];
        this.leftCurPos = edges[1];
        this.backCurPos = edges[2];
        this.rightCurPos = edges[3];
        this.curCoord[0] = column;
        this.curCoord[1] = row;

        this.refreshMap(this.getAutoUpdate());
    }

    // Set current position (integer array)
    public void setCurPos(int[] pos) {
        this.frontCurPos = pos[0];
        this.leftCurPos = pos[1];
        this.backCurPos = pos[2];
        this.rightCurPos = pos[3];

        this.refreshMap(this.getAutoUpdate());
    }

    // Get current position
    public int[] getCurPos() {
        int[] pos = new int[4];
        pos[0] = this.frontCurPos;
        pos[1] = this.leftCurPos;
        pos[2] = this.backCurPos;
        pos[3] = this.rightCurPos;

        return pos;
    }

    // Set Auto update
    public void setAutoUpdate(boolean auto){
        this.autoUpdate = auto;
    }

    // Get Auto update
    public boolean getAutoUpdate(){
        return this.autoUpdate;
    }

    // Set Robot Direction
    public void setRobotDirection(int direction) {
        this.robotDirection = direction;

        this.refreshMap(this.getAutoUpdate());
    }

    // Get Robot Direction
    public int getRobotDirection() {
        return this.robotDirection;
    }

    // Set Arrow coordinates
    public void setArrowImageCoord() {
        int[] arrowImageCoord = new int[3];
        int[] robotPos = this.getCurCoord();
        int robotColumn = robotPos[0];
        int robotRow = robotPos[1];
        int robotDir = this.getRobotDirection();

        // Camera will be on the bottom right of the robot.

        // If robot is facing up, arrow direction would be left.
        // Arrow coordinates will be col+2, row-1
        if (robotDir == 0) {
            arrowImageCoord[0] = robotColumn+2;
            arrowImageCoord[1] = robotRow-1;
        }

        // If robot is facing left, arrow direction would be down.
        // Arrow coordinates will be col+1, row+2
        else if (robotDir == 1) {
            arrowImageCoord[0] = robotColumn+1;
            arrowImageCoord[1] = robotRow+2;
        }

        // If robot is facing down, arrow direction would be right.
        // Arrow coordinates will be col-2, row+1
        else if (robotDir == 2) {
            arrowImageCoord[0] = robotColumn-2;
            arrowImageCoord[1] = robotRow+1;
        }

        // If robot is facing right, arrow direction would be up.
        // Arrow coordinates will be col-1, row-2
        else {
            arrowImageCoord[0] = robotColumn-1;
            arrowImageCoord[1] = robotRow-2;
        }
        arrowImageCoord[2] = robotDir;
        this.arrowImageCoords.add(arrowImageCoord);
        this.refreshMap(this.getAutoUpdate());
    }

    // Get Arrow coordinates
    public ArrayList<int[]> getArrowImageCoords(){
        return this.arrowImageCoords;
    }

    // Get Waypoint
    public int[] getWayPoint(){
        return this.wayPoint;
    }

    // Get obstacle
    public boolean[][] getObstacles () {
        return this.obstacles;
    }


    @Override
    protected void onSizeChanged ( int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    // Calculate dimensions
    private void calculateDimensions () {
        if (numColumns < 1 || numRows < 1) {
            return;
        }

        cellWidth = getWidth() / getNumColumns();
        cellHeight = cellWidth;
        cellExplored = new boolean[getNumColumns()][getNumRows()];

        invalidate();
    }

    @Override
    protected void onDraw (Canvas canvas){
        int pos[] = this.getCurPos();
        if (numColumns == 0 || numRows == 0) {
            return;
        }

        int width = cellWidth * getNumColumns();
        int height = cellHeight * getNumRows();

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        grayPaint);
            }
        }

        // vertical lines
        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, blackPaint);
        }

        // horizontal lines
        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, blackPaint);
        }

        this.setExploredMapping(canvas, lightGrayPaint);
        this.setStartPointColor(canvas, greenPaint);
        this.setEndPointColor(canvas, 18, 13, redPaint);
        if (this.wayPoint != null)
            this.setWayPointColor(canvas, cyanPaint);
        this.robotPosMapping(canvas, pos, robotDirection);
        this.obstacleMapping(canvas);
        this.arrowMapping(canvas);
    }


    @Override
    public boolean onTouchEvent (MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int column = (int) (event.getX() / cellWidth);
            int row = (int) (event.getY() / cellHeight);

            if (selectStartPosition) {
                if (!checkStartPoint(row, column)) {
                    selectStartPosition = false;
                    invalidate();
                    return true;
                }
                this.setStartPos(inverseRowCoord(row), column);
                selectStartPosition = false;
                // For checklist C5
//                String startCoord = "Start Point: ".concat(Integer.toString(this.getStartCoord()[0])).concat(",").concat(Integer.toString(this.getStartCoord()[1]));
//                Log.d(TAG, "Start Point: " + Integer.toString(this.getStartCoord()[0]) + "," + Integer.toString(this.getStartCoord()[1]));
//                byte[] bytes = startCoord.getBytes(Charset.defaultCharset());
//                BluetoothChat.writeMsg(bytes);
                invalidate();

            } else if (selectWayPoint) {
                this.setWayPoint(inverseRowCoord(row), column);
                selectWayPoint = false;
                // For checklist C5
//                String waypointCoordinate = "Waypoint: ".concat(Integer.toString(this.getWayPoints().get(0)[0])).concat(",").concat(Integer.toString(this.getWayPoints().get(0)[1]));
//                Log.d(TAG, "Waypoint: " + Integer.toString(this.getWayPoints().get(0)[0]).concat(",").concat(Integer.toString(this.getWayPoints().get(0)[1])));
//                String waypointCoordinate = "Waypoint: ".concat(Integer.toString(this.getWayPoint()[0])).concat(",").concat(Integer.toString(this.getWayPoint()[1]));
//                Log.d(TAG, "Waypoint: " + Integer.toString(this.getWayPoint()[0]).concat(",").concat(Integer.toString(this.getWayPoint()[1])));
//                byte[] bytes = waypointCoordinate.getBytes(Charset.defaultCharset());
//                BluetoothChat.writeMsg(bytes);
                invalidate();
            }
        }

        return true;
    }

    // Set Waypoint
    public void setWayPoint ( int row, int column){
        int[] wayPoint = new int[2];
        wayPoint[0] = column;
        wayPoint[1] = row;
        if (this.wayPoint == null) this.wayPoint = new int[2];
        this.wayPoint = wayPoint;
        String wayPointString = "Setting Waypoint to (" + String.valueOf(column) + "," + String.valueOf(row) + ")";
        Toast.makeText(this.getContext(), wayPointString, Toast.LENGTH_SHORT).show();

    }

    // Check Start Point
    private boolean checkStartPoint ( int row, int column){
        if (row < 1 || row >= 19 || column < 1 || column >= 14) {
            return false;
        }
        return true;
    }

    // Explored Map
    public void setExploredMapping (Canvas canvas, Paint exploredColor){
        boolean[][] exploredCell = this.getCellExplored();

        for(int i = 0; i < this.getNumColumns(); i++){
            for(int j = 0; j < this.getNumRows(); j++){
                if (exploredCell[i][j]){
                    canvas.drawRect(i * cellWidth, (19 - j) * cellHeight,
                            (i + 1) * cellWidth, (20 - j) * cellHeight,
                            exploredColor);
                }
            }
        }
        // vertical lines
        for (int i = 1; i < this.getNumColumns(); i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, (float) 0.92 * this.getHeight(), blackPaint);
        }

        // horizontal lines
        for (int i = 1; i < this.getNumRows(); i++) {
            canvas.drawLine(0, i * cellHeight, this.getWidth(), i * cellHeight, blackPaint);
        }
    }

    // Set Start Point Color
    public void setStartPointColor (Canvas canvas, Paint colorStart){

        int[] startPointEdges = this.getStartPos();
        for (int i = startPointEdges[1]; i <= startPointEdges[3]; i++) {
            for (int j = startPointEdges[0]; j <= startPointEdges[2]; j++) {
                canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        colorStart);
            }
        }
    }

    // Set End Point Color
    public void setEndPointColor (Canvas canvas,int row, int column, Paint colorEnd){
        int[] endPointEdges = convertRobotPosToEdge(row, column);
        for (int i = endPointEdges[1]; i <= endPointEdges[3]; i++) {
            for (int j = endPointEdges[0]; j <= endPointEdges[2]; j++) {
                canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        colorEnd);
            }
        }
    }

    // Set Waypoint Color
    private void setWayPointColor (Canvas canvas, Paint wayPointPaint){

        int[] wayPoint = this.getWayPoint();
        int[] wayPointEdges = this.convertTileToEdge(wayPoint[1], wayPoint[0]);

        for (int i = wayPointEdges[1]; i < wayPointEdges[3]; i++){
            for (int j = wayPointEdges[0]; j < wayPointEdges[2]; j++) {
                canvas.drawRect(i * cellWidth, j * cellHeight,
                            (i + 1) * cellWidth, (j + 1) * cellHeight,
                            wayPointPaint);
            }
        }

    }

    // Map Robot Position
    public void robotPosMapping (Canvas canvas,int[] pos, int robotDirection){

        for (int i = Math.min(pos[1], pos[3]); i <= Math.max(pos[1], pos[3]); i++) {
            for (int j = Math.min(pos[0], pos[2]); j <= Math.max(pos[0], pos[2]); j++) {
                canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        yellowPaint);
            }
        }


        // head color
        // front
        if (robotDirection == 0) {
            canvas.drawRect((pos[1] + 1) * cellWidth, pos[0] * cellHeight,
                    (pos[3]) * cellWidth, (pos[2] - 1) * cellHeight,
                    bluePaint);
        }

        // left
    else if (robotDirection == 1) {
            canvas.drawRect((pos[1]) * cellWidth, (pos[0] + 1) * cellHeight,
                    (pos[3] - 1) * cellWidth, (pos[2]) * cellHeight,
                    bluePaint);
        }

        // back
        else if (robotDirection == 2) {
            canvas.drawRect((pos[1] + 1) * cellWidth, (pos[0] + 2) * cellHeight,
                    (pos[3]) * cellWidth, (pos[2] + 1) * cellHeight,
                    bluePaint);
        }

        // right
        else if (robotDirection == 3) {
            canvas.drawRect((pos[1] + 2) * cellWidth, (pos[0] + 1) * cellHeight,
                    (pos[3] + 1) * cellWidth, (pos[2]) * cellHeight,
                    bluePaint);
        }
    }

    // Map obstacles
    public void obstacleMapping (Canvas canvas){
        boolean obstacle[][] = this.getObstacles();
        for (int i = 0; i < this.getNumColumns(); i++) {
            for (int j = 0; j < this.getNumRows(); j++) {
                if (obstacle[i][j]) {
                    canvas.drawRect(i * cellWidth, (19 - j) * cellHeight,
                            (i + 1) * cellWidth, (20 - j) * cellHeight,
                            blackPaint);
                }
            }
        }
    }

    // Map Arrows
    public void arrowMapping (Canvas canvas){
        ArrayList<int[]> arrowImageCoords = this.getArrowImageCoords();
        if(arrowImageCoords.size() == 0) return;
        int robotDir = this.getRobotDirection();
        RectF rect;
        for (int i = 0; i < arrowImageCoords.size(); i++){

            rect = new RectF(arrowImageCoords.get(i)[0] * cellWidth, (19 - arrowImageCoords.get(i)[1]) * cellHeight,
                    (arrowImageCoords.get(i)[0] + 1) * cellWidth, (20 - arrowImageCoords.get(i)[1]) * cellHeight);

            // Camera will be on the bottom right of the robot.

            // If robot is facing up, arrow direction would be left.
            // Arrow coordinates will be col+2, row-1
            if(arrowImageCoords.get(i)[2] == 0){
                Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_up_l);
                canvas.drawBitmap(arrowBitmap,null, rect, null);
            }

            // If robot is facing left, arrow direction would be down.
            // Arrow coordinates will be col+1, row+2
            else if(arrowImageCoords.get(i)[2] == 1){
                Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_up_d);
                canvas.drawBitmap(arrowBitmap,null, rect, null);
            }

            // If robot is facing down, arrow direction would be right.
            // Arrow coordinates will be col-2, row+1
            else if(arrowImageCoords.get(i)[2] == 2){
                Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_up_r);
                canvas.drawBitmap(arrowBitmap,null, rect, null);
            }

            // If robot is facing right, arrow direction would be up.
            // Arrow coordinates will be col-1, row-2
            else if(arrowImageCoords.get(i)[2] == 3){
                Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_up_u);
                canvas.drawBitmap(arrowBitmap,null, rect, null);
            }


        }
    }

    // Check if robot has reached wall
    public boolean checkReachedWall ( int[] pos, int direction){

            int[] boundaries = new int[4];
            boundaries[0] = 0;
            boundaries[1] = 0;
            boundaries[2] = 19;
            boundaries[3] = 14;

            if (pos[direction] == boundaries[direction]) {
                return true;
            }

            return false;
        }


    // Move forward
    public void moveForward () {
        int[] pos = this.getCurPos();
        int[] prev = this.getCurPos();

        int dir = this.getRobotDirection();
        boolean reachedWall = this.checkReachedWall(pos, dir);
        boolean reachedObstacle = this.checkReachedObstacle(pos, dir);


        if (!reachedWall && !reachedObstacle) {
            if (dir == 0) {
                pos[0]--;
                pos[2]--;
                this.moveCurCoord(0, 1);
            } else if (dir == 1) {
                pos[1]--;
                pos[3]--;
                this.moveCurCoord(-1, 0);

            } else if (dir == 2) {
                pos[0]++;
                pos[2]++;
                this.moveCurCoord(0, -1);

            } else if (dir == 3) {
                pos[1]++;
                pos[3]++;
                this.moveCurCoord(1, 0);
            }


        }
        this.setCurPos(pos);
        this.exploredTile();

        return;
    }

    // Turn Left
    public void rotateLeft () {
        int dir = this.getRobotDirection();

        dir = (dir + 1) % 4;
        this.setRobotDirection(dir);
    }

    // Turn Right
    public void rotateRight () {
        int dir = this.getRobotDirection();

        dir = (dir + 3) % 4;
        this.setRobotDirection(dir);
    }

    // Move backwards
    public void moveBackwards () {
        int[] pos = this.getCurPos();
        int dir = this.getRobotDirection();

        boolean reachedWall = this.checkReachedWall(pos, (dir + 2) % 4);
        boolean reachedObstacle = this.checkReachedObstacle(pos, (dir + 2) % 4);

        if (!reachedWall && !reachedObstacle) {
            if (dir == 0) {
                pos[0]++;
                pos[2]++;
                this.moveCurCoord(0, -1);
            } else if (dir == 1) {
                pos[1]++;
                pos[3]++;
                this.moveCurCoord(1, 0);
            } else if (dir == 2) {
                pos[0]--;
                pos[2]--;
                this.moveCurCoord(0, 1);
            } else if (dir == 3) {
                pos[1]--;
                pos[3]--;
                this.moveCurCoord(-1, 0);
            }


        }
        this.setCurPos(pos);
        this.exploredTile();
    }

    // Check if robot reached obstacle
    public boolean checkReachedObstacle ( int[] pos, int dir){
        boolean[][] obstacle = this.getObstacles();
        if (dir == 0) {
            if(pos[0] == 0) return true;
            if (obstacle[pos[1]][20 - pos[0]] || obstacle[pos[1] + 1][20 - pos[0]] || obstacle[pos[3]][20 - pos[0]]) {
                return true;
            }
        } else if (dir == 1) {
            if(pos[1] == 0) return true;
            if (obstacle[pos[1] - 1][17 - pos[0]] || obstacle[pos[1] - 1][18 - pos[0]] || obstacle[pos[1] - 1][19 - pos[0]]) {
                return true;
            }
        } else if (dir == 2) {
            if(pos[2] == 19) return true;
            if (obstacle[pos[1]][18 - pos[2]] || obstacle[pos[1] + 1][18 - pos[2]] || obstacle[pos[3]][18 - pos[2]]) {
                return true;
            }
        } else {
            if(pos[3] == 14) return true;
            if (obstacle[pos[3] + 1][17 - pos[0]] || obstacle[pos[3] + 1][18 - pos[0]] || obstacle[pos[3] + 1][19 - pos[0]]) {
                return true;
            }
        }

        return false;

    }

    // Select Waypoint
    public void selectWayPoint () {
        Log.d(TAG, "Setting Waypoint...");
        selectWayPoint = true;

    }

    // Select Start Point
    public void selectStartPoint () {
        Log.d(TAG, "Setting Start Point...");
        selectStartPosition = true;
    }


    // To inverse row's coordinates
    public int inverseRowCoord ( int rowNum){

        return (19 - rowNum);
    }

    // Convert Robot Position to Edge
    public int[] convertRobotPosToEdge ( int row, int column){
        int rowFormatConvert = inverseRowCoord(row);
        int topEdge, leftEdge, bottomEdge, rightEdge;
        topEdge = rowFormatConvert - 1;
        leftEdge = column - 1;
        bottomEdge = rowFormatConvert + 1;
        rightEdge = column + 1;
        int[] edges = new int[4];
        edges[0] = topEdge;
        edges[1] = leftEdge;
        edges[2] = bottomEdge;
        edges[3] = rightEdge;
        return edges;
    }

    // Convert tile to edge
    public int[] convertTileToEdge ( int row, int column){
        int rowFormatConvert = inverseRowCoord(row);
        int topEdge, leftEdge, bottomEdge, rightEdge;
        topEdge = rowFormatConvert;
        leftEdge = column;
        bottomEdge = rowFormatConvert + 1;
        rightEdge = column + 1;
        int[] edges = new int[4];
        edges[0] = topEdge;
        edges[1] = leftEdge;
        edges[2] = bottomEdge;
        edges[3] = rightEdge;
        return edges;
    }

    // Refresh map
    public void refreshMap (boolean updateMap) {
        if (updateMap == true) {
            invalidate();
        }
    }

    // Check whether the tile has been explored
    public void exploredTile (){
        for (int i = this.getCurCoord()[1] - 1; i <= this.getCurCoord()[1] + 1; i++){
            for (int j = this.getCurCoord()[0] - 1; j <= this.getCurCoord()[0] + 1; j++){
                this.setCellExplored(j, i, true);
            }
        }
        this.refreshMap(this.getAutoUpdate());
    }

    // Part 1 of Map Descriptor
    public void mapDescriptorExplored (String hexMap) {
        BigInteger hexBigInteger = new BigInteger(hexMap, 16);
        String binMap = hexBigInteger.toString(2);
        String binMapExtracted = binMap.substring(2,302);
        char cur;
        Integer[] binMapArray = new Integer[binMapExtracted.length()];
        for (int i = 0; i < binMapExtracted.length(); i++){
            cur = binMapExtracted.charAt(i);
            binMapArray[i] = Integer.parseInt(String.valueOf(cur));
        }

        int binMapArrayIndex = 0;
        for(int j = 0; j < this.getNumRows(); j++){
            for(int i = 0; i < this.getNumColumns(); i++) {
                if(binMapArray[binMapArrayIndex] == 1){
                    this.setCellExplored(i, j, true);
                }
                else
                    this.setCellExplored(i, j, false);
                binMapArrayIndex++;
            }
        }
        this.refreshMap(this.getAutoUpdate());
    }

    // Part 2 of Map Descriptor
    public void mapDescriptorObstacle(String hexMap){

        String hexMapTemp = "1".concat(hexMap);
        BigInteger hexBigInteger = new BigInteger(hexMapTemp, 16);
        String binMap = hexBigInteger.toString(2);
        char cur;
        int length = binMap.length();
        Integer[] binMapArray = new Integer[binMap.length() - 1];
        for (int i = 1; i < binMap.length(); i++){
            cur = binMap.charAt(i);
            binMapArray[i - 1] = Integer.parseInt(String.valueOf(cur));
        }
        boolean[][] exploredCell = this.getCellExplored();

        int binMapArrayIndex = 0;
        for(int j = 0; j < this.getNumRows(); j++){
            for(int i = 0; i < this.getNumColumns(); i++) {
                if (exploredCell[i][j]) {
                    if (binMapArray[binMapArrayIndex] == 1) {
                        this.setObstacle(i, j, true);
                    } else
                        this.setObstacle(i, j, false);
                    binMapArrayIndex++;
                }
            }
        }
        this.refreshMap(this.getAutoUpdate());
    }

    // Map Descriptor for Checklist only
    public void mapDescriptorChecklist(String hexMap){
        BigInteger hexBigInteger = new BigInteger(hexMap, 16);
        String binMap = hexBigInteger.toString(2);
        String binMapWithLeadingZeros = String.format("%300s", binMap).replace(" ", "0");
        char cur;
        Integer[] binMapArray = new Integer[binMapWithLeadingZeros.length()];
        for (int i = 0; i < binMapWithLeadingZeros.length(); i++){
            cur = binMapWithLeadingZeros.charAt(i);
            binMapArray[i] = Integer.parseInt(String.valueOf(cur));
        }

        int binMapArrayIndex = 0;
        for(int j = this.getNumRows() - 1; j >= 0; j--){
            for(int i = 0; i < this.getNumColumns(); i++){
                if(binMapArray[binMapArrayIndex] == 1){
                    this.setObstacle(i, j, true);
                }
                else
                    this.setObstacle(i, j, false);
                binMapArrayIndex++;
            }
        }
        this.refreshMap(this.getAutoUpdate());
    }
}