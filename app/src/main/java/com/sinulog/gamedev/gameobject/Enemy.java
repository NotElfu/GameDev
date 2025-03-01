package com.sinulog.gamedev.gameobject;

//moves on the direction of the player

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.sinulog.gamedev.game.GameDisplay;
import com.sinulog.gamedev.game.GameLoop;
import com.sinulog.gamedev.R;

import java.util.List;

public class Enemy extends Circle {
    private static final double SPEED_PIXELS_PER_SECOND = Player.SPEED_PIXELS_PER_SECOND*0.6;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private static double SPAWNS_PER_MINUTE = 20;
    private static double SPAWNS_PER_SECOND = SPAWNS_PER_MINUTE/60.0;
    private static double UPDATES_PER_SPAWN = GameLoop.MAX_UPS/SPAWNS_PER_SECOND;
    private static double updatesUntilNextSpawn = UPDATES_PER_SPAWN;

    // New code line 2/27 game improvement SPAWN and COLLISION
    private static final double MIN_SPAWN_DISTANCE = 600;
    private static final double SPAWN_RADIUS = 500;

    private Player player;

    //Variable for switching
    private Bitmap[] enemyFrames = new Bitmap[4];
    private boolean isMovingLeft = false;
    private int currentFrame = 0;
    private int frameSwitchCounter = 0;
    private static final int SWITCH_FRAME_EVERY = 10; //Frames per updates
    public static final int ENEMY_HEALTH_POINTS = 1;
    private int EnemyHP;


    public Enemy(Context context, Player player, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.enemy), positionX, positionY, radius);
        // Enemy Hp
        this.EnemyHP = ENEMY_HEALTH_POINTS;
        // Enemy sprite frames cause not in ArrayList :P
        enemyFrames[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy_frame1);
        enemyFrames[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy_frame2);
        enemyFrames[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy_frame3);
        enemyFrames[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy_frame4);
    }

    public Enemy(Context context, Player player) {
        super(
                context,
                ContextCompat.getColor(context, R.color.enemy),
                Math.random()*10000,
                Math.random()*10000,
                30
        );
        this.player = player;
    }

    public void takeDamage(int damage) {

    }

    private void destroy() {
    }

    public int getEnemyHp() {
        return EnemyHP;
    }



    public static boolean readyToSpawn(Player player, List<Enemy> enemyList) {
        if (updatesUntilNextSpawn <= 0){
            updatesUntilNextSpawn += UPDATES_PER_SPAWN;

            double angle = Math.random() * 2 * Math.PI;
            double spawnDistance = MIN_SPAWN_DISTANCE + Math.random() * (SPAWN_RADIUS - MIN_SPAWN_DISTANCE);

            // Spawning enemy not to close to each others
            double spawnX = player.getPositionX() + (Math.cos(angle)) * spawnDistance;
            double spawnY = player.getPositionY() + (Math.sin(angle)) * spawnDistance;

            if (isPositionValid(enemyList, spawnX, spawnY)){
                return true;
            }

        }else {
            updatesUntilNextSpawn --;
        }
        return false;
    }

    //Enemy to enemy collision
    private static boolean isPositionValid(List<Enemy> enemyList, double spawnX, double spawnY) {
        if (Double.isNaN(spawnX) || Double.isNaN(spawnY)) {
            Log.e("EnemySpawn", "Invalid spawn position");
            return false;
        }

        for (Enemy enemy : enemyList) {
            double distance = GameObject.getDistanceBetweenObjects(enemy, spawnX, spawnY);
            if (distance < MIN_SPAWN_DISTANCE) {
                Log.d("EnemySpawn", "Spawn too close to another enemy! Distance: " + distance);
                return false;
            }
        }
        return true;
    }

    @Override
    public void update() {
        //Calculate Player X & Y
        double distanceToPlayerX = player.getPositionX() - positionX;
        double distanceToPlayerY = player.getPositionY() - positionY;

        //Distance from Enemy to Player
        double distanceToPlayer = GameObject.getDistanceBetweenObjects (this,player);

        //Direction of Enemy to Player
        double directionX = distanceToPlayerX / distanceToPlayer;
        double directionY = distanceToPlayerY / distanceToPlayer;

        //Enemy movement
        isMovingLeft = directionX<0;

        //Velocity to the direction of the player
        if (distanceToPlayer > 0){
            velocityX = directionX*MAX_SPEED;
            velocityY = directionY*MAX_SPEED;
        }else {
            velocityX = 0;
            velocityY = 0;
        }

        positionX += velocityX;
        positionY += velocityY;

        //Logic for frame switching
        if (frameSwitchCounter >= SWITCH_FRAME_EVERY)  {
            currentFrame = (currentFrame +1) % 2;
            frameSwitchCounter = 0;
        }
    }


    @Override
    public void draw (Canvas canvas, GameDisplay gameDisplay) {
        Bitmap currentSprite;
        if (isMovingLeft) {
            currentSprite = enemyFrames[currentFrame];
        }else {
            currentSprite = enemyFrames[currentFrame + 2];
        }

        canvas.drawBitmap(
                currentSprite,
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX) - currentSprite.getWidth() / 2,
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY) - currentSprite.getHeight() /2,
                paint
        );
    }
}
