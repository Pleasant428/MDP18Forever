package com.mdp18.group18android2018;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class PixelGridView extends View{

    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private int frontStartPos, backStartPos, leftStartPos, rightStartPos;
    private int frontCurPos, backCurPos, leftCurPos, rightCurPos;
    private int robotDirection;
    private ArrayList<int[]> wayPoints = new ArrayList<int[]>();
    private boolean selectStartPosition = false, selectWayPoint = false;
    private Paint blackPaint = new Paint();
    private Paint cyanPaint = new Paint();
    private Paint redPaint = new Paint();
    private Paint greenPaint = new Paint();
    private Paint yellowPaint = new Paint();
    private Paint grayPaint = new Paint();
    private Paint whitePaint = new Paint();
    private Paint bluePaint = new Paint();
    private boolean[][] cellChecked;

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

    }

    public void initializeMap(){
        this.setNumColumns(15);
        this.setNumRows(20);
        this.setStartPos(17,0,19,2);
        this.setRobotDirection(0);
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
        calculateDimensions();
    }

    public int getNumRows() {
        return numRows;
    }

    public void setStartPos(int frontStartPos, int leftStartPos, int backStartPos, int rightStartPos){
        this.frontStartPos = frontStartPos;
        this.leftStartPos = leftStartPos;
        this.backStartPos = backStartPos;
        this.rightStartPos = rightStartPos;
        this.frontCurPos = frontStartPos;
        this.leftCurPos = leftStartPos;
        this.backCurPos = backStartPos;
        this.rightCurPos = rightStartPos;
    }

    public void setStartPos(int row, int column){
        int[] startEdges = convertRobotPosToEdge(row, column);
        this.setStartPos(startEdges[0], startEdges[1], startEdges[2], startEdges[3]);
    }

    public int[] getStartPos(){
        int[] startPos = new int[4];
        startPos[0] = this.frontStartPos;
        startPos[1] = this.leftStartPos;
        startPos[2] = this.backStartPos;
        startPos[3] = this.rightStartPos;
        return startPos;
    }

    public void setCurPos(int row, int column){
        int[] edges = convertTileToEdge(row, column);
        this.frontCurPos = edges[0];
        this.leftCurPos = edges[1];
        this.backCurPos = edges[2];
        this.rightCurPos = edges[3];

    }

    public void setCurPos(int[] pos) {
        this.frontCurPos = pos[0];
        this.leftCurPos = pos[1];
        this.backCurPos = pos[2];
        this.rightCurPos = pos[3];
    }

    public int[] getCurPos(){
        int[] pos = new int[4];
        pos[0] = this.frontCurPos;
        pos[1] = this.leftCurPos;
        pos[2] = this.backCurPos;
        pos[3] = this.rightCurPos;

        return pos;
    }

    public void setRobotDirection(int direction){
        this.robotDirection = direction;
    }

    public int getRobotDirection(){
        return this.robotDirection;
    }

    private ArrayList<int[]> getWayPoints() {
        return this.wayPoints;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    private void calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            return;
        }

        cellWidth = getWidth() / getNumColumns();
        cellHeight = cellWidth;

        cellChecked = new boolean[getNumColumns()][getNumRows()];

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.GRAY);
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

//        this.setStartEndPointColor(canvas, greenPaint, redPaint);
        this.setStartPointColor(canvas, greenPaint);
        this.setEndPointColor(canvas, 18, 13, redPaint);
        this.setWayPointColor(canvas, cyanPaint);
        this.robotPosMapping(canvas, pos, robotDirection);

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int column = (int) (event.getX() / cellWidth);
            int row = (int)(event.getY() / cellHeight);

            if(selectStartPosition){
                if(!checkStartPoint(row, column)){
                    selectStartPosition = false;
                    return false;
                }
                this.setStartPos(inverseRowCoord(row), column);
                selectStartPosition = false;
                invalidate();


            }

            else if (selectWayPoint){
                this.setWayPoint(inverseRowCoord(row), column);
                selectWayPoint = false;
                invalidate();
            }

//            cellChecked[column][row] = !cellChecked[column][row];
        }

        return true;
    }

    private void setWayPoint(int row, int column) {
        int[] wayPoint = new int[2];
        wayPoint[0] = row;
        wayPoint[1] = column;
        wayPoints.add(wayPoint);
    }

    private boolean checkStartPoint(int row, int column) {
        if (row < 1 || row >= 19 || column < 1 || column >= 14 ){
            return false;
        }
        return true;
    }
//
//    public void setStartEndPointColor(Canvas canvas, Paint colorStart, Paint colorEnd){
//        for (int i = 0; i < 3; i++){
//            for (int j = 17; j < 20; j++){
//                canvas.drawRect(i * cellWidth, j * cellHeight,
//                        (i + 1) * cellWidth, (j + 1) * cellHeight,
//                        colorStart);
//            }
//        }
//
//        for (int i = 12; i < 15; i++){
//            for (int j = 0; j < 3; j++){
//                canvas.drawRect(i * cellWidth, j * cellHeight,
//                        (i + 1) * cellWidth, (j + 1) * cellHeight,
//                        colorEnd);
//            }
//        }
//    }

    public void setStartPointColor(Canvas canvas, Paint colorStart){

        int[] startPointEdges = this.getStartPos();
        for (int i = startPointEdges[1]; i <= startPointEdges[3]; i++){
            for (int j = startPointEdges[0]; j <= startPointEdges[2]; j++){
                canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        colorStart);
            }
        }
    }
    public void setEndPointColor(Canvas canvas, int row, int column, Paint colorEnd){
        int[] endPointEdges = convertRobotPosToEdge(row, column);
        for (int i = endPointEdges[1]; i <= endPointEdges[3]; i++){
            for (int j = endPointEdges[0]; j <= endPointEdges[2]; j++){
                canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        colorEnd);
            }
        }
    }

    private void setWayPointColor(Canvas canvas, Paint wayPointPaint) {
        ArrayList<int[]> wayPoints = this.getWayPoints();
        ArrayList<int[]> wayPointsEdges = new ArrayList<int[]>();
        for (int i[] : wayPoints ){
            wayPointsEdges.add(convertTileToEdge(i[0],i[1]));
        }

        for (int i[] : wayPointsEdges){
            for (int j = i[1]; j < i[3]; j++){
                for (int k = i[0]; k < i[2]; k++){
                    canvas.drawRect(j * cellWidth, k * cellHeight,
                            (j + 1) * cellWidth, (k + 1) * cellHeight,
                            wayPointPaint);
                }

            }
        }

    }



    public void robotPosMapping(Canvas canvas, int[] pos, int robotDirection){
        boolean reachedWall = this.checkReachedWall(pos, robotDirection);
//        boolean reachedWall = false;
        if(!reachedWall){
            for (int i = Math.min(pos[1],pos[3]); i <= Math.max(pos[1],pos[3]); i++){
                for (int j = Math.min(pos[0],pos[2]); j <= Math.max(pos[0],pos[2]); j++){
                    canvas.drawRect(i * cellWidth, j * cellHeight,
                            (i + 1) * cellWidth, (j + 1) * cellHeight,
                            yellowPaint);
                }
            }


            // head color

            // front
            if (robotDirection == 0){
                canvas.drawRect((pos[1] + 1) * cellWidth, pos[0] * cellHeight,
                        (pos[3]) * cellWidth, (pos[2] - 1) * cellHeight,
                        bluePaint);
            }

            // left
            else if (robotDirection == 1){
                canvas.drawRect((pos[1]) * cellWidth, (pos[0] + 1) * cellHeight,
                        (pos[3] - 1) * cellWidth, (pos[2]) * cellHeight,
                        bluePaint);
            }

            // back
            else if (robotDirection == 2){
                canvas.drawRect((pos[1] + 1) * cellWidth, (pos[0] + 2) * cellHeight,
                        (pos[3]) * cellWidth, (pos[2] + 1) * cellHeight,
                        bluePaint);
            }

            // right
            else if (robotDirection == 3){
                canvas.drawRect((pos[1] + 2) * cellWidth, (pos[0] + 1) * cellHeight,
                        (pos[3] + 1) * cellWidth, (pos[2]) * cellHeight,
                        bluePaint);
            }
        }

    }

    private boolean checkReachedWall(int[] pos, int direction) {
        int[] boundaries = new int[4];
        boundaries[0] = -1;
        boundaries[1] = 0;
        boundaries[2] = 19;
        boundaries[3] = 14;

        if(pos[direction] == boundaries[direction]){
            return true;
        }

        return false;
    }

    public void moveForward(){
        int[] pos = this.getCurPos();
        int dir = this.getRobotDirection();
        boolean reachedWall = this.checkReachedWall(pos, dir);

        if (!reachedWall){
            if (dir == 0){
                pos[0]--;
                pos[2]--;
            }

            else if (dir == 1){
                pos[1]--;
                pos[3]--;
            }


            else if (dir == 2){
                pos[0]++;
                pos[2]++;
            }

            else if (dir == 3){
                pos[1]++;
                pos[3]++;
            }

            this.setCurPos(pos);

            this.invalidate();
        }

    }

    public void rotateLeft(){
        int dir = this.getRobotDirection();

        dir = (dir + 1) % 4;
        this.setRobotDirection(dir);
        this.invalidate();;
    }

    public void rotateRight(){
        int dir = this.getRobotDirection();

        dir = (dir + 3) % 4;
        this.setRobotDirection(dir);

        this.invalidate();

    }

    public void moveBackwards(){
        int[] pos = this.getCurPos();
        int dir = this.getRobotDirection();
        boolean reachedWall = this.checkReachedWall(pos, (dir + 2)%4);

        if(!reachedWall){
            if (dir == 0){
                pos[0]++;
                pos[2]++;
            }

            else if (dir == 1){
                pos[1]++;
                pos[3]++;
            }


            else if (dir == 2){
                pos[0]--;
                pos[2]--;
            }

            else if (dir == 3){
                pos[1]--;
                pos[3]--;
            }

            this.setCurPos(pos);

            this.invalidate();
        }

    }

    public void selectWayPoint() {
        Log.d(TAG, "Setting Waypoint...");
        selectWayPoint = true;

        // SET WAYPOINT CODE



        Log.d(TAG, "Waypoint set to;");

    }

    public void selectStartPoint() {
        Log.d(TAG, "Setting Start Point...");
        selectStartPosition = true;

        // SET START POINT CODE




        Log.d(TAG, "Start Point set to;");
    }

    // Keeps the updated map on app regardless of auto or manual mode
    public void updateMap(String[] mapInfo, boolean updateMap) {

        // UPDATE MAP CODE





        // If updateMap == true, refresh map
        if (updateMap) {
            invalidate();
        }
    }

    // To inverse row's coordinates
    public int inverseRowCoord (int rowNum) {

        return (19 - rowNum);
    }

    public int[] convertRobotPosToEdge (int row, int column){
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

    public int[] convertTileToEdge (int row, int column){
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
    public void refreshMap(){
        invalidate();
    }

}
