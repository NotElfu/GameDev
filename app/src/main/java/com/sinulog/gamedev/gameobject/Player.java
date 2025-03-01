package com.sinulog.gamedev.gameobject;

import android.content.Context;
import android.graphics.Canvas;

import androidx.core.content.ContextCompat;

import com.sinulog.gamedev.game.GameDisplay;
import com.sinulog.gamedev.game.GameLoop;
import com.sinulog.gamedev.game.Utils;
import com.sinulog.gamedev.gamepanel.Healthbar;
import com.sinulog.gamedev.gamepanel.Joystick;
import com.sinulog.gamedev.R;
import com.sinulog.gamedev.graphics.Animator;
import com.sinulog.gamedev.graphics.Sprite;

public class Player extends Circle {
    public static final double SPEED_PIXELS_PER_SECOND = 400;
    public static final int MAX_HEALTH_POINTS = 5;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private Joystick joystick;
    private Healthbar healthbar;
    private int healthPoints;
    private Animator animator;
    private PlayerState playerState;



    public Player(Context context, Joystick joystick, double positionX, double positionY, double radius, Animator animator) {
        super(context, ContextCompat.getColor(context, R.color.player), positionX, positionY, radius);
        this.joystick = joystick;
        this.healthbar = new Healthbar(context,this);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.animator = animator;
        this.playerState = new PlayerState(this);
    }


    public void update() {
        //Velocity based on the joystick
        velocityX = joystick.getActuatorX() * MAX_SPEED;
        velocityY = joystick.getActuatorY() * MAX_SPEED;

        //Update position
        positionX += velocityX;
        positionY += velocityY;

        //Update direction of player
        if (velocityX != 0 || velocityY != 0 ) {
            //Normalize velocity to get direction ( unit vector of velocity)
            double distance = Utils.getDistanceBetweenPoints (0,0,velocityX, velocityY);
            directionX = velocityX / distance;
            directionY = velocityY / distance;
        }

        //Update player state
        playerState.update();

    }
    public void draw (Canvas canvas, GameDisplay gameDisplay) {
        //super.draw (canvas, gameDisplay);
        animator.draw(canvas, gameDisplay, this);

        healthbar.draw(canvas, gameDisplay);
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        if (healthPoints >= 0) {
            this.healthPoints = healthPoints;
        }
    }
    public PlayerState getPlayerState(){
        return playerState;
    }
}
