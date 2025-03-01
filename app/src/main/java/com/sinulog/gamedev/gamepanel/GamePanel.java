package com.sinulog.gamedev.gamepanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.sinulog.gamedev.R;
import com.sinulog.gamedev.game.MainActivity;

import java.util.Objects;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private Context context;
    private final SurfaceHolder holder;
    private volatile boolean isRunning;
    private Rect startButton, tutorialButton, helpButton;
    private Paint buttonPaint, textPaint;
    private boolean gameStarted = false;
    private boolean gameTutorial = false;
    private boolean gameHelp = false;
    private Thread gameThread;
    public int screenWidth;
    public int screenHeight;
    private MainActivity mainActivity;
    private Bitmap Background;

    public GamePanel(Context context) {
        super(context);
        this.context = context;

        //holder and callback OH MY GOD
        holder = getHolder();
        holder.addCallback(this);

        //Screen dimensions
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        //Set focusable
        setFocusable(true);

        //Background Image
        loadAndScaleBackground();

        // button size
        int buttonWidth = screenWidth / 5;
        int buttonHeight = screenHeight / 10;
        int buttonX = (screenWidth - buttonWidth) / 2;

        int firstButtonY = screenHeight / 5;
        int spacing = screenHeight / 7;

        //position of button
        int startButtonY = firstButtonY;
        int tutorialButtonY = startButtonY + buttonHeight + spacing;
        int helpButtonY = tutorialButtonY + buttonHeight + spacing;



        startButton = new Rect(
                buttonX,
                startButtonY,
                buttonX + buttonWidth,
                startButtonY + buttonHeight);

        tutorialButton = new Rect(
                buttonX,
                tutorialButtonY,
                buttonX + buttonWidth,
                tutorialButtonY + buttonHeight);

        helpButton = new Rect(
                buttonX,
                helpButtonY,
                buttonX + buttonWidth,
                helpButtonY + buttonHeight);


        // Button colors
        buttonPaint = new Paint();
        buttonPaint.setColor(ContextCompat.getColor(context, R.color.black));

        // Text style
        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(context, R.color.white));
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void loadAndScaleBackground() {
        try {
            Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.backgroundpanel);

            if (originalBitmap != null) {
                Background = Bitmap.createScaledBitmap(
                        originalBitmap,
                        screenWidth,
                        screenHeight,
                        true
                );

                if (originalBitmap != Background) {
                    originalBitmap.recycle();
                }
            } else {
                Log.e("GamePanel", "Cant load BG image :(");
            }
        } catch (Exception e) {
            Log.e("GamePanel", "Error loading BG image :(" + e.getMessage());
        }
    }

    public void resume() {
        isRunning = true;
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    update();
                    draw();
                }
            }
        });
        gameThread.start();
    }

    public void pause() {
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void update() {
        // Update game state here
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            // Draw background
            if (Background != null) {
                canvas.drawBitmap(Background, 0, 0, null);
            }

            if (!gameStarted) {
                drawMenu(canvas);
            } else if (gameTutorial) {
                drawTutorial(canvas);
            } else if (gameHelp) {
                drawHelp(canvas);
            }
        }
    }


    private void draw() {
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            if (canvas != null) {
                draw(canvas);
            }
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    // Surface Callback Methods
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        resume();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes if needed
    }

    @Override
    public void surfaceDestroyed (@NonNull SurfaceHolder holder) {
        pause();
        if (Background != null) {
            Background.recycle();
            Background = null;
        }
    }


    private void drawMenu(Canvas canvas) {
        // Draw buttons
        canvas.drawRect(startButton, buttonPaint);
        canvas.drawRect(tutorialButton, buttonPaint);
        canvas.drawRect(helpButton, buttonPaint);

        // Draw button text
        canvas.drawText("Start Game", startButton.centerX(), startButton.centerY(), textPaint);
        canvas.drawText("Tutorial", tutorialButton.centerX(), tutorialButton.centerY(), textPaint);
        canvas.drawText("Help", helpButton.centerX(), helpButton.centerY(), textPaint);
    }

    private void drawTutorial(Canvas canvas) {
        canvas.drawText("Tutorial: Move with Joystick, Attack with Tap", 540, 500, textPaint);
    }

    private void drawHelp(Canvas canvas) {
        canvas.drawText("Help: Avoid enemies, use spells to attack!", 540, 500, textPaint);
    }

    public boolean handleTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            if (startButton.contains(x, y)) {
                gameStarted = true; //Start the game class
                if (context instanceof MainActivity) {
                    ((MainActivity) context).startGame();
                }
                } else if (tutorialButton.contains(x, y)) {
                    showTutorialPopup();
                } else if (helpButton.contains(x, y)) {
                    showHelpPopup();
                }
            }
            return true;
        }

        private void showTutorialPopup() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.popup_tutorial, null);
            builder.setView(view);

            // Get Views
            ImageView tutorialImage = view.findViewById(R.id.tutorialImage);
            TextView tutorialText = view.findViewById(R.id.tutorialText);
            Button closeButton = view.findViewById(R.id.closeButton);

            // Set content
            tutorialImage.setImageResource(R.drawable.tutorial_image);
            tutorialText.setText("Move with the joystick, attack with tap!");

            // Create and show dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            // Close button dismisses the dialog
            closeButton.setOnClickListener(v -> dialog.dismiss());
        }

        private void showHelpPopup() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Help").setMessage("Avoid enemies, use spells to attack!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();

            // Customizing the title and message text color
            Objects.requireNonNull(dialog.getWindow()).getDecorView().setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
        }
        public boolean isGameStarted () {
            return gameStarted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return handleTouch(event);
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Log.d("Gamepanel", "drawingpleasework");
    }

}

