package com.sinulog.gamedev.graphics;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap;

public class Sprite {
    private final SpriteSheet spriteSheet;
    private final Rect rect;
    private boolean facingRight;

    public Sprite(SpriteSheet spriteSheet, Rect rect, boolean facingRight) {
        this.spriteSheet = spriteSheet;
        this.rect = rect;
        this.facingRight = facingRight;
    }

    public void draw (Canvas canvas, int x, int y) {
        canvas.drawBitmap(
                spriteSheet.getBitmap(facingRight),
                rect,
                new Rect (x, y, x + getWidth(), y + getHeight()),
                null
        );
    }
    public int getWidth(){
        return rect.width();
    }

    public int getHeight() {
        return rect.height();
    }
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
}
