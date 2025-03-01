package com.sinulog.gamedev.game;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.sinulog.gamedev.gamepanel.GamePanel;

public class MainActivity extends Activity {

    private Game game;
    private GamePanel gamePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity.java", "onCreate()");
        super.onCreate(savedInstanceState);

        //Set fullscreen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Gamepanel
        gamePanel = new GamePanel(this);
        setContentView(gamePanel);
    }

    public void startGame(){
        game = new Game(this);
        setContentView(game);
    }

    @Override
    protected void onStart() {
        Log.d("MainActivity.java", "onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("MainActivity.java", "onResume()");
        if (gamePanel != null) {
            gamePanel.resume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (game != null) {
            Log.d("MainActivity.java", "onPause()");
            if (gamePanel != null) {
                gamePanel.pause();
            }
            if (game != null) {
                game.pause();
            }
            super.onPause();
        }
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity.java", "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity.java", "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d("MainActivity.java", "onBackPressed()");
        //super.onBackPressed();
        // ^^^^^ commented out to prevent back press action
    }
}