package com.sinulog.gamedev.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sinulog.gamedev.gameobject.Circle;
import com.sinulog.gamedev.gameobject.Enemy;
import com.sinulog.gamedev.gameobject.Player;
import com.sinulog.gamedev.gameobject.Spell;
import com.sinulog.gamedev.gamepanel.Joystick;
import com.sinulog.gamedev.gamepanel.GameOver;
import com.sinulog.gamedev.gamepanel.Performance;
import com.sinulog.gamedev.graphics.Animator;
import com.sinulog.gamedev.graphics.Sprite;
import com.sinulog.gamedev.graphics.SpriteSheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Game manages objects in the game
 * update all states and render
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {
    private final Player player;
    private GameLoop gameLoop;
    private Joystick joystick;
    private List<Enemy> enemyList = new ArrayList<>();
    private List<Spell> spellList = new ArrayList<>();
    private int joystickPointerID = -1;
    private int numberOfSpellsToCast = 0;
    private GameOver gameOver;
    private Performance performance;
    private GameDisplay gameDisplay;


    public Game(Context context) {
        super(context);

        //Surface holder
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameLoop = new GameLoop(this, surfaceHolder);
        setFocusable(true);

        // Initialize Game Panels
        performance = new Performance(context, gameLoop);
        gameOver = new GameOver(context);
        joystick = new Joystick(275, 700,70,40);

        //Initialize Game objects
        SpriteSheet spriteSheet = new SpriteSheet(context);
        Sprite[] spriteRight = spriteSheet.getPlayerSpriteArray(true);  //facing right
        Sprite[] spriteLeft = spriteSheet.getPlayerSpriteArray(false); //facingleft

        Animator animator = new Animator(spriteLeft,spriteRight);
        player = new Player(getContext(),joystick,  500,500, 30, animator);

        //Initialize display and center aroudn the player
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        gameDisplay = new GameDisplay(displayMetrics.widthPixels, displayMetrics.heightPixels, player);

        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            // Get the action index and pointer ID for all event types
            int actionIndex = event.getActionIndex();
            int pointerId = event.getPointerId(actionIndex);
            int action = event.getActionMasked();
            
            switch(action){
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    // Check if touch is on joystick area - only if joystick isn't already pressed
                    if(!joystick.getIsPressed() && 
                       joystick.isPressed((double)event.getX(actionIndex), (double)event.getY(actionIndex))){
                        joystickPointerID = pointerId;
                        joystick.setIsPressed(true);
                    } else {
                        // Touch outside joystick - cast spell
                        numberOfSpellsToCast++;
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    // Process all pointers in move events
                    if(joystick.getIsPressed()) {
                        // Find our joystick pointer in the move event
                        int joystickPointerIndex = event.findPointerIndex(joystickPointerID);
                        if (joystickPointerIndex >= 0 && joystickPointerIndex < event.getPointerCount()) {
                            // Update joystick with the position of only the pointer that's controlling it
                            joystick.setActuator(
                                (double)event.getX(joystickPointerIndex), 
                                (double)event.getY(joystickPointerIndex)
                            );
                        }
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    // Only process this event if it's the joystick pointer
                    if (pointerId == joystickPointerID){
                        joystick.setIsPressed(false);
                        joystick.resetActuator();
                        joystickPointerID = -1;
                    }
                    return true;
                    
                case MotionEvent.ACTION_CANCEL:
                    // Always reset joystick on cancel
                    joystick.setIsPressed(false);
                    joystick.resetActuator();
                    joystickPointerID = -1;
                    return true;
            }
        } catch (Exception e) {
            Log.e("Game", "Error in touch event: " + e.getMessage(), e);  // Add stack trace for better debugging
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(gameLoop.getState().equals(Thread.State.TERMINATED)){
            SurfaceHolder surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            gameLoop = new GameLoop(this,surfaceHolder);
        }
        gameLoop.startLoop();


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {


    }

    @Override
    public void draw (Canvas canvas){
        super.draw(canvas);
        //performance.drawUPS(canvas);
        //performance.drawFPS(canvas);
        //Draw game

        player.draw(canvas, gameDisplay);

        for (Enemy enemy : enemyList) {
            enemy.draw(canvas, gameDisplay);
        }

        for (Spell spell : spellList) {
            spell.draw(canvas, gameDisplay);
        }

        //Draw game panels
        joystick.draw(canvas);
        performance.draw(canvas);


        // Game over condition -> if player dead (HP=0) = GAME OVER!
        if (player.getHealthPoints() <= 0) {
            gameOver.draw(canvas);
        }
    }


    public void update() {
        // Stop update if HP <= 0;
        if (player.getHealthPoints() <= 0) {
            return;
        }

        try {
            // Update game components
            joystick.update();
            player.update();

            // Handle spell creation
            for (int i = 0; i < numberOfSpellsToCast; i++) {
                spellList.add(new Spell(getContext(), player));
            }
            numberOfSpellsToCast = 0;  // Reset counter after creating all spells
            
            // Spawn enemy if needed
            if (Enemy.readyToSpawn(player, enemyList)) {
                enemyList.add(new Enemy(getContext(), player));
            }

            // Update enemies
            Iterator<Enemy> enemyIterator = enemyList.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                if (enemy == null) {
                    enemyIterator.remove();
                    continue;
                }
                enemy.update();
            }

            // Update spells
            Iterator<Spell> spellIterator = spellList.iterator();
            while (spellIterator.hasNext()) {
                Spell spell = spellIterator.next();
                if (spell == null) {
                    spellIterator.remove();
                    continue;
                }
                spell.update();
            }

            // Handle collisions and update display
            handleCollisions();
            gameDisplay.update();
            
        } catch (Exception e) {
            Log.e("Game", "Error in update: " + e.getMessage(), e);
        }
    }

    private void handleCollisions() {
        try {
            // Check for null collections first
            if (enemyList == null || spellList == null) {
                return;
            }
            
            // Iterate enemylist for collision and enemy to player & spells to enemy
            Iterator<Enemy> iteratorEnemy = enemyList.iterator();
            while (iteratorEnemy.hasNext()) {
                Enemy enemy = iteratorEnemy.next();
                if (enemy == null) continue;

                // Player collision on enemy
                if (Circle.isColliding(enemy, player)){
                    //remove enemy if it collides
                    enemy.takeDamage(1);
                    player.setHealthPoints(player.getHealthPoints() -1);
                    if (enemy.getEnemyHp() <= 0) {
                        iteratorEnemy.remove();
                    }
                    continue;
                }

                Iterator<Spell> iteratorSpell = spellList.iterator();
                while (iteratorSpell.hasNext()) {
                    Spell spell = iteratorSpell.next();
                    if (spell == null) continue;
                    
                    //remove enemy if it collides with a spell
                    if (Circle.isColliding(spell, enemy)){
                        iteratorSpell.remove();
                        enemy.takeDamage(1);

                        if (enemy.getEnemyHp() <= 0){
                            iteratorEnemy.remove();
                        }
                        break;
                    }
                }
            }
            
            gameDisplay.update();
        } catch (Exception e) {
            Log.e("Game", "Error in handleCollisions: " + e.getMessage(), e);
        }
        gameDisplay.update();
    }
    public void pause() {
        gameLoop.stopLoop();
    }
}
