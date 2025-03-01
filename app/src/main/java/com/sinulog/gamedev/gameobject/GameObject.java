package com.sinulog.gamedev.gameobject;

import android.graphics.Canvas;

import com.sinulog.gamedev.game.GameDisplay;

public abstract class GameObject  {
    protected double positionX;
    protected double positionY;
    protected double velocityX;
    protected double velocityY;
    protected double directionX = 1;
    protected double directionY ;


    public GameObject (double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    protected static double getDistanceBetweenObjects(Enemy enemy, double spawnX, double spawnY) {
        double distanceX = enemy.getPositionX() - spawnX;
        double distanceY = enemy.getPositionY() - spawnY;
        return Math.sqrt(distanceX * distanceX + distanceY * distanceY);
    }

    public abstract void draw (Canvas canvas, GameDisplay gameDisplay);
    public abstract void update();

    public double getPositionX(){return positionX;}
    public double getPositionY(){return positionY;}

    public double getDirectionX() {return directionX;}
    public double getDirectionY() {return directionY;}

    protected static double getDistanceBetweenObjects(GameObject obj1, GameObject obj2) {
        return Math.sqrt(
                Math.pow(obj2.getPositionX() - obj1.getPositionX(), 2) +
                Math.pow (obj2.getPositionY() - obj1.getPositionY(), 2)
        );
    }

}
