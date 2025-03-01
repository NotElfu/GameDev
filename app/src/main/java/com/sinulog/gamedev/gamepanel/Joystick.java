package com.sinulog.gamedev.gamepanel;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.sinulog.gamedev.game.Utils;

public class Joystick {


    private final int outerCircleCenterPositionX;
    private final int outerCircleCenterPositionY;
    private int innerCircleCenterPositionX;
    private int innerCircleCenterPositionY;

    private final int outerCircleRadius;
    private final int innerCircleRadius;

    private Paint outerCirclePaint;
    private Paint innerCirclePaint;

    private double joystickCenterToTouchDistance;
    private boolean isPressed;
    private double actuatorX;
    private double actuatorY;


    public Joystick(int centerPostionX, int centerPositionY, int outerCircleRadius, int innerCircleRadius){
        //inner & outer joystick
        outerCircleCenterPositionX = centerPostionX;
        outerCircleCenterPositionY = centerPositionY;
        innerCircleCenterPositionX = centerPostionX;
        innerCircleCenterPositionY = centerPositionY;

        //circle radius
        this.outerCircleRadius = outerCircleRadius;
        this.innerCircleRadius = innerCircleRadius;

        //paint of circles
        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(Color.GRAY);
        outerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(Color.BLUE);
        innerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    public void draw(Canvas canvas){
        //Inner circle
        canvas.drawCircle(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                outerCircleRadius,
                outerCirclePaint
                );

        //Outer Circle
        canvas.drawCircle(
                innerCircleCenterPositionX,
                innerCircleCenterPositionY,
                innerCircleRadius,
                innerCirclePaint
                );


    }

    public void update() {
        updateInnerCirclePosition();

    }

    private void updateInnerCirclePosition() {
        innerCircleCenterPositionX = (int)  (outerCircleCenterPositionX + actuatorX*outerCircleRadius);
        innerCircleCenterPositionY = (int)  (outerCircleCenterPositionY + actuatorY*outerCircleRadius);
    }

    public boolean isPressed(double touchPostionX, double touchPostionY) {
        joystickCenterToTouchDistance = Utils.getDistanceBetweenPoints(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                touchPostionX,
                touchPostionY
        );
        return joystickCenterToTouchDistance < outerCircleRadius;
    }
    public boolean setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
        return true;
    }

    public boolean getIsPressed(){
        return isPressed;
    }
    public void setActuator(double touchPositionX, double touchPositionY){
        double deltaX = touchPositionX - outerCircleCenterPositionX;
        double deltaY = touchPositionY - outerCircleCenterPositionY;
        double deltaDistance = Utils.getDistanceBetweenPoints(0,0, deltaX, deltaY);

        if(deltaDistance < outerCircleRadius){
            actuatorX = deltaX/outerCircleRadius;
            actuatorY = deltaY/outerCircleRadius;
        }else {
            actuatorX = deltaX/deltaDistance;
            actuatorY = deltaY/deltaDistance;
        }
    }
    public void resetActuator(){
        actuatorX = 0.0;
        actuatorY = 0.0;
    }

    public double getActuatorX() {
        return actuatorX;
    }
    public double getActuatorY() {
        return actuatorY;
    }
}
