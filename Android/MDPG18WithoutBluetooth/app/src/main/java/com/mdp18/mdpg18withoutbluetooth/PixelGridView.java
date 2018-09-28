package com.mdp18.mdpg18withoutbluetooth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.lang.Math;

public class PixelGridView extends View {
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private int frontStartPos, backStartPos, leftStartPos, rightStartPos;
    private int frontCurPos, backCurPos, leftCurPos, rightCurPos;
    private int robotDirection;
    private Paint blackPaint = new Paint();
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

    public void setCurPos(int[] pos){
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

        this.setStartEndPointColor(canvas, greenPaint, redPaint);
        this.robotPosMapping(canvas, pos, robotDirection);

    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            int column = (int)(event.getX() / cellWidth);
//            int row = (int)(event.getY() / cellHeight);
//
//            cellChecked[column][row] = !cellChecked[column][row];
//            invalidate();
//        }
//
//        return true;
//    }

    public void setStartEndPointColor(Canvas canvas, Paint colorStart, Paint colorEnd){
        for (int i = 0; i < 3; i++){
            for (int j = 17; j < 20; j++){
                canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        colorStart);
            }
        }

        for (int i = 12; i < 15; i++){
            for (int j = 0; j < 3; j++){
                canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        colorEnd);
            }
        }
    }

    public void robotPosMapping(Canvas canvas, int[] pos, int robotDirection){

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

    public void moveForward(){
        int[] pos = this.getCurPos();
        int dir = this.getRobotDirection();

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
}