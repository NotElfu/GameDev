package com.sinulog.gamedev.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.sinulog.gamedev.game.GameLoop;
import com.sinulog.gamedev.R;

public class Performance {
    private GameLoop gameLoop;
    private Context context;

    public Performance (Context context, GameLoop gameLoop) {
        this.context = context;
        this.gameLoop = gameLoop;

    }

    public void draw (Canvas canvas) {
        drawUPS(canvas);
        drawFPS (canvas);
    }
    public void drawFPS (Canvas canvas){
        String averageFPS = Double.toString(gameLoop.getAverageFPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(25);
        canvas.drawText("FPS: " + averageFPS, 100,100,paint);
    }
    public void drawUPS (Canvas canvas){
        String averageUPS = Double.toString(gameLoop.getAverageUPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(25);
        canvas.drawText("UPS: " + averageUPS, 100,200,paint);
    }
}
